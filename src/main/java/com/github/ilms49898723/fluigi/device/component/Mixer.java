package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import org.jfree.graphics2d.svg.SVGGraphics2D;

import java.awt.*;

public class Mixer extends BaseComponent {
    private int mNumBends;
    private int mBendSpacing;
    private int mBendLength;
    private int mChannelWidth;

    public Mixer(String identifier, ComponentLayer layer, int numBends, int bendSpacing, int bendLength, int channelWidth) {
        super(identifier, ComponentType.MIXER, layer);
        mNumBends = numBends;
        mBendSpacing = bendSpacing;
        mBendLength = bendLength;
        mChannelWidth = channelWidth;
    }

    @Override
    public void drawPng(Graphics2D png) {

    }

    @Override
    public void drawSvg(SVGGraphics2D svg) {

    }
}
