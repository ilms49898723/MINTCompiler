package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.ArrayList;

public class Channel extends BaseComponent {
    private int mChannelWidth;

    public Channel(String identifier, ComponentLayer layer, int channelWidth) {
        super(identifier, layer, ComponentType.CHANNEL);
        mChannelWidth = channelWidth;
        setWidth(mChannelWidth);
        setHeight(0);
    }

    public int getChannelWidth() {
        return mChannelWidth;
    }

    public void addPoint(Point2DPair point, Color color) {
        mPoints.add(point);
        mColors.add(color);
    }

    public void cleanup() {
        mPoints = new ArrayList<>();
        mColors = new ArrayList<>();
    }

    @Override
    public boolean supportRotate() {
        return false;
    }

    @Override
    public boolean supportSwapPort() {
        return false;
    }

    @Override
    public void draw(Graphics2D g) {
        Point2DUtil.drawPoints(mPoints, mColors, Point2D.ZERO, g);
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

        Channel channel = (Channel) o;

        return getChannelWidth() == channel.getChannelWidth();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getChannelWidth();
        return result;
    }
}
