package com.github.ilms49898723.fluigi.routing.hadlock;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoutingOrder {
    private static class EdgeInfo implements Comparable<EdgeInfo> {
        public DeviceEdge mEdge;
        public int mLength;
        public int mCost;

        public EdgeInfo(DeviceEdge edge, int length) {
            mEdge = edge;
            mLength = length;
            mCost = 0;
        }

        public void addCost(int cost) {
            mCost += cost;
        }

        @Override
        public int compareTo(EdgeInfo o) {
            if (Integer.compare(mCost, o.mCost) != 0) {
                return Integer.compare(mCost, o.mCost);
            } else {
                return Integer.compare(mLength, o.mLength);
            }
        }
    }
    private SymbolTable mSymbolTable;

    public RoutingOrder(SymbolTable symbolTable) {
        mSymbolTable = symbolTable;
    }

    public List<DeviceEdge> getRoutingOrder(List<DeviceEdge> edges) {
        List<Point2D> ports = new ArrayList<>();
        for (DeviceEdge edge : edges) {
            BaseComponent src = mSymbolTable.get(edge.getSource().getIdentifier());
            BaseComponent dst = mSymbolTable.get(edge.getTarget().getIdentifier());
            int srcPort = edge.getSource().getPortNumber();
            int dstPort = edge.getTarget().getPortNumber();
            ports.add(src.getPort(srcPort));
            ports.add(dst.getPort(dstPort));
        }
        List<DeviceEdge> result = new ArrayList<>();
        List<EdgeInfo> edgeInfo = new ArrayList<>();
        for (DeviceEdge edge : edges) {
            BaseComponent src = mSymbolTable.get(edge.getSource().getIdentifier());
            BaseComponent dst = mSymbolTable.get(edge.getTarget().getIdentifier());
            int srcPort = edge.getSource().getPortNumber();
            int dstPort = edge.getTarget().getPortNumber();
            Point2D lt = getRectPoints(src.getPort(srcPort), dst.getPort(dstPort)).get(0);
            Point2D rb = getRectPoints(src.getPort(srcPort), dst.getPort(dstPort)).get(1);
            Point2D mid = rb.midpoint(lt);
            EdgeInfo info = new EdgeInfo(
                    edge,
                    (int) ((rb.getX() - lt.getX()) + (rb.getY() - lt.getY()))
            );
            int w = (int) ((rb.getX() - lt.getX()) / 2);
            int h = (int) ((rb.getY() - lt.getY()) / 2);
            for (Point2D pt : ports) {
                int xDis = (int) Math.abs(pt.subtract(mid).getX());
                int yDis = (int) Math.abs(pt.subtract(mid).getY());
                if (xDis <= w && yDis <= h) {
                    info.addCost(1);
                }
            }
            edgeInfo.add(info);
        }
        edgeInfo.sort(EdgeInfo::compareTo);
        for (EdgeInfo info : edgeInfo) {
            result.add(info.mEdge);
        }
        return result;
    }

    private List<Point2D> getRectPoints(Point2D a, Point2D b) {
        Point2D pa = new Point2D(
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY())
        );
        Point2D pb = new Point2D(
                Math.max(a.getX(), b.getX()),
                Math.max(a.getY(), b.getY())
        );
        return Arrays.asList(pa, pb);
    }
}
