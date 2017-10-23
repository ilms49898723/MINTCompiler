package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import com.github.ilms49898723.fluigi.device.symbol.PortDirection;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Port extends BaseComponent {
    private int mRadius;

    public Port(String identifier, ComponentLayer layer, int radius) {
        super(identifier, layer, ComponentType.PORT);
        mRadius = radius;
        mSwappablePorts.addAll(Arrays.asList(1, 2, 3, 4));
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
    public List<Point2DPair> getPortPoints() {
        List<Point2DPair> result = new ArrayList<>();
        for (int portId : mPortChannelWidth.keySet()) {
            if (mPortChannelWidth.get(portId) != -1) {
                result.add(Point2DUtil.getPortPoints(
                        getPort(portId),
                        getPosition(),
                        mPortDirection.get(portId),
                        mPortChannelWidth.get(portId)
                ));
            }
        }
        return result;
    }

    @Override
    public void draw(Graphics2D g) {
        int x = (int) getPosition().getX();
        int y = (int) getPosition().getY();
        int r = mRadius;
        g.setPaint((getLayer() == ComponentLayer.FLOW) ? Color.BLUE : Color.RED);
        g.fillOval(x - r, y - r, r * 2, r * 2);
        for (int portId : mPortChannelWidth.keySet()) {
            if (mPortChannelWidth.get(portId) != -1) {
                Point2DUtil.drawPort(
                        getPort(portId),
                        getPosition(),
                        mPortDirection.get(portId),
                        mPortChannelWidth.get(portId),
                        g,
                        (getLayer() == ComponentLayer.FLOW) ? Color.BLUE : Color.RED
                );
            }
        }
    }

    private void setPoints() {
        Point2D leftTop = new Point2D(-mRadius / 2, -mRadius / 2);
        Point2D rightBottom = new Point2D(mRadius, mRadius);
        mPoints.add(new Point2DPair(leftTop, rightBottom));
        addPort(1, Point2D.ZERO, PortDirection.TOP);
        addPort(2, Point2D.ZERO, PortDirection.BOTTOM);
        addPort(3, Point2D.ZERO, PortDirection.LEFT);
        addPort(4, Point2D.ZERO, PortDirection.RIGHT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Port port = (Port) o;

        return !(getRadius() != port.getRadius());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) getRadius();
        return result;
    }
}
