package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<Integer> getValvePositions(int threshold, int step) {
        List<Integer> result = new ArrayList<>();
        int numPoints = ((mPoints.size() - threshold) / 2 + 1) < 0 ? 1 : (mPoints.size() - threshold) / 2 + 1;
        int pivot = mPoints.size() / 2;
        for (int i = 0; i < numPoints; ++i) {
            boolean found = false;
            if (isValidPosition(pivot + i, threshold)) {
                result.add(pivot + i);
                found = true;
            }
            if (isValidPosition(pivot - i, threshold)) {
                result.add(pivot - i);
                found = true;
            }
            if (found) {
                i += step;
            } else {
                ++i;
            }
            if (result.size() >= 20) {
                break;
            }
        }
        return result;
    }

    public boolean isVerticalChannelAt(int index) {
        int checkA = Math.max(0, index - 1);
        int checkB = Math.min(index + 1, mPoints.size() - 1);
        Point2D a = mPoints.get(checkA).getPointA();
        Point2D b = mPoints.get(checkB).getPointA();
        return ((int) a.getX()) == ((int) b.getX());
    }

    public boolean isHorizontalChannelAt(int index) {
        int checkA = Math.max(0, index - 1);
        int checkB = Math.min(index + 1, mPoints.size() - 1);
        Point2D a = mPoints.get(checkA).getPointA();
        Point2D b = mPoints.get(checkB).getPointA();
        return ((int) a.getY()) == ((int) b.getY());
    }

    public int countBends() {
        int counter = 0;
        for (int i = 0; i < mPoints.size() - 2; ++i) {
            Point2D a = mPoints.get(i).getPointA();
            Point2D b = mPoints.get(i + 2).getPointA();
            if (Math.abs(a.getX() - b.getX()) > 1e-2 &&
                    Math.abs(a.getY() - b.getY()) > 1e-2)
                ++counter;
        }
        return counter;
    }

    private boolean isValidPosition(int midIdx, int threshold) {
        int ht = threshold / 2 + 1;
        if (midIdx - ht < 0 || midIdx + ht >= mPoints.size()) {
            return true;
        }
        int pivot = midIdx - ht;
        for (int i = 0; i < threshold - 3; ++i) {
            if (!isContinuous(mPoints.get(pivot + i).getPointA(), mPoints.get(pivot + i + 2).getPointA())) {
                return false;
            }
        }
        return true;
    }

    private boolean isContinuous(Point2D a, Point2D b) {
        int xa = (int) a.getX();
        int xb = (int) b.getX();
        int ya = (int) a.getY();
        int yb = (int) b.getY();
        return (xa == xb || ya == yb);
    }

    @Override
    public boolean supportRotate() {
        return false;
    }

    @Override
    public List<Point2DPair> getPortPoints() {
        return new ArrayList<>();
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
