package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;

import java.awt.*;

public class Port extends BaseComponent {
    private int mRadius;

    public Port(String identifier, ComponentLayer layer, int radius) {
        super(identifier, layer, ComponentType.PORT);
        mRadius = radius;
        setPoints();
        setWidth(2 * radius);
        setHeight(2 * radius);
    }

    public double getRadius() {
        return mRadius;
    }

    @Override
    public boolean supportRotate() {
        return false;
    }

    @Override
    public void rotate() {

    }

    @Override
    public void draw(Graphics2D g) {
        int x = (int) getPosition().getX();
        int y = (int) getPosition().getY();
        int r = mRadius;
        g.setPaint(Color.BLUE);
        g.fillOval(x - r / 2, y - r / 2, r, r);

    }

    private void setPoints() {
        Point2D leftTop = new Point2D(-mRadius / 2, -mRadius / 2);
        Point2D rightBottom = new Point2D(mRadius, mRadius);
        mPoints.add(new Point2DPair(leftTop, rightBottom));
        addPort(1, new Point2D(0.0, mRadius));
        addPort(2, new Point2D(mRadius, 0.0));
        addPort(3, new Point2D(0.0, -mRadius));
        addPort(4, new Point2D(-mRadius, 0.0));
    }
}
