package com.github.ilms49898723.fluigi.placement;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;

import java.util.Random;

public class DummyPlacer extends BasePlacer {
    public DummyPlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
    }

    @Override
    public boolean placement() {
        Random random = new Random();
        for (String identifier : mSymbolTable.keySet()) {
            BaseComponent component = mSymbolTable.get(identifier);
            int x = random.nextInt(mParameters.getMaxDeviceWidth() - 50);
            int y = random.nextInt(mParameters.getMaxDeviceHeight() - 50);
            component.setPositionX(x);
            component.setPositionY(y);
        }
        return true;
    }
}
