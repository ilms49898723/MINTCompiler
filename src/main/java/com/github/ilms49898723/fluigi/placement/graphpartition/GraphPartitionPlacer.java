package com.github.ilms49898723.fluigi.placement.graphpartition;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.graph.GraphEdge;
import com.github.ilms49898723.fluigi.device.graph.GraphUtil;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;
import org.jgrapht.Graph;

import java.util.*;

public class GraphPartitionPlacer extends BasePlacer {
    private static final int NUM_CELLS_IN_A_SLOT = 4;

    private Graph<String, GraphEdge> mGraph;

    public GraphPartitionPlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
        mGraph = GraphUtil.constructGraph(mDeviceGraph.getGraph());
    }

    @Override
    public boolean placement() {
        return graphPartition(mGraph, Point2D.ZERO, new Point2D(mParameters.getMaxDeviceWidth(), mParameters.getMaxDeviceHeight()));
    }

    private boolean graphPartition(Graph<String, GraphEdge> graph, Point2D basePoint, Point2D size) {
        if (graph.vertexSet().size() <= NUM_CELLS_IN_A_SLOT) {
            System.out.println(graph.vertexSet());
            return placeCells(graph, basePoint, size);
        } else {
            GraphUtil.FarthestPair farthestPair = new GraphUtil.FarthestPair(graph);
            String startA = farthestPair.getVertexA();
            int numVertex = graph.vertexSet().size();
            int limitA = numVertex / 2;
            Set<String> vertexA = breathFirstSearch(graph, startA, limitA);
            Set<String> vertexB = new HashSet<>();
            vertexB.addAll(graph.vertexSet());
            vertexB.removeAll(vertexA);
            Graph<String, GraphEdge> subGraphA = GraphUtil.constructSubGraph(graph, vertexA);
            Graph<String, GraphEdge> subGraphB = GraphUtil.constructSubGraph(graph, vertexB);
            double rateA = getRate(vertexA, graph.vertexSet());
            double rateB = 1.0 - rateA;
            if (size.getX() < size.getY()) {
                Point2D newBasePointA = new Point2D(basePoint.getX(), basePoint.getY());
                Point2D newBasePointB = new Point2D(basePoint.getX(), basePoint.getY() + size.getY() * rateA);
                Point2D newSizeA = new Point2D(size.getX(), size.getY() * rateA);
                Point2D newSizeB = new Point2D(size.getX(), size.getY() * rateB);
                boolean resultA = graphPartition(subGraphA, newBasePointA, newSizeA);
                boolean resultB = graphPartition(subGraphB, newBasePointB, newSizeB);
                return resultA && resultB;
            } else {
                Point2D newBasePointA = new Point2D(basePoint.getX(), basePoint.getY());
                Point2D newBasePointB = new Point2D(basePoint.getX() + size.getX() * rateA, basePoint.getY());
                Point2D newSizeA = new Point2D(size.getX() * rateA, size.getY());
                Point2D newSizeB = new Point2D(size.getX() * rateB, size.getY());
                boolean resultA = graphPartition(subGraphA, newBasePointA, newSizeA);
                boolean resultB = graphPartition(subGraphB, newBasePointB, newSizeB);
                return resultA && resultB;
            }
        }
    }

    private double getRate(Set<String> sub, Set<String> all) {
        double a = (double) sub.size();
        double b = (double) all.size();
        System.out.println(a / b);
        return a / b;
//        double allArea = 0.0;
//        double subArea = 0.0;
//        for (String v : all) {
//            BaseComponent component = mSymbolTable.get(v);
//            allArea += component.getWidth() * component.getHeight();
//            if (sub.contains(v)) {
//                subArea += component.getWidth() * component.getHeight();
//            }
//        }
//        return subArea / allArea;
    }

    private Set<String> breathFirstSearch(Graph<String, GraphEdge> graph, String start, int limit) {
        Set<String> result = new HashSet<>();
        int size = 0;
        Queue<String> queue = new ArrayDeque<>();
        queue.add(start);
        result.add(start);
        size++;
        while (!queue.isEmpty() && size < limit) {
            String front = queue.poll();
            for (GraphEdge edge : graph.edgesOf(front)) {
                String out = (edge.getVertexA().equals(front)) ? edge.getVertexB() : edge.getVertexA();
                if (!result.contains(out)) {
                    if (size < limit) {
                        result.add(out);
                        size++;
                        queue.add(out);
                    }
                }
            }
        }
        return result;
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
