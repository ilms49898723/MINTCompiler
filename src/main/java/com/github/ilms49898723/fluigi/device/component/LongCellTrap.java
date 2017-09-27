package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import com.github.ilms49898723.fluigi.device.symbol.PortDirection;
import javafx.geometry.Point2D;

import java.awt.*;

public class LongCellTrap extends BaseComponent {
    private int mNumChambers;
    private int mChamberWidth;
    private int mChamberLength;
    private int mChamberSpacing;
    private int mChannelWidth;

    public LongCellTrap(String identifier, ComponentLayer layer, int numChambers, int chamberWidth, int chamberLength, int chamberSpacing, int channelWidth) {
        super(identifier, layer, ComponentType.LONG_CELLTRAP);
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
    public boolean supportSwapPort() {
        return true;
    }

    @Override
    public void draw(Graphics2D g) {
        Point2DUtil.drawPoints(mPoints, mColors, getPosition(), g);
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
        addPort(1, portA, PortDirection.LEFT);
        addPort(2, portB, PortDirection.RIGHT);
        Point2DUtil.subtractPoints(mPoints, midPoint);
        setWidth((int) (portB.getX() - portA.getX()));
        setHeight(2 * mChamberLength + mChannelWidth);
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

        LongCellTrap cellTrap = (LongCellTrap) o;

        if (mNumChambers != cellTrap.mNumChambers) {
            return false;
        }
        if (mChamberWidth != cellTrap.mChamberWidth) {
            return false;
        }
        if (mChamberLength != cellTrap.mChamberLength) {
            return false;
        }
        if (mChamberSpacing != cellTrap.mChamberSpacing) {
            return false;
        }
        return mChannelWidth == cellTrap.mChannelWidth;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + mNumChambers;
        result = 31 * result + mChamberWidth;
        result = 31 * result + mChamberLength;
        result = 31 * result + mChamberSpacing;
        result = 31 * result + mChannelWidth;
        return result;
    }
}
