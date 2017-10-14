package com.github.ilms49898723.fluigi.routing.hadlock;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.Channel;
import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.valveplacer.ValvePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import com.github.ilms49898723.fluigi.routing.BaseRouter;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.*;
import java.util.List;

public class HadlockRouter extends BaseRouter {
    private enum GridStatus {
        VISITED, OCCUPIED, LAYER, EMPTY
    }

    private class GridPoint implements Comparable<GridPoint> {
        public int mX;
        public int mY;
        public int mPathCost;
        public int mDetourCost;

        public GridPoint(int x, int y, int pathCost, int detourCost) {
            mX = x;
            mY = y;
            mPathCost = pathCost;
            mDetourCost = detourCost;
        }

        public GridPoint add(int x, int y) {
            return new GridPoint(mX + x, mY + y, mPathCost, mDetourCost);
        }

        public GridPoint subtract(int x, int y) {
            return new GridPoint(mX - x, mY - y, mPathCost, mDetourCost);
        }

        public int manhattanDistance(GridPoint that) {
            return Math.abs(mX - that.mX) + Math.abs(mY - that.mY);
        }

        public boolean equalsPosition(GridPoint that) {
            return mX == that.mX && mY == that.mY;
        }

        @Override
        public int compareTo(GridPoint o) {
            if (Integer.compare(mDetourCost, o.mDetourCost) != 0) {
                return Integer.compare(mDetourCost, o.mDetourCost);
            } else {
                return Integer.compare(mPathCost, o.mPathCost);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            GridPoint gridPoint = (GridPoint) o;

            if (mX != gridPoint.mX) {
                return false;
            }
            if (mY != gridPoint.mY) {
                return false;
            }
            if (mPathCost != gridPoint.mPathCost) {
                return false;
            }
            return mDetourCost == gridPoint.mDetourCost;
        }

        @Override
        public int hashCode() {
            int result = mX;
            result = 31 * result + mY;
            result = 31 * result + mPathCost;
            result = 31 * result + mDetourCost;
            return result;
        }
    }

    private static final int MAX_ITERATION = 10;
    private static final int LAYER_COST = 10;
    private static final int BEND_COST = 250;
    private static final int sMoves[][] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private static int backMove(int moveId) {
        switch (moveId) {
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
                return 3;
            case 3:
                return 2;
            default:
                return -1;
        }
    }

    private int[][] mTraceMap;
    private GridStatus[][] mMapStatus;
    private Map<DeviceComponent, Point2D> mPortsPosition;

    public HadlockRouter(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
        mTraceMap = new int[mParameters.getMaxDeviceWidth()][mParameters.getMaxDeviceHeight()];
        mMapStatus = new GridStatus[mParameters.getMaxDeviceWidth()][mParameters.getMaxDeviceHeight()];
        mPortsPosition = new HashMap<>();
        for (DeviceComponent port : mDeviceGraph.vertexSet()) {
            Point2D position = mSymbolTable.get(port.getIdentifier()).getPort(port.getPortNumber());
            mPortsPosition.put(port, position);
        }
    }

    @Override
    public boolean routing() {
        initializeMap(true);
        routingPreMark();
        int routingCounter;
        List<DeviceEdge> channelList;
        boolean result;
        routingCounter = 0;
        channelList = new ArrayList<>(mDeviceGraph.getAllFlowEdges());
        do {
            result = routeChannels(channelList, routingCounter);
            ++routingCounter;
        } while (!result && routingCounter < MAX_ITERATION);
        if (!result) {
            System.err.println("Routing failed.");
            return false;
        }
        afterRouteLayer();
        ValvePlacer placer = new ValvePlacer(mSymbolTable, mDeviceGraph, mParameters);
        placer.placement();
        initializeMap(false);
        routingPreMark();
        routingCounter = 0;
        channelList = new ArrayList<>(mDeviceGraph.getAllControlEdges());
        do {
            result = routeChannels(channelList, routingCounter);
            ++routingCounter;
        } while (!result && routingCounter < MAX_ITERATION);
        if (!result) {
            System.err.println("Routing failed.");
            return false;
        }
        return true;
    }

