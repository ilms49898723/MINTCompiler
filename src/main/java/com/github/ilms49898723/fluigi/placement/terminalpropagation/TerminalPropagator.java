package com.github.ilms49898723.fluigi.placement.terminalpropagation;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;

public class TerminalPropagator extends BasePlacer {
    public TerminalPropagator(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
    }

    @Override
    public boolean placement() {
        return propagation();
    }

    private boolean propagation() {
        rotatePropagation();
        swapPortPropagation();
        return true;
    }

    private void rotatePropagation() {
        for (BaseComponent component : mSymbolTable.getComponents()) {
            if (component.supportRotate()) {
                int[] costs = new int[4];
                costs[0] = getCost(component);
                component.rotate();
                costs[1] = getCost(component);
                component.rotate();
                costs[2] = getCost(component);
                component.rotate();
                costs[3] = getCost(component);
                component.rotate();
                int times = 0;
                for (int i = 1; i < 4; ++i) {
                    if (costs[i] < costs[times]) {
                        times = i;
                    }
                }
                for (int i = 0; i < times; ++i) {
                    component.rotate();
                }
            }
        }
    }

    private void swapPortPropagation() {
        for (DeviceEdge edge : mDeviceGraph.edgeSet()) {
            BaseComponent src = mSymbolTable.get(edge.getSource().getIdentifier());
            BaseComponent dst = mSymbolTable.get(edge.getTarget().getIdentifier());
            int srcPort = edge.getSource().getPortNumber();
            int dstPort = edge.getTarget().getPortNumber();
        }
    }

    private int getCost(BaseComponent component) {
        int cost = 0;
        for (int i = 1; i <= component.getNumPorts(); ++i) {
            DeviceComponent vertex = new DeviceComponent(component.getIdentifier(), i);
            for (DeviceEdge edge : mDeviceGraph.edgesOf(vertex)) {
                BaseComponent src = mSymbolTable.get(edge.getSource().getIdentifier());
                BaseComponent dst = mSymbolTable.get(edge.getTarget().getIdentifier());
                cost += Point2DUtil.manhattanDistance(src.getPort(edge.getSource().getPortNumber()), dst.getPort(edge.getTarget().getPortNumber()));
            }
        }
        return cost;
    }
}
