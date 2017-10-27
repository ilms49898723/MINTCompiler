package com.github.ilms49898723.fluigi.placement.terminalpropagation;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;

import java.util.ArrayList;
import java.util.List;

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
        rotatePropagation(mSymbolTable.getComponents(ComponentLayer.FLOW));
    }

    public void rotatePropagation(List<BaseComponent> components) {
        for (BaseComponent component : components) {
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
        swapPortPropagation(mSymbolTable.getComponents(ComponentLayer.FLOW));
    }

    public void swapPortPropagation(List<BaseComponent> components) {
        for (DeviceEdge edge : mDeviceGraph.edgeSet()) {
            BaseComponent src = mSymbolTable.get(edge.getSource().getIdentifier());
            BaseComponent dst = mSymbolTable.get(edge.getTarget().getIdentifier());
            if (!(components.contains(src) || components.contains(dst))) {
                continue;
            }
            int srcPort = edge.getSource().getPortNumber();
            int dstPort = edge.getTarget().getPortNumber();
            int minCost = getCost(src, dst);
            int newSrcPort = srcPort;
            int newDstPort = dstPort;
            List<Integer> srcCandidates = new ArrayList<>();
            List<Integer> dstCandidates = new ArrayList<>();
            if (src.supportSwapPort(srcPort)) {
                srcCandidates.addAll(src.getSwappablePorts());
            } else {
                srcCandidates.add(srcPort);
            }
            if (dst.supportSwapPort(dstPort)) {
                dstCandidates.addAll(dst.getSwappablePorts());
            } else {
                dstCandidates.add(dstPort);
            }
            for (int srcPortSwap : srcCandidates) {
                for (int dstPortSwap : dstCandidates) {
                    src.swapPort(srcPort, srcPortSwap, mParameters.getChannelSpacing());
                    dst.swapPort(dstPort, dstPortSwap, mParameters.getChannelSpacing());
                    int cost = getCost(src, dst);
                    if (cost < minCost) {
                        minCost = cost;
                        newSrcPort = srcPortSwap;
                        newDstPort = dstPortSwap;
                    }
                    src.swapPort(srcPort, srcPortSwap, mParameters.getChannelSpacing());
                    dst.swapPort(dstPort, dstPortSwap, mParameters.getChannelSpacing());
                }
            }
            if (src.supportSwapPort(srcPort)) {
                src.swapPort(srcPort, newSrcPort, mParameters.getChannelSpacing());
            }
            if (dst.supportSwapPort(dstPort)) {
                dst.swapPort(dstPort, newDstPort, mParameters.getChannelSpacing());
            }
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

    private int getCost(BaseComponent vertexA, BaseComponent vertexB) {
        return getCost(vertexA) + getCost(vertexB);
    }
}
