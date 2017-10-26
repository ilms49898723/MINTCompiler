package com.github.ilms49898723.fluigi.placement.transformation;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;

public class TransformationPlacer extends BasePlacer {
    public TransformationPlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
    }

    @Override
    public boolean placement() {
        return doPlacement(ComponentLayer.FLOW);
    }

    public boolean placement(ComponentLayer layer) {
        return doPlacement(layer);
    }

    private boolean doPlacement(ComponentLayer layer) {
        double left = mParameters.getMaxDeviceWidth();
        double right = 0;
        double top = mParameters.getMaxDeviceHeight();
        double bottom = 0;
        for (BaseComponent component : mSymbolTable.getComponents(layer)) {
            for (Point2DPair point : component.getPoints()) {
                Point2D ptA = point.getPointA();
                Point2D ptB = point.getPointB();
                left = Math.min(left, ptA.getX());
                right = Math.max(right, ptB.getX());
                top = Math.min(top, ptA.getY());
                bottom = Math.max(bottom, ptB.getY());
            }
        }
        Point2D lt = new Point2D(left, top);
        Point2D rb = new Point2D(right, bottom);
        Point2D mid = lt.midpoint(rb);
        Point2D target = new Point2D(mParameters.getMaxDeviceWidth() / 2, mParameters.getMaxDeviceHeight() / 2);
        Point2D delta = target.subtract(mid);
        for (BaseComponent component : mSymbolTable.getComponents(layer)) {
            Point2D newPos = component.getPosition().add(delta);
            component.setPosition(newPos);
        }
        return true;
    }
}
