package com.github.ilms49898723.fluigi.device.component;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;

public class Valve extends BaseComponent {
	private List<Point2DPair> mPoints;
	private List<Color> mColors;
	private String mChannelId;
	
	public Valve(String identifier, ComponentLayer layer, int width, int length, String channelId) {
		super(identifier, ComponentType.VALVE, layer);
		mPoints = new ArrayList<>();
		mColors = new ArrayList<>();
		mChannelId = channelId;
		setWidth(width);
		setHeight(length);
	}

	@Override
	public boolean supportRotate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void rotate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

}
