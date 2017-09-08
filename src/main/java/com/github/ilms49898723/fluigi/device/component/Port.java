package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;
import org.jfree.graphics2d.svg.SVGGraphics2D;

import java.awt.*;

public class Port extends BaseComponent {
    private double mRadius;

    public Port(String identifier, ComponentLayer layer, double radius) {
        super(identifier, ComponentType.PORT, layer);
        mRadius = radius;
        addPort(1, new Point2D(0.0, radius));
        addPort(2, new Point2D(radius, 0.0));
        addPort(3, new Point2D(0.0, -radius));
        addPort(4, new Point2D(-radius, 0.0));
    }

    public double getRadius() {
        return mRadius;
    }

    @Override
    public void doRotate(int degree) {

    }

    @Override
    public void draw(Graphics2D g) {
        int x = (int) getPosition().getX();
        int y = (int) getPosition().getY();
        int r = (int) mRadius;
        g.setPaint(Color.BLUE);
        g.fillOval(x, y, r, r);
    }
}
