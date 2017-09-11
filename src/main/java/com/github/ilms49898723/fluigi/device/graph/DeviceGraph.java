package com.github.ilms49898723.fluigi.device.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.List;

public class DeviceGraph {
    private Graph<DeviceComponent, ComponentEdge> mDeviceGraph;
    private List<ComponentEdge> mEdges;

    public DeviceGraph() {
        mDeviceGraph = new SimpleGraph<>(ComponentEdge.class);
        mEdges = new ArrayList<>();
    }

    public boolean addVertex(String identifier, int portNumber) {
        return mDeviceGraph.addVertex(new DeviceComponent(identifier, portNumber));
    }

    public boolean addEdge(String fromId, int fromPort, String toId, int toPort) {
        ComponentEdge edge = mDeviceGraph.addEdge(new DeviceComponent(fromId, fromPort), new DeviceComponent(toId, toPort));
        mEdges.add(edge);
        return (edge != null);
    }

    public List<ComponentEdge> getAllEdges() {
        return mEdges;
    }

    public void dump() {
        for (ComponentEdge edge : mEdges) {
            System.out.println(edge);
        }
    }
}
