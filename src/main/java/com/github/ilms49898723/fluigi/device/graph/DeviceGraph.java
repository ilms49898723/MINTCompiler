package com.github.ilms49898723.fluigi.device.graph;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceGraph {
    private Graph<DeviceComponent, DeviceEdge> mDeviceGraph;
    private List<DeviceEdge> mEdges;

    public DeviceGraph() {
        mDeviceGraph = new SimpleGraph<>(DeviceEdge.class);
        mEdges = new ArrayList<>();
    }

    public boolean addVertex(String identifier, int portNumber) {
        return mDeviceGraph.addVertex(new DeviceComponent(identifier, portNumber));
    }

    public boolean addEdge(String fromId, int fromPort, String toId, int toPort, String channelId) {
        DeviceComponent source = new DeviceComponent(fromId, fromPort);
        DeviceComponent target = new DeviceComponent(toId, toPort);
        DeviceEdge edge = new DeviceEdge(source, target, channelId);
        mEdges.add(edge);
        return mDeviceGraph.addEdge(source, target, edge);
    }

    public List<DeviceEdge> getAllEdges() {
        return mEdges;
    }

    public List<DeviceEdge> getAllFlowEdges(SymbolTable symbolTable) {
        List<DeviceEdge> result = new ArrayList<>();
        for (DeviceEdge edge : getAllEdges()) {
            ComponentLayer layer = symbolTable.get(getEdgeSource(edge).getIdentifier()).getLayer();
            if (layer == ComponentLayer.FLOW) {
                result.add(edge);
            }
        }
        return result;
    }

    public List<DeviceEdge> getAllControlEdges(SymbolTable symbolTable) {
        List<DeviceEdge> result = new ArrayList<>();
        for (DeviceEdge edge : getAllEdges()) {
            ComponentLayer layer = symbolTable.get(getEdgeSource(edge).getIdentifier()).getLayer();
            if (layer == ComponentLayer.CONTROL) {
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

    public DeviceComponent getEdgeSource(DeviceEdge edge) {
        return mDeviceGraph.getEdgeSource(edge);
    }

    public DeviceComponent getEdgeTarget(DeviceEdge edge) {
        return mDeviceGraph.getEdgeTarget(edge);
    }

    public void dump() {
        for (DeviceEdge edge : mEdges) {
            System.out.println(edge);
        }
    }
}
