package com.github.ilms49898723.fluigi.device.graph;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceGraph {
    private Graph<DeviceComponent, DeviceEdge> mDeviceGraph;
    private List<DeviceEdge> mEdges;
    private List<DeviceEdge> mFlowEdges;
    private List<DeviceEdge> mControlEdges;

    public DeviceGraph() {
        mDeviceGraph = new SimpleGraph<>(DeviceEdge.class);
        mEdges = new ArrayList<>();
        mFlowEdges = new ArrayList<>();
        mControlEdges = new ArrayList<>();
    }

    public boolean addVertex(String identifier, int portNumber) {
        return mDeviceGraph.addVertex(new DeviceComponent(identifier, portNumber));
    }

    public boolean addEdge(String fromId, int fromPort, String toId, int toPort, String channelId, ComponentLayer layer) {
        DeviceComponent source = new DeviceComponent(fromId, fromPort);
        DeviceComponent target = new DeviceComponent(toId, toPort);
        DeviceEdge edge = new DeviceEdge(source, target, channelId);
        mEdges.add(edge);
        if (layer == ComponentLayer.FLOW) {
            mFlowEdges.add(edge);
        } else {
            mControlEdges.add(edge);
        }
        return mDeviceGraph.addEdge(source, target, edge);
    }

    public List<DeviceEdge> getAllEdges() {
        return mEdges;
    }

    public List<DeviceEdge> getAllFlowEdges() {
        return mFlowEdges;
    }

    public List<DeviceEdge> getAllControlEdges() {
        return mControlEdges;
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

    public void removeVertex(DeviceComponent vertex) {
        mDeviceGraph.removeVertex(vertex);
    }

    public void removeEdge(DeviceEdge edge) {
        mDeviceGraph.removeEdge(edge);
        for (int i = 0; i < mEdges.size(); ++i) {
            if (mEdges.get(i).equals(edge)) {
                mEdges.remove(i);
                break;
            }
        }
        for (int i = 0; i < mFlowEdges.size(); ++i) {
            if (mFlowEdges.get(i).equals(edge)) {
                mFlowEdges.remove(i);
                break;
            }
        }
        for (int i = 0; i < mControlEdges.size(); ++i) {
            if (mControlEdges.get(i).equals(edge)) {
                mControlEdges.remove(i);
                break;
            }
        }
    }

    public void removeEdge(DeviceComponent source, DeviceComponent target) {
        mDeviceGraph.removeEdge(source, target);
    }

    public Graph<DeviceComponent, DeviceEdge> getGraph() {
        return mDeviceGraph;
    }

    public void dump() {
        for (DeviceEdge edge : mEdges) {
            System.out.println(edge);
        }
    }
}
