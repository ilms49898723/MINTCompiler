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

public class Valve extends BaseComponent {
    public Valve(String identifier, ComponentLayer layer, int width, int length, String channelId) {
        super(identifier, layer, ComponentType.VALVE);
        setWidth(width);
        setHeight(length);
        setPoints();
    }

    @Override
    public boolean supportRotate() {
        return true;
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
        for (int portId : mPortChannelWidth.keySet()) {
            if (mPortChannelWidth.get(portId) != -1) {
                Point2DUtil.drawPort(
                        getPort(portId),
                        getPosition(),
                        mPortDirection.get(portId),
                        mPortChannelWidth.get(portId),
                        g,
                        (portId == 3 || portId == 4) ? Color.BLUE : Color.RED
                );
            }
        }
        Point2DUtil.drawPoints(mPoints, mColors, getPosition(), g);
    }

    private void setPoints() {
        Point2D pa = new Point2D(-(getWidth() / 2), -(getHeight() / 2));
        Point2D pb = new Point2D(getWidth() / 2, getHeight() / 2);
        mPoints.add(new Point2DPair(pa, pb));
        mColors.add(Color.RED);
        Point2D portL = new Point2D(-getWidth() / 2, 0.0);
        Point2D portR = new Point2D(getWidth() / 2, 0.0);
        Point2D portB = new Point2D(0.0, getHeight() / 2);
        Point2D portT = new Point2D(0.0, -getHeight() / 2);
        addPort(1, portB, PortDirection.BOTTOM);
        addPort(2, portT, PortDirection.TOP);
        addPort(3, portL, PortDirection.LEFT);
        addPort(4, portR, PortDirection.RIGHT);
    }
}
