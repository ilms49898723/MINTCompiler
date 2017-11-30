package com.github.ilms49898723.fluigi.placement.mindistance;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.Valve;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.PortDirection;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.placement.drc.OverlapFixer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;

import java.util.*;

public class MinDistancePlacer extends BasePlacer {
    private Map<String, Integer> mLocked;
    private List<String> mOrder;

    public MinDistancePlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
        mLocked = new HashMap<>();
        mOrder = new PlacementOrder(symbolTable, deviceGraph).getStaticOrder();
    }

    @Override
    public boolean placement() {
        return minDistance();
    }

    private boolean minDistance() {
        if(mOrder == null) {
            for (BaseComponent c : mSymbolTable.getComponents(ComponentLayer.FLOW)) {
                if (mLocked.containsKey(c.getIdentifier())) {
                    continue;
                }
                if (!fixComponentPosition(c.getIdentifier())) {
                    return false;
                }
            }
        } else {
            for(int i = 0 ; i < mOrder.size() ; i++) {
                String id = mOrder.get(i);
                if (mLocked.containsKey(id)) {
                    continue;
                }
                if (!fixComponentPosition(id)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean fixComponentPosition(String id) {
        //System.out.println(">>>>>>>>>>fix " + id + "<<<<<<<<<<<<");
        Point2D newPosition = Point2D.ZERO;//The new position
        Map<Integer, List<BaseComponent>> connectedComponents = new HashMap<>();
        Map<Integer, List<Point2D>> connectedPorts = new HashMap<>();
        Map<Integer, List<Integer>> connectedPortsId = new HashMap<>();

        //Get connected components
        int numOfCC = 0;
        for (int i = 1; i <= mSymbolTable.get(id).getNumPorts(); i++) {
            if (!mSymbolTable.get(id).hasPort(i)) {
                continue;
            }

            DeviceComponent src = new DeviceComponent(id, i);
            List<BaseComponent> result1 = new ArrayList<>();
            List<Point2D> result2 = new ArrayList<>();
            List<Integer> result3 = new ArrayList<>();
            Set<DeviceEdge> connectedEdges = mDeviceGraph.edgesOf(src);

            if (!connectedEdges.isEmpty()) {
                for (DeviceEdge itr : connectedEdges) {
                    DeviceComponent target;
                    target = (!(mDeviceGraph.getEdgeSource(itr).equals(src))) ? mDeviceGraph.getEdgeSource(itr) : mDeviceGraph.getEdgeTarget(itr);
                    result1.add(mSymbolTable.get(target.getIdentifier()));
                    Point2D desPort = mSymbolTable.get(target.getIdentifier()).getPort(target.getPortNumber());
                    result2.add(new Point2D(desPort.getX(), desPort.getY()));
                    //System.out.println("--" + target.getIdentifier());
                    //System.out.println("--" + desPort.toString());
                    result3.add(target.getPortNumber());
                    numOfCC++;
                }
            }

            connectedComponents.put(i, result1);
            connectedPorts.put(i, result2);
            connectedPortsId.put(i, result3);
        }

        //System.out.println("old position: " + mSymbolTable.get(id).getPosition().toString());
        if(numOfCC != 1) {
            //Find new position which distance is minimum
            newPosition = getMinimumDistancePoint(id, connectedPorts);
            mLocked.put(id, 1);
            if(!fixOverlap(id, newPosition)) return false;
            //System.out.println("new position: " + mSymbolTable.get(id).getPosition().toString());
        } else {
            //Fix the position by x/y of connected port
            for(int i = 1; i <= mSymbolTable.get(id).getNumPorts(); i++) {
                if (!mSymbolTable.get(id).hasPort(i)) {
                    continue;
                }

                if(connectedPorts.get(i).size() != 0) {
                	int cof = 2;
                	double valvesWidth = 0;
                	DeviceComponent src = new DeviceComponent(id, i);
                    Set<DeviceEdge> connectedEdges = mDeviceGraph.edgesOf(src);
                    List<BaseComponent> valves = mSymbolTable.getValves();
                    for(int j = 0 ; j < valves.size() ; j++) {
                    	for(DeviceEdge itr : connectedEdges) {
                    		if(itr.getChannel() == ((Valve)valves.get(j)).getChannelId()) {
                    			valvesWidth += valves.get(j).getWidth();
                    		}
                    	}
                    }

                    while(cof*mParameters.getComponentSpacing() <= valvesWidth+mParameters.getChannelSpacing()) {
                    	cof++;
                    }

                	double keep = 0;
                	switch(mSymbolTable.get(id).getPortDirection(i)) {
                		case TOP:
                			keep = mSymbolTable.get(id).getHeight()/2;
                			break;
                		case BOTTOM:
                			keep = mSymbolTable.get(id).getHeight()/2;
                			break;
                		case LEFT:
                			keep = mSymbolTable.get(id).getWidth()/2;
                			break;
                		case RIGHT:
                			keep = mSymbolTable.get(id).getWidth()/2;
                			break;
                	}
                    switch(connectedComponents.get(i).get(0).getPortDirection(connectedPortsId.get(i).get(0))) {
                        case TOP:
                            newPosition = new Point2D(connectedPorts.get(i).get(0).getX(), connectedPorts.get(i).get(0).getY() - cof*mParameters.getComponentSpacing() - keep);
                            break;
                        case BOTTOM:
                            newPosition = new Point2D(connectedPorts.get(i).get(0).getX(), connectedPorts.get(i).get(0).getY() + cof*mParameters.getComponentSpacing() + keep);
                            break;
                        case LEFT:
                            newPosition = new Point2D(connectedPorts.get(i).get(0).getX() - cof*mParameters.getComponentSpacing() - keep, connectedPorts.get(i).get(0).getY());
                            break;
                        case RIGHT:
                            newPosition = new Point2D(connectedPorts.get(i).get(0).getX() + cof*mParameters.getComponentSpacing() + keep, connectedPorts.get(i).get(0).getY());
                            break;
                    }
                    break;
                }
            }
            //System.out.println("tmp position: " + newPosition.toString());
            mLocked.put(id, 1);

            double minCost = calculateAllCost(id, connectedPorts, newPosition);
            tryToRotate(id, connectedPorts, newPosition, minCost);
            newPosition = tryToSwap(id, connectedPorts, newPosition, minCost, true);
            //System.out.println("tmp 2nd position: " + newPosition.toString());
            if(!fixOverlap(id, newPosition)) return false;
            //System.out.println("new position: " + mSymbolTable.get(id).getPosition().toString());
        }

        return true;
    }

    private Point2D getMinimumDistancePoint(String id, Map<Integer, List<Point2D>> connectedPorts) {
        Point2D result = calculateMinimumDistancePoint(id, connectedPorts);
        double minCost = calculateAllCost(id, connectedPorts, result);

        tryToRotate(id, connectedPorts, result, minCost);
        result = tryToSwap(id, connectedPorts, result, minCost, false);
        return result;
    }

    private Point2D tryToSwap(String id, Map<Integer, List<Point2D>> connectedPorts, Point2D localPt, double localMin, boolean isOneConnect) {
        Point2D result = localPt;
        double minCost = localMin;

        List<Integer> swappablePorts = mSymbolTable.get(id).getSwappablePorts();
        int swap_i = -1, swap_j = -1;

        for (int i = 0 ; i < swappablePorts.size() ; i++) {
            for (int j = i+1 ; j < swappablePorts.size() ; j++) {

                mSymbolTable.get(id).swapPort(swappablePorts.get(i), swappablePorts.get(j), mParameters.getChannelSpacing());

                Point2D tmpPt = localPt;
                if(!isOneConnect) tmpPt = calculateMinimumDistancePoint(id, connectedPorts);
                double tmpCost = calculateAllCost(id, connectedPorts, tmpPt);
                if(tmpCost < minCost) {
                    minCost = tmpCost;
                    result = tmpPt;
                    swap_i = i;
                    swap_j = j;
                }

                mSymbolTable.get(id).swapPort(swappablePorts.get(j), swappablePorts.get(i), mParameters.getChannelSpacing());
            }
        }

        if(swap_i != -1 && swap_j != -1) mSymbolTable.get(id).swapPort(swappablePorts.get(swap_i), swappablePorts.get(swap_j), mParameters.getChannelSpacing());
        return result;
    }

    private void tryToRotate(String id, Map<Integer, List<Point2D>> connectedPorts, Point2D localPt, double localMin) {
        BaseComponent c = mSymbolTable.get(id);
        if (c.supportRotate()) {
            double[] costs = new double[4];
            costs[0] = localMin;
            c.rotate();
            costs[1] = calculateAllCost(id, connectedPorts, localPt);
            c.rotate();
            costs[2] = calculateAllCost(id, connectedPorts, localPt);
            c.rotate();
            costs[3] = calculateAllCost(id, connectedPorts, localPt);
            c.rotate();
            int times = 0;
            for (int i = 1; i < 4; ++i) {
                if (costs[i] < costs[times]) {
                    times = i;
                }
            }
            for (int i = 0; i < times; ++i) {
                c.rotate();
            }
        }
    }

    private Point2D calculateMinimumDistancePoint(String id, Map<Integer, List<Point2D>> connectedPorts) {
        double x = 0, y = 0, total = 0;
        for (int i = 1; i <= mSymbolTable.get(id).getNumPorts(); i++) {
            if (!mSymbolTable.get(id).hasPort(i)) {
                continue;
            }

            for (int j = 0 ; j < connectedPorts.get(i).size() ; j++) {
                x += connectedPorts.get(i).get(j).getX() - mSymbolTable.get(id).getPort(i).getX() + mSymbolTable.get(id).getPositionX();
                y += connectedPorts.get(i).get(j).getY() - mSymbolTable.get(id).getPort(i).getY() + mSymbolTable.get(id).getPositionY();
                total += 1;
            }
        }
        x /= total;
        y /= total;

        return new Point2D(x, y);
    }

    private double calculateAllCost(String id, Map<Integer, List<Point2D>> connectedPorts, Point2D position) {
        double costX = 0, costY = 0;
        for (int k = 1; k <= mSymbolTable.get(id).getNumPorts(); k++) {
            if (!mSymbolTable.get(id).hasPort(k)) {
                continue;
            }

            Point2D srcPort = new Point2D(position.getX() + mSymbolTable.get(id).getPort(k).getX(), position.getY() + mSymbolTable.get(id).getPort(k).getY());
            Point2D cost = calculateCost(srcPort, mSymbolTable.get(id).getPortDirection(k), connectedPorts.get(k));
            costX += cost.getX();
            costY += cost.getY();
        }
        return costX + costY;
    }

    private Point2D calculateCost(Point2D src, PortDirection dir, List<Point2D> connectedPorts) {
        int disX = 0, disY = 0;
        for (int i = 0; i < connectedPorts.size(); i++) {
            double x = connectedPorts.get(i).getX() - src.getX();
            double y = connectedPorts.get(i).getY() - src.getY();
            switch (dir) {
                case LEFT:
                    if (x > 0) {
                        disX += 3 * x;
                    } else {
                        disX += -1 * x;
                    }
                    disY += Math.abs(y);
                    break;
                case RIGHT:
                    if (x < 0) {
                        disX += -3 * x;
                    } else {
                        disX += x;
                    }
                    disY += Math.abs(y);
                    break;
                case TOP:
                    if (y > 0) {
                        disY += 3 * y;
                    } else {
                        disY += -1 * y;
                    }
                    disX += Math.abs(x);
                    break;
                case BOTTOM:
                    if (y < 0) {
                        disY += -3 * y;
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

        for (int i = 0; i < mSymbolTable.getComponents(ComponentLayer.FLOW).size(); i++) {
            String targetId = mSymbolTable.getComponents(ComponentLayer.FLOW).get(i).getIdentifier();

            if (id.equals(targetId)) continue;

            int w2 = mSymbolTable.get(targetId).getWidth();
            int h2 = mSymbolTable.get(targetId).getHeight();
            double distanceX = Math.abs(mSymbolTable.get(targetId).getPositionX() - newPt.getX());
            double distanceY = Math.abs(mSymbolTable.get(targetId).getPositionY() - newPt.getY());

            if (distanceX <= ((w1+w2)/2 + mParameters.getComponentSpacing()) && distanceY <= ((h1+h2)/2 + mParameters.getComponentSpacing())) {
                result.add(targetId);
                /*System.out.println("----------");
                System.out.println("OVERLAP:"+targetId);
                System.out.println(mSymbolTable.get(targetId).getPosition().toString());
                System.out.println("Width: " + mSymbolTable.get(targetId).getWidth());
                System.out.println("Height: " + mSymbolTable.get(targetId).getHeight());
                System.out.println("Distance: " + distanceX + "," + distanceY);
                System.out.println("Limit:" + ((w1+w2)/2 + mParameters.getComponentSpacing()) + ", " + ((h1+h2)/2 + mParameters.getComponentSpacing()));
                System.out.println("----------");*/
            }
        }

        return result;
    }

    private boolean fixOverlap(String id, Point2D newPosition) {
        List<String> overlapComponents = getOverlapComponents(id, newPosition);
        if (overlapComponents.isEmpty()) {
            mSymbolTable.get(id).setPosition(newPosition);
            //System.out.println("NO OVERLAP!!!!");
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
        return true;
    }
}
