package com.github.ilms49898723.fluigi.placement.mincut;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.graph.GraphEdge;
import com.github.ilms49898723.fluigi.device.graph.GraphUtil;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.StoerWagnerMinimumCut;

import java.util.*;

public class MinCutInitialPlacer extends BasePlacer {
    private static final int NUM_CELLS_IN_A_SLOT = 4;

    private UndirectedGraph<String, GraphEdge> mMinCutGraph;

    public MinCutInitialPlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
        mMinCutGraph = GraphUtil.constructGraph(mDeviceGraph.getGraph());
    }

    @Override
    public boolean placement() {
        return minCutPlacement(mMinCutGraph, Point2D.ZERO, new Point2D(mParameters.getMaxDeviceWidth(), mParameters.getMaxDeviceHeight()));
    }

    private boolean minCutPlacement(UndirectedGraph<String, GraphEdge> graph, Point2D basePoint, Point2D size) {
        if (graph.vertexSet().size() <= NUM_CELLS_IN_A_SLOT) {
            return placeCells(graph, basePoint, size);
        } else {
            StoerWagnerMinimumCut<String, GraphEdge> minimumCut = new StoerWagnerMinimumCut<>(graph);
            Set<String> vertexA = minimumCut.minCut();
            Set<String> vertexB = new HashSet<>();
            vertexB.addAll(graph.vertexSet());
            vertexB.removeAll(vertexA);
            UndirectedGraph<String, GraphEdge> subGraphA = GraphUtil.constructSubGraph(graph, vertexA);
            UndirectedGraph<String, GraphEdge> subGraphB = GraphUtil.constructSubGraph(graph, vertexB);
            double rateA = getRate(vertexA, graph.vertexSet());
            double rateB = 1.0 - rateA;
            if (size.getX() < size.getY()) {
                Point2D newBasePointA = new Point2D(basePoint.getX(), basePoint.getY() * rateA);
                Point2D newBasePointB = new Point2D(basePoint.getX(), basePoint.getY() * rateB);
                Point2D newSizeA = new Point2D(size.getX(), size.getY() * rateA);
                Point2D newSizeB = new Point2D(size.getX(), size.getY() * rateB);
                boolean resultA = minCutPlacement(subGraphA, newBasePointA, newSizeA);
                boolean resultB = minCutPlacement(subGraphB, newBasePointB, newSizeB);
                return resultA && resultB;
            } else {
                Point2D newBasePointA = new Point2D(basePoint.getX() * rateA, basePoint.getY());
                Point2D newBasePointB = new Point2D(basePoint.getX() * rateB, basePoint.getY());
                Point2D newSizeA = new Point2D(size.getX() * rateA, size.getY());
                Point2D newSizeB = new Point2D(size.getX() * rateB, size.getY());
                boolean resultA = minCutPlacement(subGraphA, newBasePointA, newSizeA);
                boolean resultB = minCutPlacement(subGraphB, newBasePointB, newSizeB);
                return resultA && resultB;
            }
        }
    }

    private double getRate(Set<String> sub, Set<String> all) {
        double allArea = 0.0;
        double subArea = 0.0;
        for (String v : all) {
            BaseComponent component = mSymbolTable.get(v);
            allArea += component.getWidth() * component.getHeight();
            if (sub.contains(v)) {
                subArea += component.getWidth() * component.getHeight();
            }
        }
        return subArea / allArea;
    }

    private boolean placeCells(Graph<String, GraphEdge> graph, Point2D basePoint, Point2D size) {
        Point2D a = basePoint.add(new Point2D(size.getX() / 4, size.getY() / 4));
        Point2D b = basePoint.add(new Point2D(size.getX() * 3 / 4, size.getY() / 4));
        Point2D c = basePoint.add(new Point2D(size.getX() / 4, size.getY() * 3 / 4));
        Point2D d = basePoint.add(new Point2D(size.getX() * 3 / 4, size.getY() * 3 / 4));
        List<Point2D> mps = new ArrayList<>();
        mps.addAll(Arrays.asList(a, b, c, d));
        int counter = 0;
        for (String v : graph.vertexSet()) {
            BaseComponent component = mSymbolTable.get(v);
            component.setPosition(mps.get(counter));
            counter++;
        }
        return true;
    }
}
