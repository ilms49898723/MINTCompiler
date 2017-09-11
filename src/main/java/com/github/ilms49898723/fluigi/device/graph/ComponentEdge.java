package com.github.ilms49898723.fluigi.device.graph;

import org.jgrapht.graph.DefaultEdge;

public class ComponentEdge extends DefaultEdge {
    private DeviceComponent mSrc;
    private DeviceComponent mDst;

    public ComponentEdge(DeviceComponent src, DeviceComponent dst) {
        mSrc = src;
        mDst = dst;
    }

    public DeviceComponent getSrc() {
        return mSrc;
    }

    public DeviceComponent getDst() {
        return mDst;
    }
}
