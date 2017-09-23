package com.github.ilms49898723.fluigi.device.graph;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;

public class GraphUtil {
    public static class FarthestPair {
        private Graph<String, GraphEdge> mGraph;
        private List<String> mVertices;
        private String mVertexA;
        private String mVertexB;

        public FarthestPair(Graph<String, GraphEdge> graph) {
            mGraph = graph;
            mVertices = new ArrayList<>(graph.vertexSet());
            mVertexA = breadthFirstSearch(mVertices.get(0));
            mVertexB = breadthFirstSearch(mVertexA);
        }

        public String getVertexA() {
            return mVertexA;
        }

        public String getVertexB() {
            return mVertexB;
        }

        private String breadthFirstSearch(String vertex) {
            Queue<String> queue = new ArrayDeque<>();
            Map<String, Integer> visited = new HashMap<>();
            for (String v : mVertices) {
                visited.put(v, -1);
            }
            queue.add(vertex);
            visited.put(vertex, 0);
            while (!queue.isEmpty()) {
                String front = queue.poll();
                for (GraphEdge edge : mGraph.edgesOf(front)) {
                    String out = (edge.getVertexA().equals(front)) ? edge.getVertexB() : edge.getVertexA();
                    if (visited.get(out) == -1) {
                        visited.put(out, visited.get(front) + 1);
                        queue.add(out);
                    }
                }
            }
            String result = "";
            int max = Integer.MIN_VALUE;
            for (String key : visited.keySet()) {
                if (visited.get(key) > max) {
                    max = visited.get(key);
                    result = key;
                }
            }
            return result;
        }
    }

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
