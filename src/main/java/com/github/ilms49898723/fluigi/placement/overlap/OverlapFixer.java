package com.github.ilms49898723.fluigi.placement.overlap;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;


public class OverlapFixer extends BasePlacer {
    public OverlapFixer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
    }

    @Override
    public boolean placement() {
        for (int i = 0; i < mSymbolTable.getComponents().size(); ++i) {
            for (int j = i + 1; j < mSymbolTable.getComponents().size(); ++j) {
                BaseComponent a = mSymbolTable.getComponents().get(i);
                BaseComponent b = mSymbolTable.getComponents().get(j);
                if (Point2DUtil.isOverlapped(a, b, mParameters)) {
                    Point2D oriA = a.getPosition();
                    Point2D oriB = b.getPosition();
                    Point2D newA = findNewPosition(a, mSymbolTable, mParameters);
                    Point2D newB = findNewPosition(b, mSymbolTable, mParameters);
                    int costA = getCost(a);
                    a.setPosition(newA);
                    costA = getCost(a) - costA;
                    a.setPosition(oriA);
                    int costB = getCost(b);
                    b.setPosition(newB);
                    costB = getCost(b) - costB;
                    b.setPosition(oriB);
                    if (costA < costB) {
                        a.setPosition(newA);
                    } else {
                        b.setPosition(newB);
                    }
                }
            }
        }
        return true;
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

    public static Point2D findNewPosition(BaseComponent target, SymbolTable symbolTable, Parameters parameters) {
        int minDis = Integer.MAX_VALUE;
        Point2D result = Point2D.ZERO;
        int maxWidth = parameters.getMaxDeviceWidth();
        int maxHeight = parameters.getMaxDeviceHeight();
        Point2D originalPosition = target.getPosition();
        for (int j = 0; j < maxHeight; ++j) {
            for (int i = 0; i < maxWidth; ++i) {
                Point2D position = new Point2D(i, j);
                target.setPosition(position);
                if (isValidPosition(target, position, maxWidth, maxHeight, parameters.getComponentSpacing())) {
                    boolean hasOverlap = false;
                    for (BaseComponent component : symbolTable.getComponents()) {
                        if (target.equals(component)) {
                            continue;
                        }
                        if (Point2DUtil.isOverlapped(target, component, parameters)) {
                            hasOverlap = true;
                        }
                    }
                    if (!hasOverlap) {
                        if (Point2DUtil.manhattanDistance(originalPosition, position) < minDis) {
                            minDis = Point2DUtil.manhattanDistance(originalPosition, position);
                            result = new Point2D(i, j);
                        }
                    }
                }
            }
        }
        target.setPosition(originalPosition);
        if (minDis != Integer.MAX_VALUE) {
            return result;
        } else {
            return null;
        }
    }

    private static boolean isValidPosition(BaseComponent component, Point2D position, int maxWidth, int maxHeight, int spacing) {
        int x = (int) position.getX();
        int y = (int) position.getY();
        int w = component.getWidth();
        int h = component.getHeight();
        return (x - w / 2 - spacing >= 0 && x + w / 2 + spacing < maxWidth) &&
               (y - h / 2 - spacing >= 0 && y + h / 2 + spacing < maxHeight);
    }
}
