package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.drawing.Point2DPair;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class LongCellTrap extends BaseComponent {
    private List<Point2DPair> mPoints;
    private int mNumChambers;
    private int mChamberWidth;
    private int mChamberLength;
    private int mChamberSpacing;
    private int mChannelWidth;

    public LongCellTrap(String identifier, ComponentLayer layer, int numChambers, int chamberWidth, int chamberLength, int chamberSpacing, int channelWidth) {
        super(identifier, ComponentType.LONG_CELLTRAP, layer);
        mPoints = new ArrayList<>();
        mNumChambers = numChambers;
        mChamberWidth = chamberWidth;
        mChamberLength = chamberLength;
        mChamberSpacing = chamberSpacing;
        mChannelWidth = channelWidth;
        setPoints();
    }

    @Override
    public void doRotate(int degree) {
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.CYAN);
        for (int i = 1; i < mPoints.size(); ++i) {
            Point2D pt = mPoints.get(i).getPointA();
            int w = (int) (mPoints.get(i).getPointB().getX() - pt.getX());
            int h = (int) (mPoints.get(i).getPointB().getY() - pt.getY());
            pt = new Point2D.Double(pt.getX() + getPosition().getX(), pt.getY() + getPosition().getY());
            g.fillRect((int) pt.getX(), (int) pt.getY(), w, h);
        }
        g.setColor(Color.BLUE);
        Point2D start = mPoints.get(0).getPointA();
        start = new Point2D.Double(start.getX() + getPosition().getX(), start.getY() + getPosition().getY());
        int channelWidth = (int) (mPoints.get(0).getPointB().getX() - mPoints.get(0).getPointA().getX());
        int channelHeight = (int) (mPoints.get(0).getPointB().getY() - mPoints.get(0).getPointA().getY());
        g.fillRect((int) start.getX(), (int) start.getY(), channelWidth, channelHeight);
    }

    private void setPoints() {
        Point2D startPoint = new Point2D.Double(mChamberWidth, -mChamberLength);
        Point2D channelStart = new Point2D.Double(0.0, 0.0);
        Point2D channelEnd = new Point2D.Double((mNumChambers + 2) * mChamberWidth + (mNumChambers - 1) * mChamberSpacing, mChannelWidth);
        mPoints.add(new Point2DPair(channelStart, channelEnd));
        for (int i = 0; i < mNumChambers; ++i) {
            Point2D pa = new Point2D.Double(startPoint.getX(), startPoint.getY());
            Point2D pb = new Point2D.Double(pa.getX() + mChamberWidth, pa.getY() + 2 * mChamberLength + mChannelWidth);
            mPoints.add(new Point2DPair(pa, pb));
            startPoint = new Point2D.Double(startPoint.getX() + mChamberWidth + mChamberSpacing, startPoint.getY());
        }
    }
}
