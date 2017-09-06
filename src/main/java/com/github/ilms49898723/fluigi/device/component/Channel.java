package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;

public class Channel extends BaseComponent {
    public Channel(String identifier, ComponentLayer layer) {
        super(identifier, ComponentType.CHANNEL, layer);
        addPort(1, new Point2D(0.0, 0.0));
        addPort(2, new Point2D(0.0, 0.0));
    }
}
