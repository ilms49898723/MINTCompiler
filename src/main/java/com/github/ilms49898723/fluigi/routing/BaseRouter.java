package com.github.ilms49898723.fluigi.routing;

import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;

public abstract class BaseRouter {
    protected SymbolTable mSymbolTable;
    protected DeviceGraph mDeviceGraph;
    protected Parameters mParameters;

    public BaseRouter(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        mSymbolTable = symbolTable;
        mDeviceGraph = deviceGraph;
        mParameters = parameters;
    }

    public abstract void start();
}
