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
                String vertexA = itr.getSource().getIdentifier();
                String vertexB = itr.getTarget().getIdentifier();
                toId = (srcPort.getIdentifier().equals(vertexA)) ? vertexB : vertexA;
                connectedComponents.add(mSymbolTable.get(toId));
            }
        }
        for (BaseComponent connectedComponent : connectedComponents) {
            if (mWeights.containsKey(connectedComponent.getIdentifier())) {
                continue;
            }
            int weight = 0;
            for (int j = 1; j <= connectedComponent.getNumPorts(); j++) {
                if (!connectedComponent.hasPort(j)) {
                    continue;
                }

                DeviceComponent srcPort = new DeviceComponent(connectedComponent.getIdentifier(), j);
                if (mDeviceGraph.edgesOf(srcPort) != null) {
                    weight += 1;
                }
            }
            mWeights.put(connectedComponent.getIdentifier(), weight);
        }
        if (connectedComponents.size() != 1) {
            double s1 = 0, s2 = 0, r = 0;
            for (BaseComponent connectedComponent : connectedComponents) {
                s1 += connectedComponent.getPositionX() * mWeights.get(connectedComponent.getIdentifier());
                s2 += connectedComponent.getPositionY() * mWeights.get(connectedComponent.getIdentifier());
                r += mWeights.get(connectedComponent.getIdentifier());
            }
            newPosition = new Point2D(s1 / r, s2 / r);
            mLocked.put(id, 1);
            List<String> overlapComponents = getOverlapComponents(id, newPosition);
            if (overlapComponents.isEmpty()) {
                mSymbolTable.get(id).setPosition(newPosition);
            } else {
                boolean isValid = true;
                for (String component : overlapComponents) {
                    if (mLocked.containsKey(component)) {
                        isValid = false;
                        break;
                    }
                }
                if (isValid) {
                    mSymbolTable.get(id).setPosition(newPosition);
                    for (String overlapComponent : overlapComponents) {
                        fixSingleComponentPosition(overlapComponent);
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
