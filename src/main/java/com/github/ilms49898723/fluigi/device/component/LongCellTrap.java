package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LongCellTrap extends BaseComponent {
    private List<Point2DPair> mPoints;
    private List<Color> mColors;
    private int mNumChambers;
    private int mChamberWidth;
    private int mChamberLength;
    private int mChamberSpacing;
    private int mChannelWidth;

    public LongCellTrap(String identifier, ComponentLayer layer, int numChambers, int chamberWidth, int chamberLength, int chamberSpacing, int channelWidth) {
        super(identifier, ComponentType.LONG_CELLTRAP, layer);
        mPoints = new ArrayList<>();
        mColors = new ArrayList<>();
        mNumChambers = numChambers;
        mChamberWidth = chamberWidth;
        mChamberLength = chamberLength;
        mChamberSpacing = chamberSpacing;
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
        Point2D startPoint = new Point2D(mChamberWidth, -mChamberLength);
        for (int i = 0; i < mNumChambers; ++i) {
            Point2D pa = new Point2D(startPoint.getX(), startPoint.getY());
            Point2D pb = new Point2D(pa.getX() + mChamberWidth, pa.getY() + 2 * mChamberLength + mChannelWidth);
            mPoints.add(new Point2DPair(pa, pb));
            mColors.add(Color.CYAN);
            startPoint = new Point2D(startPoint.getX() + mChamberWidth + mChamberSpacing, startPoint.getY());
        }
        Point2D channelStart = new Point2D(0.0, 0.0);
        Point2D channelEnd = new Point2D((mNumChambers + 2) * mChamberWidth + (mNumChambers - 1) * mChamberSpacing, mChannelWidth);
        mPoints.add(new Point2DPair(channelStart, channelEnd));
        mColors.add(Color.BLUE);
        Point2D midPoint = new Point2D((channelStart.getX() + channelEnd.getX())/ 2, (channelStart.getY() + channelEnd.getY()) / 2);
        Point2D portA = channelStart.add(0.0, mChannelWidth / 2);
        Point2D portB = channelEnd.add(0.0, -mChannelWidth / 2);
        portA = portA.subtract(midPoint);
        portB = portB.subtract(midPoint);
        addPort(1, portA);
        addPort(2, portB);
        Point2DUtil.subtractPoints(mPoints, midPoint);
        setWidth((int) (portB.getX() - portA.getX()));
        setHeight(2 * mChamberLength + mChannelWidth);
    }
}
