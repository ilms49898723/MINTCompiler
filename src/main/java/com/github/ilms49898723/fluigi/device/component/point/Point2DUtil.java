package com.github.ilms49898723.fluigi.device.component.point;

import javafx.geometry.Point2D;

import java.awt.*;
import java.util.List;

public class Point2DUtil {
    public static void subtractPoints(List<Point2DPair> points, Point2D pivot) {
        for (int i = 0; i < points.size(); ++i) {
            Point2D newPtA = points.get(i).getPointA().subtract(pivot);
            Point2D newPtB = points.get(i).getPointB().subtract(pivot);
            points.set(i, new Point2DPair(newPtA, newPtB));
        }
    }

    public static Point2D rotate(Point2D point) {
        int degree = 90;
        double newX = Math.cos(Math.toRadians(degree)) * point.getX() - Math.sin(Math.toRadians(degree)) * point.getY();
        double newY = Math.sin(Math.toRadians(degree)) * point.getX() + Math.cos(Math.toRadians(degree)) * point.getY();
        return new Point2D(newX, newY);
    }

    public static void drawPoint(Point2D point, Color color, Point2D pivot, int radius, Graphics2D g) {
        point = point.add(pivot);
        g.setColor(color);
        g.fillOval((int) point.getX() - radius / 2, (int) point.getY() - radius / 2, radius, radius);
    }

    public static void drawPoints(List<Point2DPair> points, List<Color> colors, Point2D pivot, Graphics2D g) {
        for (int i = 0; i < points.size(); ++i) {
            Point2D pt = points.get(i).getPointA();
            int w = (int) (points.get(i).getPointB().getX() - pt.getX());
            int h = (int) (points.get(i).getPointB().getY() - pt.getY());
            pt = new Point2D(pt.getX() + pivot.getX(), pt.getY() + pivot.getY());
            g.setColor(colors.get(i));
            g.fillRect((int) pt.getX(), (int) pt.getY(), w, h);
        }
    }
}
