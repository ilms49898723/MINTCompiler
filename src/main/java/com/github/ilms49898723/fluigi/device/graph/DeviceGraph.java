package com.github.ilms49898723.fluigi.device.graph;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class DeviceGraph {
    private Graph<BaseComponent, DefaultEdge> mDeviceGraph;

    public DeviceGraph() {
        mDeviceGraph = new SimpleGraph<>(DefaultEdge.class);
    }

    public boolean addVertex(BaseComponent component) {
        return mDeviceGraph.addVertex(component);
    }
}
