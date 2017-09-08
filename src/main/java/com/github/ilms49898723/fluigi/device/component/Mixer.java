package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.drawing.Point2DPair;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Mixer extends BaseComponent {
    private List<Point2DPair> mPoints;
    private int mNumBends;
    private int mBendSpacing;
    private int mBendLength;
    private int mChannelWidth;

    public Mixer(String identifier, ComponentLayer layer, int numBends, int bendSpacing, int bendLength, int channelWidth) {
        super(identifier, ComponentType.MIXER, layer);
        mPoints = new ArrayList<>();
        mNumBends = numBends;
        mBendSpacing = bendSpacing;
        mBendLength = bendLength;
        mChannelWidth = channelWidth;
        setPoints();
    }

    @Override
    public void doRotate(int degree) {

    }

    @Override
    public void draw(Graphics2D g) {
        for (Point2DPair pair : mPoints) {
            Point2D start = pair.getPointA();
            start = new Point2D.Double(start.getX() + getPosition().getX(), start.getY() + getPosition().getY());
            Point2D end = pair.getPointB();
            end = new Point2D.Double(end.getX() - pair.getPointA().getX(), end.getY() - pair.getPointA().getY());
            int x = (int) start.getX();
            int y = (int) start.getY();
            int w = (int) end.getX();
            int h = (int) end.getY();
            g.setColor(Color.BLUE);
            g.fillRect(x, y, w, h);
        }
    }

    private void setPoints() {
        Point2D startPoint = new Point2D.Double(0.0, 0.0);
        for (int i = 0; i < mNumBends; ++i) {
            mPoints.addAll(getSingleBend(startPoint));
            startPoint = new Point2D.Double(startPoint.getX() + 2 * (mChannelWidth + mBendSpacing), startPoint.getY());
        }
    }

    private List<Point2DPair> getSingleBend(Point2D startPoint) {
        List<Point2DPair> result = new ArrayList<>();
        Point2D pa1 = new Point2D.Double(startPoint.getX(), startPoint.getY() - mChannelWidth - (mBendLength - mChannelWidth) / 2);
        Point2D pa2 = new Point2D.Double(pa1.getX() + mChannelWidth, startPoint.getY());
        result.add(new Point2DPair(pa1, pa2));
        Point2D pb1 = new Point2D.Double(pa1.getX(), pa1.getY());
        Point2D pb2 = new Point2D.Double(pb1.getX() + 2 * mChannelWidth + mBendSpacing, pb1.getY() + mChannelWidth);
        result.add(new Point2DPair(pb1, pb2));
        Point2D pc1 = new Point2D.Double(pb2.getX() - mChannelWidth, pb2.getY() - mChannelWidth);
        Point2D pc2 = new Point2D.Double(pc1.getX() + mChannelWidth, pc1.getY() + mBendLength);
        result.add(new Point2DPair(pc1, pc2));
        Point2D pd1 = new Point2D.Double(pc2.getX() - mChannelWidth, pc2.getY() - mChannelWidth);
        Point2D pd2 = new Point2D.Double(pd1.getX() + 2 * mChannelWidth + mBendSpacing, pd1.getY() + mChannelWidth);
        result.add(new Point2DPair(pd1, pd2));
        Point2D pe1 = new Point2D.Double(pd2.getX() - mChannelWidth, pd2.getY() - (mBendLength - mChannelWidth) / 2 - mChannelWidth);
        Point2D pe2 = new Point2D.Double(pd2.getX(), pd2.getY());
        result.add(new Point2DPair(pe1, pe2));
        return result;
    }
}
