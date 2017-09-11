package com.github.ilms49898723.fluigi.device.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceGraph {
    private Graph<DeviceComponent, DefaultEdge> mDeviceGraph;
    private List<DefaultEdge> mEdges;

    public DeviceGraph() {
        mDeviceGraph = new SimpleGraph<>(DefaultEdge.class);
        mEdges = new ArrayList<>();
    }

    public boolean addVertex(String identifier, int portNumber) {
        return mDeviceGraph.addVertex(new DeviceComponent(identifier, portNumber));
    }

    public boolean addEdge(String fromId, int fromPort, String toId, int toPort) {
        DefaultEdge edge = mDeviceGraph.addEdge(new DeviceComponent(fromId, fromPort), new DeviceComponent(toId, toPort));
        mEdges.add(edge);
        return (edge != null);
    }

    public List<DefaultEdge> getAllEdges() {
        return mEdges;
    }

    public Set<DefaultEdge> edgeSet() {
        return mDeviceGraph.edgeSet();
    }

    public DeviceComponent getEdgeSource(DefaultEdge edge) {
        return mDeviceGraph.getEdgeSource(edge);
    }

    public DeviceComponent getEdgeTarget(DefaultEdge edge) {
        return mDeviceGraph.getEdgeTarget(edge);
    }

    public void dump() {
        for (DefaultEdge edge : mEdges) {
            System.out.println(edge);
        }
    }
}