    private boolean routeChannels(List<DeviceEdge> channels, int counter) {
        for (int i = 0; i < channels.size(); ++i) {
            DeviceEdge channel = channels.get(i);
            DeviceComponent source = mDeviceGraph.getEdgeSource(channel);
            DeviceComponent target = mDeviceGraph.getEdgeTarget(channel);
            Channel chn = (Channel) mSymbolTable.get(channel.getChannel());
            boolean routeResult = routeChannel(source, target, chn);
            if (!routeResult) {
                Point2D portA = mSymbolTable.get(source.getIdentifier()).getPort(source.getPortNumber());
                Point2D portB = mSymbolTable.get(target.getIdentifier()).getPort(target.getPortNumber());
                int radius = chn.getChannelWidth() * 2;
                Point2DUtil.outputPng("Route_" + counter, mSymbolTable, mParameters, radius, portA, portB);
                System.err.println("Routing failed on channel " + chn.getIdentifier());
                System.err.println("Try re-ordering and re-routing.");
                channels.remove(i);
                channels.add(0, channel);
                failedCleanUp(channels);
                return false;
            }
            afterRouteChannel();
        }
        return true;
    }

    private void initializeMap(boolean force) {
        for (int i = 0; i < mTraceMap.length; ++i) {
            for (int j = 0; j < mTraceMap[i].length; ++j) {
                mTraceMap[i][j] = -1;
                if (force) {
                    mMapStatus[i][j] = GridStatus.EMPTY;
                } else {
                    if (mMapStatus[i][j] != GridStatus.LAYER) {
                        mMapStatus[i][j] = GridStatus.EMPTY;
                    }
                }
            }
        }
    }

    private void routingPreMark() {
        for (BaseComponent component : mSymbolTable.getComponents()) {
            markComponent(component);
        }
    }

    private void markComponent(BaseComponent component) {
        List<Point2DPair> points = component.getPoints();
        for (Point2DPair pointPair : points) {
            Point2D pA = pointPair.getPointA();
            Point2D pB = pointPair.getPointB();
            int x = (int) pA.getX();
            int y = (int) pA.getY();
            int w = (int) pB.subtract(pA).getX();
            int h = (int) pB.subtract(pA).getY();
            for (int i = 0; i < w; ++i) {
                for (int j = 0; j < h; ++j) {
                    if (mMapStatus[x + i][y + j] != GridStatus.LAYER) {
                        mMapStatus[x + i][y + j] = GridStatus.VISITED;
                    }
                }
            }
        }
    }

    private boolean routeChannel(DeviceComponent source, DeviceComponent target, Channel channel) {
        BaseComponent src = mSymbolTable.get(source.getIdentifier());
        BaseComponent dst = mSymbolTable.get(target.getIdentifier());
        int srcPort = source.getPortNumber();
        int dstPort = target.getPortNumber();
        PriorityQueue<GridPoint> queue = new PriorityQueue<>();
        Point2D startPt = src.getPort(srcPort);
        Point2D endPt = dst.getPort(dstPort);
        GridPoint start = new GridPoint((int) startPt.getX(), (int) startPt.getY(), 0, 0);
        GridPoint end = new GridPoint((int) endPt.getX(), (int) endPt.getY(), 0, 0);
        cleanPort(start, channel.getChannelWidth());
        cleanPort(end, channel.getChannelWidth());
        queue.add(start);
        GridPoint current = start;
        mTraceMap[start.mX][start.mY] = -1;
        while (!current.equalsPosition(end) && !queue.isEmpty()) {
            current = queue.poll();
            int backOfCurrent = mTraceMap[current.mX][current.mY];
            for (int i = 0; i < 4; ++i) {
                GridPoint next = current.add(sMoves[i][0], sMoves[i][1]);
                if (!isValidGrid(next, source, target, channel.getChannelWidth())) {
                    continue;
                }
                if (next.manhattanDistance(end) >= current.manhattanDistance(end)) {
                    next.mDetourCost = current.mDetourCost + 1;
                } else {
                    next.mDetourCost = current.mDetourCost;
                }
                next.mDetourCost += (backMove(i) == backOfCurrent ? 0 : BEND_COST);
                next.mPathCost = current.mPathCost + 1;
                next.mPathCost += (mMapStatus[next.mX][next.mY] == GridStatus.LAYER) ? LAYER_COST : 0;
                mMapStatus[next.mX][next.mY] = GridStatus.OCCUPIED;
                mTraceMap[next.mX][next.mY] = backMove(i);
                queue.add(next);
            }
        }
        return current.equalsPosition(end) && traceBack(end, start, channel);
    }

