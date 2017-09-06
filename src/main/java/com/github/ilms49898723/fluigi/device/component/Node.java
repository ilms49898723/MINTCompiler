package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;
import org.jfree.graphics2d.svg.SVGGraphics2D;

import java.awt.*;

public class Node extends BaseComponent {
    public Node(String identifier, ComponentLayer layer) {
        super(identifier, ComponentType.NODE, layer);
        // NOTE: port position???
        addPort(1, new Point2D(0.0, 2.0));
        addPort(2, new Point2D(2.0, 0.0));
        addPort(3, new Point2D(0.0, -2.0));
        addPort(4, new Point2D(-2.0, 0.0));
    }

    @Override
    public void drawPng(Graphics2D png) {
        int x = (int) getPosition().getX();
        int y = (int) getPosition().getY();
        png.setPaint(Color.BLUE);
        png.fillRect(x - 100, y - 50, 200, 100);
        png.fillRect(x - 50, y - 100, 100, 200);
    }

    @Override
    public void drawSvg(SVGGraphics2D svg) {
        int x = (int) getPosition().getX();
        int y = (int) getPosition().getY();
        svg.setPaint(Color.BLUE);
        svg.fillRect(x - 100, y - 50, 200, 100);
        svg.fillRect(x - 50, y - 100, 100, 200);
    }
}
