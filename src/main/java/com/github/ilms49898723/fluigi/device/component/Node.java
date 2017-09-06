package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;

public class Node extends BaseComponent {
    public Node(String identifier, ComponentLayer layer) {
        super(identifier, ComponentType.NODE, layer);
        // NOTE: port position???
        addPort(1, new Point2D(0.0, 2.0));
        addPort(2, new Point2D(2.0, 0.0));
        addPort(3, new Point2D(0.0, -2.0));
        addPort(4, new Point2D(-2.0, 0.0));
    }
}
