package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;

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
    public void doTranspose(int degree) {

    }

    @Override
    public void draw(Graphics2D g) {

    }
}
