package com.github.ilms49898723.fluigi.placement.forcedirected;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.placement.drc.OverlapFixer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;

import java.util.*;

public class ForceDirectedPlacer extends BasePlacer {
    private Map<String, Integer> mLocked;
    private Map<String, Integer> mWeights;

    public ForceDirectedPlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
        mLocked = new HashMap<>();
        mWeights = new HashMap<>();
    }

    @Override
    public boolean placement() {
        return forceDirected();
    }

    private boolean forceDirected() {
        for (BaseComponent c : mSymbolTable.getComponents()) {
            String id = c.getIdentifier();
            if (mLocked.containsKey(id)) {
                continue;
            }
            fixSingleComponentPosition(id);
        }
        return true;
    }

    private void fixSingleComponentPosition(String id) {
        Point2D newPosition;
        List<BaseComponent> connectedComponents = new ArrayList<>();
        for (int i = 1; i <= mSymbolTable.get(id).getNumPorts(); i++) {
            if (!mSymbolTable.get(id).hasPort(i)) {
                continue;
            }
            DeviceComponent srcPort = new DeviceComponent(id, i);
            Set<DeviceEdge> connectedEdges = mDeviceGraph.edgesOf(srcPort);
            for (DeviceEdge itr : connectedEdges) {
                String toId;
                toId = (!mDeviceGraph.getEdgeSource(itr).equals(srcPort)) ? mDeviceGraph.getEdgeSource(itr).getIdentifier() : mDeviceGraph.getEdgeTarget(itr).getIdentifier();
                connectedComponents.add(mSymbolTable.get(toId));
            }
        }
        for (int i = 0; i < connectedComponents.size(); i++) {
            if (mWeights.containsKey(connectedComponents.get(i).getIdentifier())) {
                continue;
            }
            int weight = 0;
            for (int j = 1; j <= connectedComponents.get(i).getNumPorts(); j++) {
                if (!connectedComponents.get(i).hasPort(j)) {
                    continue;
                }

                DeviceComponent srcPort = new DeviceComponent(connectedComponents.get(i).getIdentifier(), j);
                if (mDeviceGraph.edgesOf(srcPort) != null) {
                    weight += 1;
                }
            }
            mWeights.put(connectedComponents.get(i).getIdentifier(), weight);
        }
        if (connectedComponents.size() != 1) {
            double s1 = 0, s2 = 0, r = 0;
            for (int i = 0; i < connectedComponents.size(); i++) {
                s1 += connectedComponents.get(i).getPositionX() * mWeights.get(connectedComponents.get(i).getIdentifier());
                s2 += connectedComponents.get(i).getPositionY() * mWeights.get(connectedComponents.get(i).getIdentifier());
                r += mWeights.get(connectedComponents.get(i).getIdentifier());
            }
            newPosition = new Point2D(s1 / r, s2 / r);
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
                        fixSingleComponentPosition(overlapComponents.get(i));
                    }
                } else {
                    Point2D p = OverlapFixer.findNewPosition(mSymbolTable.get(id), mSymbolTable, this.mParameters);
                    if (p == null) {
                        return;
                    }
                    mSymbolTable.get(id).setPosition(p);
                }
            }
        } else {
            mLocked.put(id, 1);
        }
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
