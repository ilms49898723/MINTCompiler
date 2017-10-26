package com.github.ilms49898723.fluigi.placement.drc;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;

public class PositionChecker extends BasePlacer {
    public PositionChecker(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
    }

    @Override
    public boolean placement() {
        for (BaseComponent component : mSymbolTable.getComponents(ComponentLayer.FLOW)) {
            Point2DUtil.adjustComponent(component, mParameters);
        }
        return true;
    }
}
