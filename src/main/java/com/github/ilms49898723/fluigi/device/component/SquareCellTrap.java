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

public class SquareCellTrap extends BaseComponent {
    private int mChamberWidth;
    private int mChamberLength;
    private int mChannelWidth;

    public SquareCellTrap(String identifier, ComponentLayer layer, int chamberWidth, int chamberLength, int channelWidth) {
        super(identifier, layer, ComponentType.SQUARE_CELLTRAP);
        mChamberWidth = chamberWidth;
        mChamberLength = chamberLength;
        mChannelWidth = channelWidth;
        mSwappablePorts.addAll(Arrays.asList(1, 2, 3, 4));
        setPoints(new Point2D(0.0, 0.0));
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
        Point2DUtil.drawPoints(mPoints, mColors, getPosition(), g);
    }

    private void setPoints(Point2D startPoint) {
        Point2D chamberStart = new Point2D(startPoint.getX() + mChannelWidth, startPoint.getY() - mChamberLength);
        Point2D chamberEnd = new Point2D(chamberStart.getX() + mChannelWidth + 2 * mChamberWidth, chamberStart.getY() + mChannelWidth + 2 * mChamberWidth);
        mPoints.add(new Point2DPair(chamberStart, chamberEnd));
        mColors.add(Color.CYAN);
        Point2D channelAStart = new Point2D(startPoint.getX(), startPoint.getY());
        Point2D channelAEnd = new Point2D(startPoint.getX() + 3 * mChannelWidth + 2 * mChamberWidth, startPoint.getY() + mChannelWidth);
        mPoints.add(new Point2DPair(channelAStart, channelAEnd));
        mColors.add(Color.BLUE);
        Point2D channelBStart = new Point2D(startPoint.getX() + mChannelWidth + mChamberWidth, startPoint.getY() - mChannelWidth - mChamberWidth);
        Point2D channelBEnd = new Point2D(channelBStart.getX() + mChannelWidth, channelBStart.getY() + 3 * mChannelWidth + 2 * mChamberWidth);
        mPoints.add(new Point2DPair(channelBStart, channelBEnd));
        mColors.add(Color.BLUE);
        Point2D midPoint = chamberStart.midpoint(chamberEnd);
        Point2D portA = new Point2D(mChannelWidth / 2 + mChamberWidth + mChannelWidth, 0.0);
        Point2D portB = new Point2D(-(mChannelWidth / 2 + mChamberWidth + mChannelWidth), 0.0);
        Point2D portC = new Point2D(0.0, mChannelWidth / 2 + mChamberWidth + mChannelWidth);
        Point2D portD = new Point2D(0.0, -(mChannelWidth / 2 + mChamberWidth + mChannelWidth));
        addPort(1, portA, PortDirection.RIGHT);
        addPort(2, portB, PortDirection.LEFT);
        addPort(3, portC, PortDirection.BOTTOM);
        addPort(4, portD, PortDirection.TOP);
        Point2DUtil.subtractPoints(mPoints, midPoint);
        setWidth((int) (portA.getX() - portB.getX()));
        setHeight((int) (portC.getY() - portD.getY()));
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

        SquareCellTrap cellTrap = (SquareCellTrap) o;

        if (mChamberWidth != cellTrap.mChamberWidth) {
            return false;
        }
        if (mChamberLength != cellTrap.mChamberLength) {
            return false;
        }
        return mChannelWidth == cellTrap.mChannelWidth;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + mChamberWidth;
        result = 31 * result + mChamberLength;
        result = 31 * result + mChannelWidth;
        return result;
    }
}