    private boolean traceBack(GridPoint start, GridPoint end, Channel channel) {
        GridPoint current = start;
        while (!current.equalsPosition(end)) {
            int backId = mTraceMap[current.mX][current.mY];
            int dX = sMoves[backId][0];
            int dY = sMoves[backId][1];
            GridPoint next = current.add(dX, dY);
            mMapStatus[next.mX][next.mY] = GridStatus.VISITED;
            Point2D leftTop = new Point2D(next.mX - channel.getChannelWidth() / 2, next.mY - channel.getChannelWidth() / 2);
            Point2D rightBottom = leftTop.add(channel.getChannelWidth(), channel.getChannelWidth());
            channel.addPoint(new Point2DPair(leftTop, rightBottom), (channel.getLayer() == ComponentLayer.FLOW) ? Color.BLUE : Color.RED);
            current = next;
        }
        if (!current.equalsPosition(end)) {
            return false;
        } else {
            markComponent(channel);
            return true;
        }
    }

    private boolean isValidGrid(GridPoint pt, DeviceComponent source, DeviceComponent target, int channelWidth) {
        int x = pt.mX;
        int y = pt.mY;
        int w = mParameters.getChannelSpacing() * 2 + channelWidth;
        Point2D point = new Point2D(pt.mX, pt.mY);
        pt = pt.subtract(w / 2, w / 2);
        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < w; ++j) {
                if (pt.mX + i < 0 || pt.mY + j < 0 || pt.mX + i >= mParameters.getMaxDeviceWidth() || pt.mY + j >= mParameters.getMaxDeviceHeight()) {
                    return false;
                }
                if (mMapStatus[pt.mX + i][pt.mY + j] == GridStatus.VISITED) {
                    return false;
                }
            }
        }
        for (DeviceComponent key : mPortsPosition.keySet()) {
            if (key.equals(source) || key.equals(target)) {
                continue;
            }
            Point2D port = mPortsPosition.get(key);
            if (Math.abs(point.subtract(port.getX(), 0.0).getX()) < mParameters.getPortSpacing()
                    && Math.abs(point.subtract(0.0, port.getY()).getY()) < mParameters.getPortSpacing()) {
                return false;
            }
        }
        return (mMapStatus[x][y] == GridStatus.EMPTY || mMapStatus[x][y] == GridStatus.LAYER);
    }

    private void cleanPort(GridPoint pt, int channelWidth) {
        int w = mParameters.getChannelSpacing() * 2 + channelWidth;
        pt = pt.subtract(w / 2, w / 2);
        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < w; ++j) {
                mMapStatus[pt.mX + i][pt.mY + j] = GridStatus.EMPTY;
            }
        }
    }

    private void afterRouteChannel() {
        for (int i = 0; i < mMapStatus.length; ++i) {
            for (int j = 0; j < mMapStatus[i].length; ++j) {
                if (mMapStatus[i][j] != GridStatus.VISITED && mMapStatus[i][j] != GridStatus.LAYER) {
                    mMapStatus[i][j] = GridStatus.EMPTY;
                }
            }
        }
    }

    private void afterRouteLayer() {
        for (int i = 0; i < mMapStatus.length; ++i) {
            for (int j = 0; j < mMapStatus[i].length; ++j) {
                if (mMapStatus[i][j] == GridStatus.VISITED) {
                    mMapStatus[i][j] = GridStatus.LAYER;
                } else {
                    mMapStatus[i][j] = GridStatus.EMPTY;
                }
            }
        }
    }

    private void failedCleanUp(List<DeviceEdge> channels) {
        initializeMap(false);
        routingPreMark();
        List<BaseComponent> channelComponents = new ArrayList<>();
        for (DeviceEdge edge : channels) {
            channelComponents.add(mSymbolTable.get(edge.getChannel()));
        }
        for (BaseComponent component : channelComponents) {
            ((Channel) component).cleanup();
        }
    }
}
