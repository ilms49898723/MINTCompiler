package com.github.ilms49898723.fluigi.placement.overlap;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;

public class OverlapFixer {
    public static Point2D findNewPosition(BaseComponent target, SymbolTable symbolTable, Parameters parameters) {
        int minDis = Integer.MAX_VALUE;
        Point2D result = Point2D.ZERO;
        int maxWidth = parameters.getMaxDeviceWidth();
        int maxHeight = parameters.getMaxDeviceHeight();
        for (int j = 0; j < maxHeight; ++j) {
            for (int i = 0; i < maxWidth; ++i) {
                Point2D position = new Point2D(i, j);
                if (isValidPosition(target, position, maxWidth, maxHeight, parameters.getComponentSpacing())) {
                    boolean hasOverlap = false;
                    for (BaseComponent component : symbolTable.getComponents()) {
                        if (target.equals(component)) {
                            continue;
                        }
                        if (Point2DUtil.isOverlapped(target, component, parameters)) {
                            hasOverlap = true;
                            i += (int) Point2DUtil.calculateOverlap(target, component, parameters).getX();
                        }
                    }
                    if (!hasOverlap) {
                        if (Point2DUtil.manhattanDistance(target.getPosition(), position) < minDis) {
                            minDis = Point2DUtil.manhattanDistance(target.getPosition(), position);
                            result = new Point2D(i, j);
                        }
                    }
                }
            }
        }
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
