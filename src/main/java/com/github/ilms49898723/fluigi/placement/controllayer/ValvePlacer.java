package com.github.ilms49898723.fluigi.placement.controllayer;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.Channel;
import com.github.ilms49898723.fluigi.device.component.Valve;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;

import java.util.List;

public class ValvePlacer extends BasePlacer {
    public ValvePlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
    }

    @Override
    public boolean placement() {
        return placeValves();
    }

    private boolean placeValves() {
        List<BaseComponent> valves = mSymbolTable.getValves();
        for (BaseComponent valveComponent : valves) {
            Valve valve = (Valve) valveComponent;
            String channelId = valve.getChannelId();
            Channel channel = (Channel) mSymbolTable.get(channelId);
            placeValve(valve, channel);
        }
        return true;
    }

    private boolean placeValve(Valve valve, Channel channel) {
        int threshold = valve.getWidth() + 2 * channel.getChannelWidth();
        List<Integer> result = channel.getValvePositions(threshold, 20);
        Point2D a = channel.getPoints().get(result.get(0)).getPointA();
        Point2D b = channel.getPoints().get(result.get(0)).getPointB();
        valve.setPosition(a.midpoint(b));
        if (channel.isVerticalChannelAt(result.get(0))) {
            valve.rotate();
        }
        return true;
    }
}
