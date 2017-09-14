package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Mixer extends BaseComponent {
    private int mNumBends;
    private int mBendSpacing;
    private int mBendLength;
    private int mChannelWidth;

    public Mixer(String identifier, ComponentLayer layer, int numBends, int bendSpacing, int bendLength, int channelWidth) {
        super(identifier, layer, ComponentType.MIXER);
        mNumBends = numBends;
        mBendSpacing = bendSpacing;
        mBendLength = bendLength;
        mChannelWidth = channelWidth;
        setPoints();
    }

    @Override
    public boolean supportRotate() {
        return true;
    }

    @Override
    public void rotate() {
        Point2DUtil.rotateDevice(mPoints, mPorts);
        rotateWidthHeight();
    }

    @Override
    public void draw(Graphics2D g) {
        Point2DUtil.drawPoints(mPoints, mColors, getPosition(), g);
        Point2DUtil.drawPoint(getPosition(), Color.BLACK, Point2D.ZERO, 20, g);
        for (int key : getPorts().keySet()) {
            Point2DUtil.drawPoint(getPort(key), Color.BLACK, Point2D.ZERO, 20, g);
        }
    }

    private void setPoints() {
        Point2D startPoint = new Point2D(0.0, 0.0);
        for (int i = 0; i < mNumBends; ++i) {
            mPoints.addAll(getSingleBend(startPoint));
            startPoint = new Point2D(startPoint.getX() + 2 * (mChannelWidth + mBendSpacing), startPoint.getY());
        }
        for (int i = 0; i < mPoints.size(); ++i) {
            mColors.add(Color.BLUE);
        }
        Point2D midPoint = mPoints.get(0).getPointA().midpoint(mPoints.get(mPoints.size() - 1).getPointB());
        Point2D portA = new Point2D(mChannelWidth / 2, -mChannelWidth / 2);
        Point2D portB = portA.add(mNumBends * 2 * (mChannelWidth + mBendSpacing), 0.0);
        portA = portA.subtract(midPoint);
        portB = portB.subtract(midPoint);
        addPort(1, portA);
        addPort(2, portB);
        Point2DUtil.subtractPoints(mPoints, midPoint);
        setWidth((int) (portB.getX() - portA.getX()));
        setHeight(mBendLength);
    }

    private List<Point2DPair> getSingleBend(Point2D startPoint) {
        List<Point2DPair> result = new ArrayList<>();
        Point2D pa1 = new Point2D(startPoint.getX(), startPoint.getY() - mChannelWidth - (mBendLength - mChannelWidth) / 2);
        Point2D pa2 = new Point2D(pa1.getX() + mChannelWidth, startPoint.getY());
        result.add(new Point2DPair(pa1, pa2));
        Point2D pb1 = new Point2D(pa1.getX(), pa1.getY());
        Point2D pb2 = new Point2D(pb1.getX() + 2 * mChannelWidth + mBendSpacing, pb1.getY() + mChannelWidth);
        result.add(new Point2DPair(pb1, pb2));
        Point2D pc1 = new Point2D(pb2.getX() - mChannelWidth, pb2.getY() - mChannelWidth);
        Point2D pc2 = new Point2D(pc1.getX() + mChannelWidth, pc1.getY() + mBendLength);
        result.add(new Point2DPair(pc1, pc2));
        Point2D pd1 = new Point2D(pc2.getX() - mChannelWidth, pc2.getY() - mChannelWidth);
        Point2D pd2 = new Point2D(pd1.getX() + 2 * mChannelWidth + mBendSpacing, pd1.getY() + mChannelWidth);
        result.add(new Point2DPair(pd1, pd2));
        Point2D pe1 = new Point2D(pd2.getX() - mChannelWidth, pd2.getY() - (mBendLength - mChannelWidth) / 2 - mChannelWidth);
        Point2D pe2 = new Point2D(pd2.getX(), pd2.getY());
        result.add(new Point2DPair(pe1, pe2));
        return result;
    }
}
