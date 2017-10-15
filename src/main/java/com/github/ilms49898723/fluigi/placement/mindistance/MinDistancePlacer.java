package com.github.ilms49898723.fluigi.placement.mindistance;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.PortDirection;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.placement.overlap.OverlapFixer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;

import java.util.*;

public class MinDistancePlacer extends BasePlacer {
    private Map<String, Integer> mLocked;
    private Map<String, Integer> mWeights;

    public MinDistancePlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
    }

    @Override
    public boolean placement() {
        return minDistance();
    }

    private boolean minDistance() {
        for (BaseComponent c : mSymbolTable.getComponents()) {
        	if (mLocked.containsKey(c.getIdentifier())) {
                continue;
            }
            if (!fixComponentPosition(c.getIdentifier())) {
                return false;
            }
        }

        return true;
    }

    private boolean fixComponentPosition(String id) {
        Point2D newPosition = Point2D.ZERO;//The new position
        Map<Integer, List<BaseComponent>> connectedComponents = new HashMap<>();
        Map<Integer, List<Point2D>> connectedPorts = new HashMap<>();

        //Get connected components
        int numOfCC = 0;
        for (int i = 1; i <= mSymbolTable.get(id).getNumPorts(); i++) {
            if (!mSymbolTable.get(id).hasPort(i)) {
                continue;
            }

            DeviceComponent src = new DeviceComponent(id, i);
            List<BaseComponent> result1 = new ArrayList<>();
            List<Point2D> result2 = new ArrayList<>();
            Set<DeviceEdge> connectedEdges = mDeviceGraph.edgesOf(src);

            if (!connectedEdges.isEmpty()) {
                for (DeviceEdge itr : connectedEdges) {
                    DeviceComponent target;
                    target = (mDeviceGraph.getEdgeSource(itr) != src) ? mDeviceGraph.getEdgeSource(itr) : mDeviceGraph.getEdgeTarget(itr);
                    result1.add(mSymbolTable.get(target.getIdentifier()));
                    Point2D desPort = mSymbolTable.get(target.getIdentifier()).getPort(target.getPortNumber());
                    result2.add(new Point2D(mSymbolTable.get(target.getIdentifier()).getPositionX() + desPort.getX(),
                            mSymbolTable.get(target.getIdentifier()).getPositionY() + desPort.getY()));
                    numOfCC += 1;
                }
            }

            connectedComponents.put(i, result1);
            connectedPorts.put(i, result2);
        }

        if(numOfCC != 1) {
        	//Find new position which distance is minimum
            newPosition = getMinimumDistancePoint(id, connectedPorts);
            mLocked.put(id, 1);

            List<String> overlapComponents = getOverlapComponents(id, newPosition);
            if (overlapComponents.isEmpty()) {
                mSymbolTable.get(id).setPosition(newPosition);
            } else {
                boolean isValid = true;
                for (int i = 0; i < overlapComponents.size(); i++) {
                    if (mLocked.containsKey(overlapComponents.get(i))) {
                        isValid = false;
                        break;
                    }
                }

                if (isValid) {
                    mSymbolTable.get(id).setPosition(newPosition);
                    for (int i = 0; i < overlapComponents.size(); i++) {
                        fixComponentPosition(overlapComponents.get(i));
                    }
                } else {
                    Point2D p = OverlapFixer.findNewPosition(mSymbolTable.get(id), mSymbolTable, this.mParameters);
                    if (p == null) {
                        return false;
                    }
                    mSymbolTable.get(id).setPosition(p);
                }
            }
        } else {
        	//Fix the position by x/y of connected port
        	for(int i = 1; i <= mSymbolTable.get(id).getNumPorts(); i++) {
        		if (!mSymbolTable.get(id).hasPort(i)) {
                    continue;
                }
        		
        		if(connectedPorts.get(i).size() != 0) {
        			switch(mSymbolTable.get(id).getPortDirection(i)) {
        			case LEFT:
        				mSymbolTable.get(id).setPosition(new Point2D(connectedPorts.get(i).get(0).getX(), mSymbolTable.get(id).getPositionY()));
                        break;
                    case RIGHT:
                    	mSymbolTable.get(id).setPosition(new Point2D(connectedPorts.get(i).get(0).getX(), mSymbolTable.get(id).getPositionY()));
                        break;
                    case TOP:
                    	mSymbolTable.get(id).setPosition(new Point2D(mSymbolTable.get(id).getPositionX(), connectedPorts.get(i).get(0).getY()));
                        break;
                    case BOTTOM:
                    	mSymbolTable.get(id).setPosition(new Point2D(mSymbolTable.get(id).getPositionX(), connectedPorts.get(i).get(0).getY()));
                        break;
        			}
        			break;
        		}
        	}
        	mLocked.put(id, 1);
        }

        return true;
    }

    private Point2D getMinimumDistancePoint(String id, Map<Integer, List<Point2D>> connectedPorts) {
        
        double x = 0, y = 0, total = 0;
        for (int i = 1; i <= mSymbolTable.get(id).getNumPorts(); i++) {
            if (!mSymbolTable.get(id).hasPort(i)) {
                continue;
            }

            for (int j = 0 ; j < connectedPorts.get(i).size() ; j++) {
            	x += connectedPorts.get(i).get(j).getX() - mSymbolTable.get(id).getPort(i).getX();
            	y += connectedPorts.get(i).get(j).getY() - mSymbolTable.get(id).getPort(i).getY();
            	total += 1;
            }
        }
        x /= total;
        y /= total;
        
        Point2D result = new Point2D(x, y);

        double costX = 0, costY = 0;
        for (int i = 1; i <= mSymbolTable.get(id).getNumPorts(); i++) {
            if (!mSymbolTable.get(id).hasPort(i)) {
                continue;
            }

            Point2D srcPort = new Point2D(result.getX() + mSymbolTable.get(id).getPort(i).getX(), result.getY() + mSymbolTable.get(id).getPort(i).getY());
            Point2D cost = calculateCost(srcPort, mSymbolTable.get(id).getPortDirection(i), connectedPorts.get(i));
            costX += cost.getX();
            costY += cost.getY();
        }


        return result;
    }

    private Point2D calculateCost(Point2D src, PortDirection dir, List<Point2D> connectedPorts) {
        int disX = 0, disY = 0;
        for (int i = 0; i < connectedPorts.size(); i++) {
            double x = connectedPorts.get(i).getX() - src.getX();
            double y = connectedPorts.get(i).getY() - src.getY();
            switch (dir) {
                case LEFT:
                    if (x > 0) {
                        disX += 2 * x;
                    } else {
                        disX += -1 * x;
                    }
                    disY += Math.abs(y);
                    break;
                case RIGHT:
                    if (x < 0) {
                        disX += -2 * x;
                    } else {
                        disX += x;
                    }
                    disY += Math.abs(y);
                    break;
                case TOP:
                    if (y > 0) {
                        disY += 2 * y;
                    } else {
                        disY += -1 * y;
                    }
                    disX += Math.abs(x);
                    break;
                case BOTTOM:
                    if (y < 0) {
                        disY += -2 * y;
                    } else {
                        disY += y;
                    }
                    disX += Math.abs(x);
                    break;
            }
        }

        return (new Point2D(disX, disY));
    }

    private List<String> getOverlapComponents(String id, Point2D newPt) {
        List<String> result = new ArrayList<>();
        int w1 = mSymbolTable.get(id).getWidth();
        int h1 = mSymbolTable.get(id).getHeight();

        for (int i = 0; i < mSymbolTable.getComponents().size(); i++) {
            if (id.equals(mSymbolTable.getComponents().get(i).getIdentifier())) {
                continue;
            }

            int w2 = mSymbolTable.getComponents().get(i).getWidth();
            int h2 = mSymbolTable.getComponents().get(i).getHeight();
            double distanceX = Math.abs(mSymbolTable.getComponents().get(i).getPositionX() - newPt.getX());
            double distanceY = Math.abs(mSymbolTable.getComponents().get(i).getPositionY() - newPt.getY());

            if (distanceX <= (w1 + w2) / 2 && distanceY <= (h1 + h2) / 2) {
                result.add(mSymbolTable.getComponents().get(i).getIdentifier());
            }
        }

        return result;
    }
}
