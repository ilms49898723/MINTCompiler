package com.github.ilms49898723.fluigi.device.component.point;

import javafx.geometry.Point2D;

public class Point2DPair {
    private Point2D mPointA;
    private Point2D mPointB;

    public Point2DPair(Point2D pointA, Point2D pointB) {
        mPointA = pointA;
        mPointB = pointB;
    }

    public Point2DPair addPointA(Point2D point) {
        return new Point2DPair(mPointA.add(point), mPointB);
    }

    public Point2DPair addPointB(Point2D point) {
        return new Point2DPair(mPointA, mPointB.add(point));
    }

    public Point2DPair addAll(Point2D point) {
        return new Point2DPair(mPointA.add(point), mPointB.add(point));
    }

    public void setPointA(Point2D pointA) {
        mPointA = pointA;
    }

    public Point2D getPointA() {
        return mPointA;
    }

    public void setPointB(Point2D pointB) {
        mPointB = pointB;
    }

    public Point2D getPointB() {
        return mPointB;
    }

    @Override
    public String toString() {
        return "(" + pointToString(mPointA) + "," + pointToString(mPointB) + ")";
    }

    private String pointToString(Point2D pt) {
        return "(" + ((int) pt.getX()) + ", " + ((int) pt.getY()) + ")";
    }
}
