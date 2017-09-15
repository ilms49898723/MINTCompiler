package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;

import java.awt.*;

public class Channel extends BaseComponent {
    private int mChannelWidth;

    public Channel(String identifier, ComponentLayer layer, int channelWidth) {
        super(identifier, layer, ComponentType.CHANNEL);
        mChannelWidth = channelWidth;
        addPort(1, new Point2D(0.0, 0.0));
        addPort(2, new Point2D(0.0, 0.0));
        setWidth(0);
        setHeight(0);
    }

    public int getChannelWidth() {
        return mChannelWidth;
    }

    public void addPoint(Point2DPair point, Color color) {
        mPoints.add(point);
        mColors.add(color);
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
        Point2DUtil.drawPoints(mPoints, mColors, Point2D.ZERO, g);
    }
}
