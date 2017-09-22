package com.github.ilms49898723.fluigi.placement;

import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;

public class MinCutInitialPlacer extends BasePlacer {
    private static final int NUM_CELLS_IN_A_SLOT = 4;

    public MinCutInitialPlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
    }

    @Override
    public boolean placement() {
        return false;
    }

    private boolean minCutPlacement(Point2D basePoint, Point2D size, int numComponents) {
        if (numComponents <= NUM_CELLS_IN_A_SLOT) {
            return placeCells();
        } else {
            // TODO do min-cut to partition components
            int numComponentsA = 1;
            int numComponentsB = 1;
            double rate = ((double) numComponentsA) / ((double) numComponentsA + numComponentsB);
            if (size.getX() < size.getY()) {
                Point2D newBasePointA = new Point2D(basePoint.getX(), basePoint.getY() * rate);
                Point2D newBasePointB = new Point2D(basePoint.getX(), basePoint.getY() * (1.0 - rate));
                Point2D newSizeA = new Point2D(size.getX(), size.getY() * rate);
                Point2D newSizeB = new Point2D(size.getX(), size.getY() * (1.0 - rate));
                boolean resultA = minCutPlacement(newBasePointA, newSizeA, numComponents);
                boolean resultB = minCutPlacement(newBasePointB, newSizeB, numComponents);
                return resultA && resultB;
            } else {
                Point2D newBasePointA = new Point2D(basePoint.getX() * rate, basePoint.getY());
                Point2D newBasePointB = new Point2D(basePoint.getX() * (1.0 - rate), basePoint.getY());
                Point2D newSizeA = new Point2D(size.getX() * rate, size.getY());
                Point2D newSizeB = new Point2D(size.getX() * (1.0 - rate), size.getY());
                boolean resultA = minCutPlacement(newBasePointA, newSizeA, numComponents);
                boolean resultB = minCutPlacement(newBasePointB, newSizeB, numComponents);
                return resultA && resultB;
            }
        }
    }

    private boolean placeCells() {
        return false;
    }
}
