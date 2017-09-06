package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;
import org.jfree.graphics2d.svg.SVGGraphics2D;

import java.awt.*;

public class Channel extends BaseComponent {
    public Channel(String identifier, ComponentLayer layer) {
        super(identifier, ComponentType.CHANNEL, layer);
        addPort(1, new Point2D(0.0, 0.0));
        addPort(2, new Point2D(0.0, 0.0));
    }

    @Override
    public void drawPng(Graphics2D png) {

    }

    @Override
    public void drawSvg(SVGGraphics2D svg) {

    }
}
