package com.github.ilms49898723.fluigi.device.graph;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import java.util.Set;

public class GraphUtil {
    public static UndirectedGraph<String, GraphEdge> constructGraph(Graph<DeviceComponent, DeviceEdge> deviceGraph) {
        UndirectedGraph<String, GraphEdge> graph = new SimpleGraph<>(GraphEdge.class);
        for (DeviceComponent vertex : deviceGraph.vertexSet()) {
            if (!graph.containsVertex(vertex.getIdentifier())) {
                graph.addVertex(vertex.getIdentifier());
            }
        }
        for (DeviceEdge edge : deviceGraph.edgeSet()) {
            String vertexA = edge.getSource().getIdentifier();
            String vertexB = edge.getTarget().getIdentifier();
            if (!graph.containsEdge(vertexA, vertexB)) {
                graph.addEdge(vertexA, vertexB, new GraphEdge(vertexA, vertexB, 1));
            } else {
                graph.getEdge(vertexA, vertexB).increaseWeight();
            }
        }
        return graph;
    }

    public static UndirectedGraph<String, GraphEdge> constructSubGraph(Graph<String, GraphEdge> graph, Set<String> vertexSet) {
        UndirectedGraph<String, GraphEdge> result = new SimpleGraph<>(GraphEdge.class);
        for (String vertex : vertexSet) {
            result.addVertex(vertex);
        }
        for (GraphEdge edge : graph.edgeSet()) {
            if (vertexSet.contains(edge.getVertexA()) && vertexSet.contains(edge.getVertexB())) {
                String a = edge.getVertexA();
                String b = edge.getVertexB();
                if (!result.containsEdge(a, b)) {
                    result.addEdge(a, b, new GraphEdge(a, b, 1));
                } else {
                    result.getEdge(a, b).increaseWeight();
                }
            }
        }
        return result;
    }
}
