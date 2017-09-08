package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;

import java.awt.*;

public class LongCellTrap extends BaseComponent {
    private int mNumChambers;
    private int mChamberWidth;
    private int mChamberLength;
    private int mChamberSpacing;
    private int mChannelWidth;

    public LongCellTrap(String identifier, ComponentLayer layer, int numChambers, int chamberWidth, int chamberLength, int chamberSpacing, int channelWidth) {
        super(identifier, ComponentType.LONG_CELLTRAP, layer);
        mNumChambers = numChambers;
        mChamberWidth = chamberWidth;
        mChamberLength = chamberLength;
        mChamberSpacing = chamberSpacing;
        mChannelWidth = channelWidth;
    }

    @Override
    public void doRotate(int degree) {

    }

    @Override
    public void draw(Graphics2D g) {

    }
}
