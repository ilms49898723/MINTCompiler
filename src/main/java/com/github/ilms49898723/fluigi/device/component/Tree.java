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

public class Tree extends BaseComponent {
    private int mInChannelNum;
    private int mOutChannelNum;
    private int mSpacing;
    private int mChannelWidth;
    private int mChannelLength;

    public Tree(String identifier, ComponentLayer layer, int inNum, int outNum, int spacing, int channelWidth) {
        super(identifier, layer, ComponentType.TREE);
        mInChannelNum = inNum;
        mOutChannelNum = outNum;
        mSpacing = spacing;
        mChannelWidth = channelWidth;
        mChannelLength = mChannelWidth * 10;
        setPoints();
    }

    @Override
    public boolean supportRotate() {
        return true;
    }

    @Override
    public void draw(Graphics2D g) {
        Point2DUtil.drawPoints(mPoints, mColors, getPosition(), g);
    }

    private void setPoints() {
        int numChannel = (mInChannelNum == 1) ? mOutChannelNum : mInChannelNum;
        int numLevel = (int) (Math.log(numChannel) / Math.log(2));
        Point2D startPt = new Point2D(0.0, 0.0);
        for(int i = 0 ; i < numChannel ; i++) {
            mPoints.addAll(getSingleChannel(startPt));
            startPt = new Point2D(startPt.getX() + mChannelWidth + mSpacing, startPt.getY());
        }
        startPt = new Point2D(mPoints.get(0).getPointA().getX(), mChannelLength - mChannelWidth);
        for(int i = 1 ; i <= numLevel ; i++) {
            int numMixChannel = (int)Math.pow(2, (numLevel - i));
            int disMixChannel = ((int)Math.pow(2, i)) * (mChannelWidth + mSpacing);
            int disNextLvl = ((int)Math.pow(2, i) - 1) * (mChannelWidth + mSpacing) / 2;
            for(int j = 0 ; j < numMixChannel ; j++) {
                mPoints.addAll(getSingleLevel(startPt, i));
                startPt = new Point2D(startPt.getX() + disMixChannel, startPt.getY());
            }
            startPt = new Point2D(mPoints.get(0).getPointA().getX() + disNextLvl, startPt.getY() + (mChannelLength / 2) - mChannelWidth);
        }

        double midLength = mPoints.get(0).getPointA().getX() + ((numChannel*(mChannelWidth + mSpacing) - mSpacing) / 2);
        double midHeight = mPoints.get(0).getPointA().midpoint(mPoints.get(mPoints.size() - 1).getPointB()).getY();
        Point2D midPt = new Point2D(midLength, midHeight);

        Point2DUtil.subtractPoints(mPoints, midPt);

        for(int i = 0 ; i < numChannel ; i++) {
            Point2D port = new Point2D(mPoints.get(i).getPointA().getX() + mChannelWidth / 2, mPoints.get(i).getPointA().getY());
            addPort(i + 1, port, PortDirection.BOTTOM);
        }
        Point2D finalPort = new Point2D((mPoints.get(mPoints.size() - 1).getPointB()).getX() - mChannelWidth / 2, (mPoints.get(mPoints.size() - 1).getPointB()).getY());
        addPort(numChannel + 1, finalPort, PortDirection.TOP);

        for(int i = 0 ; i < mPoints.size() ; i++) {
            mColors.add(Color.BLUE);
        }

        setWidth(numChannel * (mChannelWidth + mSpacing) - mSpacing);
        setHeight(mChannelLength + numLevel * (mChannelLength/2 - mChannelWidth));
    }

    private List<Point2DPair> getSingleChannel(Point2D startPt) {
        List<Point2DPair> result = new ArrayList<>();
        Point2D pa = new Point2D(startPt.getX(), startPt.getY());
        Point2D pb = new Point2D(startPt.getX() + mChannelWidth, startPt.getY() + mChannelLength);
        result.add(new Point2DPair(pa, pb));
        return result;
    }

    private List<Point2DPair> getSingleLevel(Point2D startPt, int level) {
        List<Point2DPair> result = new ArrayList<>();
        int rec1Length = ((int)Math.pow(2, level - 1)) * (mChannelWidth + mSpacing) + mChannelWidth;
        int rec2Height = mChannelLength / 2;
        Point2D pa1 = new Point2D(startPt.getX(), startPt.getY());
        Point2D pb1 = new Point2D(pa1.getX() + rec1Length, pa1.getY() + mChannelWidth);
        result.add(new Point2DPair(pa1, pb1));
        Point2D pa2 = new Point2D(startPt.getX() + ((rec1Length - mChannelWidth) / 2), startPt.getY());
        Point2D pb2 = new Point2D(pa2.getX() + mChannelWidth, pa2.getY() + rec2Height);
        result.add(new Point2DPair(pa2, pb2));
        return result;
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

		Tree tree = (Tree) o;

		if (mInChannelNum != tree.mInChannelNum) {
			return false;
		}
		if (mOutChannelNum != tree.mOutChannelNum) {
			return false;
		}
		if (mSpacing != tree.mSpacing) {
			return false;
		}
		if (mChannelWidth != tree.mChannelWidth) {
			return false;
		}
		return mChannelLength == tree.mChannelLength;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + mInChannelNum;
		result = 31 * result + mOutChannelNum;
		result = 31 * result + mSpacing;
		result = 31 * result + mChannelWidth;
		result = 31 * result + mChannelLength;
		return result;
	}
}
