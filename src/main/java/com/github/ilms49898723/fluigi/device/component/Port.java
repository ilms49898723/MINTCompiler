package com.github.ilms49898723.fluigi.device.component;

import javafx.geometry.Point2D;

public class Port extends BaseComponent {
    public Port(String identifier, ComponentLayer layer) {
        super(identifier, ComponentType.PORT, layer);
        addPort(1, new Point2D(0.0, 2.0));
        addPort(2, new Point2D(2.0, 0.0));
        addPort(3, new Point2D(0.0, -2.0));
        addPort(4, new Point2D(-2.0, 0.0));
    }
}
