package com.github.ilms49898723.fluigi.device.component.point;

import javafx.geometry.Point2D;

public class Point2DPair {
    private Point2D mPointA;
    private Point2D mPointB;

    public Point2DPair(Point2D pointA, Point2D pointB) {
        mPointA = pointA;
        mPointB = pointB;
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
        return "(" + mPointA.toString() + "," + mPointB.toString() + ")";
    }
}
