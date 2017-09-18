package com.github.ilms49898723.fluigi.device.component;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;

import javafx.geometry.Point2D;

public class RotaryPump extends BaseComponent{
	private double mRadius;
	private int mFlowWidth;
	private int mFlowLength;
	
	
	public RotaryPump(String identifier, ComponentLayer layer, double radius, int flowchannelwidth){
		super(identifier, layer, ComponentType.ROTARY_PUMP);
		mRadius = radius;
		mFlowWidth = flowchannelwidth;
		mFlowLength = 2 * (int) mRadius;
		setPoints();
	}
	
	public double getRadius() {
        return mRadius;
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
    		int x = (int) getPosition().getX();
        int y = (int) getPosition().getY();
        int r = (int) mRadius;
        g.setPaint(Color.BLUE);
        g.fillOval(x - r / 2, y - r / 2, r, r);
    }
    
    private void setPoints() {
    		Point2D StartPoint = new Point2D(3 * mRadius, 3 * mRadius);
		Point2D pa1 = new Point2D(StartPoint.getX(), mRadius + mFlowWidth);
		Point2D pa2 = new Point2D(2 * mRadius, mRadius);
		Point2DPair pp1 = new Point2DPair(pa1, pa2);
		mPoints.add(pp1);
		Point2D pb1 = new Point2D(StartPoint.getX(), -(mRadius + mFlowWidth));
		Point2D pb2 = new Point2D(-2 * mRadius, -mRadius);
		Point2DPair pp2 = new Point2DPair(pb1, pb2);
		mPoints.add(pp2);
        
    }
    
    
}
