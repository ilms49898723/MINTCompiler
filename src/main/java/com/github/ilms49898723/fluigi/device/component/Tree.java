package com.github.ilms49898723.fluigi.device.component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;

import javafx.geometry.Point2D;

public class Tree extends BaseComponent {
	private List<Point2DPair> mPoints;
	private List<Color> mColors;
	private int mInChannelNum;
	private int mOutChannelNum;
	private int mSpacing;
	private int mChannelWidth;
	
	private int mChannelLength;
	
	public Tree(String identifier, ComponentLayer layer, int inNum, int outNum, int spacing, int channelWidth) {
		super(identifier, ComponentType.TREE, layer);
		mPoints = new ArrayList<>();
		mColors = new ArrayList<>();
		mInChannelNum = inNum;
		mOutChannelNum = outNum;
		mSpacing = spacing;
		mChannelWidth = channelWidth;
		setPoints();
	}

	@Override
	public boolean supportRotate() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void rotate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}
	
	private void setPoints() {
		int numChannel = (mInChannelNum == 1) ? mOutChannelNum : mInChannelNum;
		int numLevel = (int) (Math.log(numChannel) / Math.log(2));
		Point2D startPt = new Point2D(0.0, 0.0);
		for(int i = 0 ; i < numChannel ; i++) {
			mPoints.addAll(getSingleChannel(startPt));
			startPt = new Point2D(startPt.getX() + mChannelWidth + mSpacing, startPt.getY());
		}
		startPt = new Point2D(0.0, mChannelLength - mChannelWidth);
		for(int i = 0 ; i < numLevel ; i++) {
			for(int j = 0 ; j < 1 ; j++) {
				
			}
			startPt = new Point2D(0.0, startPt.getY() + (mChannelLength / 2));
		}
		for(int i = 0 ; i < mPoints.size() ; i++) {
			mColors.add(Color.BLUE);
		}
	}
	
	private List<Point2DPair> getSingleChannel(Point2D startPt) {
		List<Point2DPair> result = new ArrayList<Point2DPair>();
		Point2D pa = new Point2D(startPt.getX(), startPt.getY());
		Point2D pb = new Point2D(startPt.getX() + mChannelWidth, startPt.getY() + mChannelLength);
		result.add(new Point2DPair(pa, pb));
		return result;
	}
	
	private List<Point2DPair> getSingleLevel(Point2D startPt) {
		List<Point2DPair> result = new ArrayList<Point2DPair>();
		return result;
	}
}
