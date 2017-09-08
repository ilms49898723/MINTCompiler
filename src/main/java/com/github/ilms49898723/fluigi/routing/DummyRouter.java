package com.github.ilms49898723.fluigi.routing;

import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public class DummyRouter extends BaseRouter {
    public DummyRouter(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
    }

    @Override
    public void start() {
        List<DefaultEdge> edges = mDeviceGraph.getAllEdges();
    }
}
