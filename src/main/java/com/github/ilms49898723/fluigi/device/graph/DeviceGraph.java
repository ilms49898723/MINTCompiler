package com.github.ilms49898723.fluigi.device.graph;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;

public class DeviceGraph {
    private Graph<DeviceComponent, DeviceEdge> mDeviceGraph;
    private Map<DeviceEdge, ComponentLayer> mEdgesLayer;

    public DeviceGraph() {
        mDeviceGraph = new SimpleGraph<>(DeviceEdge.class);
        mEdgesLayer = new HashMap<>();
    }

    public boolean addVertex(String identifier, int portNumber) {
        return mDeviceGraph.addVertex(new DeviceComponent(identifier, portNumber));
    }

    public boolean addEdge(String fromId, int fromPort, String toId, int toPort, String channelId, ComponentLayer layer) {
        DeviceComponent source = new DeviceComponent(fromId, fromPort);
        DeviceComponent target = new DeviceComponent(toId, toPort);
        DeviceEdge edge = new DeviceEdge(source, target, channelId);
        mEdgesLayer.put(edge, layer);
        return mDeviceGraph.addEdge(source, target, edge);
    }

    public Set<DeviceEdge> getAllEdges() {
        return mDeviceGraph.edgeSet();
    }

    public Set<DeviceEdge> getAllFlowEdges() {
        Set<DeviceEdge> result = new HashSet<>();
        for (DeviceEdge edge : mDeviceGraph.edgeSet()) {
            if (mEdgesLayer.get(edge) == ComponentLayer.FLOW) {
                result.add(edge);
            }
        }
        return result;
    }

    public Set<DeviceEdge> getAllControlEdges() {
        Set<DeviceEdge> result = new HashSet<>();
        for (DeviceEdge edge : mDeviceGraph.edgeSet()) {
            if (mEdgesLayer.get(edge) == ComponentLayer.CONTROL) {
                result.add(edge);
            }
        }
        return result;
    }

    public Set<DeviceComponent> vertexSet() {
        return mDeviceGraph.vertexSet();
    }

    public Set<DeviceEdge> edgeSet() {
        return mDeviceGraph.edgeSet();
    }

    public Set<DeviceEdge> edgesOf(DeviceComponent source) {
        return mDeviceGraph.edgesOf(source);
    }

    public DeviceComponent getEdgeSource(DeviceEdge edge) {
        return mDeviceGraph.getEdgeSource(edge);
    }

    public DeviceComponent getEdgeTarget(DeviceEdge edge) {
        return mDeviceGraph.getEdgeTarget(edge);
    }

    public DeviceComponent getEdgeTarget(DeviceEdge edge, DeviceComponent source) {
        DeviceComponent a = edge.getSource();
        DeviceComponent b = edge.getTarget();
        return (a.equals(source)) ? b : a;
    }

    public DeviceEdge getEdge(String channelIdentifier) {
        for (DeviceEdge edge : mDeviceGraph.edgeSet()) {
            if (edge.getChannel().equals(channelIdentifier)) {
                return edge;
            }
        }
        return null;
    }

    public DeviceEdge getEdge(DeviceComponent source, DeviceComponent target) {
        return mDeviceGraph.getEdge(source, target);
    }

    public void removeVertex(String vertexId) {
        List<DeviceComponent> toRemove = new ArrayList<>();
        for (DeviceComponent v : mDeviceGraph.vertexSet()) {
            if (v.getIdentifier().equals(vertexId)) {
                toRemove.add(v);
            }
        }
        for (DeviceComponent v : toRemove) {
            mDeviceGraph.removeVertex(v);
        }
    }

    public void removeVertex(DeviceComponent vertex) {
        mDeviceGraph.removeVertex(vertex);
    }

    public void removeEdge(DeviceEdge edge) {
        mDeviceGraph.removeEdge(edge);
    }

    public void removeEdge(DeviceComponent source, DeviceComponent target) {
        mDeviceGraph.removeEdge(source, target);
    }

    public Graph<DeviceComponent, DeviceEdge> getGraph() {
        return mDeviceGraph;
    }

    public void dump() {
        for (DeviceEdge edge : mDeviceGraph.edgeSet()) {
            System.out.println(edge);
        }
    }
}
