package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import com.github.ilms49898723.fluigi.device.symbol.PortDirection;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Node extends BaseComponent {
    private int mLength;

    public Node(String identifier, ComponentLayer layer, int length) {
        super(identifier, layer, ComponentType.NODE);
        mLength = length;
        addPort(1, Point2D.ZERO, PortDirection.TOP);
        addPort(2, Point2D.ZERO, PortDirection.BOTTOM);
        addPort(3, Point2D.ZERO, PortDirection.LEFT);
        addPort(4, Point2D.ZERO, PortDirection.RIGHT);
        setWidth(mLength);
        setHeight(mLength);
        setPoints(mLength);
    }

    @Override
    public boolean supportRotate() {
        return false;
    }

    @Override
    public boolean supportSwapPort() {
        return true;
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
        g.setColor((getLayer() == ComponentLayer.FLOW) ? Color.BLUE : Color.RED);
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

        Node node = (Node) o;

        return mLength == node.mLength;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + mLength;
        return result;
    }

    private void setPoints(int length) {
        Point2D lu = new Point2D(-length / 2, -length / 2);
        Point2D rb = new Point2D(length / 2, length / 2);
        mPoints.add(new Point2DPair(lu, rb));
    }
}
