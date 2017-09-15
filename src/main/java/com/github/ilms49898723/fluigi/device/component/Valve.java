package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;

import java.awt.*;

public class Valve extends BaseComponent {
	private String mChannelId;

	public Valve(String identifier, ComponentLayer layer, int width, int length, String channelId) {
		super(identifier, layer, ComponentType.VALVE);
		mChannelId = channelId;
		setWidth(width);
		setHeight(length);
		setPoints();
	}

	@Override
	public boolean supportRotate() {
		return false;
	}

	@Override
	public void rotate() {

	}

	@Override
	public void draw(Graphics2D g) {
		//Position will be decide after placement & routing
		Point2DUtil.drawPoints(mPoints, mColors, getPosition(), g);
	}

	private void setPoints() {
		Point2D pa = new Point2D(-(getWidth() / 2), -(getHeight() / 2));
		Point2D pb = new Point2D(getWidth() / 2, getHeight() / 2);
		mPoints.add(new Point2DPair(pa, pb));
		mColors.add(Color.RED);
	}

}
