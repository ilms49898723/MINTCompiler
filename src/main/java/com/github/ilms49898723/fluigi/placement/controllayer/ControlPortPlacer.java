package com.github.ilms49898723.fluigi.placement.controllayer;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.Port;
import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class ControlPortPlacer extends BasePlacer {
    private int mLeftLimit;
    private int mRightLimit;
    private int mTopLimit;
    private int mBottomLimit;
    private boolean[][] mDevice;

    public ControlPortPlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
        mDevice = new boolean[mParameters.getMaxDeviceWidth()][mParameters.getMaxDeviceHeight()];
        for (int i = 0; i < mDevice.length; ++i) {
            for (int j = 0; j < mDevice[i].length; ++j) {
                mDevice[i][j] = false;
            }
        }
        getBoundary();
        markArea();
    }

    @Override
    public boolean placement() {
        return doPlacement();
    }

    private void markArea() {
        for (int i = mLeftLimit; i <= mRightLimit; ++i) {
            for (int j = mTopLimit; j <= mBottomLimit; ++j) {
                mDevice[i][j] = true;
            }
        }
    }

    private void getBoundary() {
        mLeftLimit = Integer.MAX_VALUE;
        mRightLimit = 0;
        mTopLimit = Integer.MAX_VALUE;
        mBottomLimit = 0;
        for (BaseComponent component : mSymbolTable.getComponents(ComponentLayer.FLOW)) {
            for (Point2DPair pair : component.getPoints()) {
                Point2D a = pair.getPointA();
                Point2D b = pair.getPointB();
                mLeftLimit = Math.min(mLeftLimit, (int) a.getX());
                mRightLimit = Math.max(mRightLimit, (int) b.getX());
                mTopLimit = Math.min(mTopLimit, (int) a.getY());
                mBottomLimit = Math.max(mBottomLimit, (int) b.getY());
            }
        }
        mLeftLimit -= mParameters.getComponentSpacing();
        mRightLimit += mParameters.getComponentSpacing();
        mTopLimit -= mParameters.getComponentSpacing();
        mBottomLimit += mParameters.getComponentSpacing();
    }

    private boolean doPlacement() {
        List<Port> ports = new ArrayList<>();
        for (BaseComponent component : mSymbolTable.getComponents(ComponentLayer.CONTROL)) {
            if (component.getType() == ComponentType.PORT) {
                ports.add((Port) component);
            }
        }
        for (Port port : ports) {
            int posx = -1;
            int posy = -1;
            int currentCost = Integer.MAX_VALUE;
            for (int x = 0; x < mParameters.getMaxDeviceWidth(); ++x) {
                for (int y = 0; y < mParameters.getMaxDeviceHeight(); ++y) {
                    if (isValidGrid(x, y, port.getRadius())) {
                        port.setPosition(new Point2D(x, y));
                        int cost = getCost(port);
                        if (cost < currentCost) {
                            currentCost = cost;
                            posx = x;
                            posy = y;
                        }
                    }
                }
            }
            port.setPosition(new Point2D(posx, posy));
        }
        return true;
    }

    private int getCost(BaseComponent component) {
        int cost = 0;
        for (int i = 1; i <= component.getNumPorts(); ++i) {
            DeviceComponent vertex = new DeviceComponent(component.getIdentifier(), i);
            for (DeviceEdge edge : mDeviceGraph.edgesOf(vertex)) {
                BaseComponent src = mSymbolTable.get(edge.getSource().getIdentifier());
                BaseComponent dst = mSymbolTable.get(edge.getTarget().getIdentifier());
                cost += Point2DUtil.manhattanDistance(src.getPort(edge.getSource().getPortNumber()), dst.getPort(edge.getTarget().getPortNumber()));
            }
        }
        return cost;
    }

    private int getCost(BaseComponent vertexA, BaseComponent vertexB) {
        return getCost(vertexA) + getCost(vertexB);
    }

    private boolean isValidGrid(int x, int y, int r) {
        if (mDevice[x][y]) {
            return false;
        }
        if (x - r - mParameters.getComponentSpacing() < 0 ||
                x + r + mParameters.getComponentSpacing() >= mParameters.getMaxDeviceWidth() ||
                y - r - mParameters.getComponentSpacing() < 0 ||
                y + r + mParameters.getComponentSpacing() >= mParameters.getMaxDeviceHeight()) {
            return false;
        }
        int left = x - r - mParameters.getComponentSpacing();
        int right = x + r + mParameters.getComponentSpacing();
        int top = y - r - mParameters.getComponentSpacing();
        int bottom = y + r + mParameters.getComponentSpacing();
        for (int i = left; i <= right; ++i) {
            for (int j = top; j <= bottom; ++j) {
                if (mDevice[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
