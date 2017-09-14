package com.github.ilms49898723.fluigi.routing;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.Channel;
import com.github.ilms49898723.fluigi.device.component.point.Point2DPair;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class HadlockRouter extends BaseRouter {
    private enum GridStatus {
        VISITED, OCCUPIED, BACKTRACKED, EMPTY
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

    private int[][] mPathMap;
    private int[][] mDetourMap;
    private int[][][] mTraceMap;
    private GridStatus[][] mMapStatus;

    public HadlockRouter(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
        mPathMap = new int[mParameters.getMaxDeviceWidth()][mParameters.getMaxDeviceHeight()];
        mDetourMap = new int[mParameters.getMaxDeviceWidth()][mParameters.getMaxDeviceHeight()];
        mTraceMap = new int[mParameters.getMaxDeviceWidth()][mParameters.getMaxDeviceHeight()][2];
        mMapStatus = new GridStatus[mParameters.getMaxDeviceWidth()][mParameters.getMaxDeviceHeight()];
        for (int i = 0; i < mPathMap.length; ++i) {
            for (int j = 0; j < mPathMap[i].length; ++j) {
                mPathMap[i][j] = 0;
                mDetourMap[i][j] = 0;
                mTraceMap[i][j][0] = mTraceMap[i][j][1] = 0;
                mMapStatus[i][j] = GridStatus.EMPTY;
            }
        }
    }

    @Override
    public void start() {
        preMark();
        List<List<DeviceEdge>> channelLists = new ArrayList<>();
        channelLists.add(mDeviceGraph.getAllFlowEdges(mSymbolTable));
        channelLists.add(mDeviceGraph.getAllControlEdges(mSymbolTable));
        for (List<DeviceEdge> channelList : channelLists) {
            for (DeviceEdge channel : channelList) {
                DeviceComponent source = mDeviceGraph.getEdgeSource(channel);
                DeviceComponent target = mDeviceGraph.getEdgeTarget(channel);
                BaseComponent src = mSymbolTable.get(source.getIdentifier());
                BaseComponent dst = mSymbolTable.get(target.getIdentifier());
                Channel chn = (Channel) mSymbolTable.get(channel.getChannel());
                System.out.println("Routing channel " + chn.getIdentifier());
                boolean routeResult = routeChannel(src, source.getPortNumber(), dst, target.getPortNumber(), chn);
                if (!routeResult) {
                    System.err.println("Route Error!");
                    return;
                }
            }
        }
    }

    private void preMark() {
        for (BaseComponent component : mSymbolTable.getComponentsExceptChannel()) {
            System.out.println("Premark component " + component.getIdentifier());
            System.out.println("  At " + component.getPosition());
            List<Point2DPair> points = component.getPoints();
            for (Point2DPair pointPair : points) {
                int delta = mParameters.getComponentSpacing() / 2;
                Point2D pA = pointPair.getPointA().subtract(delta, delta);
                Point2D pB = pointPair.getPointB().add(delta, delta);
                int x = (int) pA.getX();
                int y = (int) pA.getY();
                int w = (int) pB.subtract(pA).getX();
                int h = (int) pB.subtract(pA).getY();
                for (int i = 0; i < w; ++i) {
                    for (int j = 0; j < h; ++j) {
                        mPathMap[x + i][y + j] = -1;
                        mDetourMap[x + i][y + j] = -1;
                        mMapStatus[x + i][y + j] = GridStatus.VISITED;
                    }
                }
            }
        }
    }

    private boolean routeChannel(BaseComponent src, int srcPort, BaseComponent dst, int dstPort, Channel channel) {
        final int[][] moves = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        PriorityQueue<GridPoint> queue = new PriorityQueue<>();
        Point2D startPt = src.getPort(srcPort);
        Point2D endPt = dst.getPort(dstPort);
        GridPoint start = new GridPoint((int) startPt.getX(), (int) startPt.getY(), 0, 0);
        GridPoint end = new GridPoint((int) endPt.getX(), (int) endPt.getY(), 0, 0);
        queue.add(start);
        GridPoint current = start;
        mMapStatus[start.mX][start.mY] = GridStatus.OCCUPIED;
        mMapStatus[end.mX][end.mY] = GridStatus.EMPTY;
        mPathMap[start.mX][start.mY] = 0;
        mDetourMap[start.mX][start.mY] = 0;
        initialCleanUp(start, end, channel);
        while (!current.equalsPosition(end) && !queue.isEmpty()) {
            current = queue.poll();
            for (int i = 0; i < 4; ++i) {
                GridPoint next = current.add(moves[i][0], moves[i][1]);
                if (!isValidGrid(next, channel.getChannelWidth()) || mMapStatus[next.mX][next.mY] != GridStatus.EMPTY) {
                    continue;
                }
                if (next.manhattanDistance(end) >= current.manhattanDistance(end)) {
                    next.mDetourCost = current.mDetourCost + 1;
                } else {
                    next.mDetourCost = current.mDetourCost;
                }
                next.mPathCost = current.mPathCost + 1;
                mMapStatus[next.mX][next.mY] = GridStatus.OCCUPIED;
                mPathMap[next.mX][next.mY] = next.mPathCost;
                mDetourMap[next.mX][next.mY] = next.mDetourCost;
                mTraceMap[next.mX][next.mY][0] = -moves[i][0];
                mTraceMap[next.mX][next.mY][1] = -moves[i][1];
                queue.add(next);
            }
        }
        if (!current.equalsPosition(end)) {
            return false;
        }
        traceBack(end, start, channel);
        return true;
    }

    private void traceBack(GridPoint start, GridPoint end, Channel channel) {
        GridPoint current = start;
        while (!current.equalsPosition(end)) {
            int dX = mTraceMap[current.mX][current.mY][0];
            int dY = mTraceMap[current.mX][current.mY][1];
            GridPoint next = current.add(dX, dY);
            mMapStatus[next.mX][next.mY] = GridStatus.BACKTRACKED;
            Point2D leftTop = new Point2D(next.mX - channel.getChannelWidth() / 2, next.mY - channel.getChannelWidth() / 2);
            Point2D rightBottom = leftTop.add(channel.getChannelWidth(), channel.getChannelWidth());
            channel.addPoint(new Point2DPair(leftTop, rightBottom), Color.BLUE);
            current = next;
        }
        if (!current.equalsPosition(end)) {
            System.err.println("Trace back error!");
        } else {
            System.err.println("Trace back succeed!");
        }
    }

    private boolean isValidGrid(GridPoint pt, int channelWidth) {
        int w = mParameters.getChannelSpacing() * 2 + channelWidth;
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
        return true;
    }

    private void initialCleanUp(GridPoint start, GridPoint end, Channel channel) {
        int channelWidth = channel.getChannelWidth();
        int startX = start.mX - channelWidth / 2 - mParameters.getComponentSpacing();
        int startY = start.mY - channelWidth / 2 - mParameters.getComponentSpacing();
        for (int i = 0; i < startX + channelWidth + 2 * mParameters.getComponentSpacing(); ++i) {
            for (int j = 0; j < startY + channelWidth + 2 * mParameters.getComponentSpacing(); ++j) {
                mPathMap[i][j] = 0;
                mDetourMap[i][j] = 0;
                mMapStatus[i][j] = GridStatus.EMPTY;
            }
        }
        int endX = end.mX - channelWidth / 2 - mParameters.getComponentSpacing();
        int endY = end.mY - channelWidth / 2 - mParameters.getComponentSpacing();
        for (int i = 0; i < endX + channelWidth + 2 * mParameters.getComponentSpacing(); ++i) {
            for (int j = 0; j < endY + channelWidth + 2 * mParameters.getComponentSpacing(); ++j) {
                mPathMap[i][j] = 0;
                mDetourMap[i][j] = 0;
                mMapStatus[i][j] = GridStatus.EMPTY;
            }
        }
    }
}
