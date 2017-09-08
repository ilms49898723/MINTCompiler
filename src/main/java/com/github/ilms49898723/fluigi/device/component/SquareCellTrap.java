package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import org.jfree.graphics2d.svg.SVGGraphics2D;

import java.awt.*;

public class SquareCellTrap extends BaseComponent {
    private int mChamberWidth;
    private int mChamberLength;
    private int mChannelWidth;

    public SquareCellTrap(String identifier, ComponentLayer layer, int chamberWidth, int chamberLength, int channelWidth) {
        super(identifier, ComponentType.SQUARE_CELLTRAP, layer);
        mChamberWidth = chamberWidth;
        mChamberLength = chamberLength;
        mChannelWidth = channelWidth;
    }

    @Override
    public void drawPng(Graphics2D png) {

    }

    @Override
    public void drawSvg(SVGGraphics2D svg) {

    }
}
