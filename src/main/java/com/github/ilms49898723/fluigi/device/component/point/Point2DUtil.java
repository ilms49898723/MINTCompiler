package com.github.ilms49898723.fluigi.device.component.point;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.List;
import java.util.Map;

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

    public static void rotateDevice(List<Point2DPair> points, Map<Integer, Point2D> ports) {
        for (int i = 0; i < points.size(); ++i) {
            Point2D newPointA = Point2DUtil.rotate(points.get(i).getPointA());
            Point2D newPointB = Point2DUtil.rotate(points.get(i).getPointB());
            Point2D pointA = new Point2D(newPointB.getX(), newPointA.getY());
            Point2D pointB = new Point2D(newPointA.getX(), newPointB.getY());
            points.set(i, new Point2DPair(pointA, pointB));
        }
        for (int identifier : ports.keySet()) {
            Point2D pt = ports.get(identifier);
            ports.put(identifier, Point2DUtil.rotate(pt));
        }
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

    public static Point2D calculateOverlap(BaseComponent a, BaseComponent b, Parameters parameters) {
        int leftA = a.getLeftTopX() - parameters.getComponentSpacing() - parameters.getRoutingSpacing();
        int rightA = a.getRightBottomX() + parameters.getComponentSpacing() + parameters.getRoutingSpacing();
        int topA = a.getLeftTopY() - parameters.getComponentSpacing() - parameters.getRoutingSpacing();
        int bottomA = a.getRightBottomY() + parameters.getComponentSpacing() + parameters.getRoutingSpacing();
        int leftB = b.getLeftTopX() - parameters.getComponentSpacing() - parameters.getRoutingSpacing();
        int rightB = b.getRightBottomX() + parameters.getComponentSpacing() + parameters.getRoutingSpacing();
        int topB = b.getLeftTopY() - parameters.getComponentSpacing() - parameters.getRoutingSpacing();
        int bottomB = b.getRightBottomY() + parameters.getComponentSpacing() + parameters.getRoutingSpacing();
        if (rightA >= leftB && leftA <= rightB && bottomA >= topB && topA <= bottomB) {
            int x = Math.min(rightA, rightB) - Math.max(leftA, leftB);
            int y = Math.min(bottomA, bottomB) - Math.max(topA, topB);
            x /= parameters.getMinResolution();
            y /= parameters.getMinResolution();
            return new Point2D(x, y);
        } else {
            return new Point2D(0, 0);
        }
    }

    public static void adjustComponent(BaseComponent component, Parameters parameters) {
        int deviceWidth = parameters.getMaxDeviceWidth();
        int deviceHeight = parameters.getMaxDeviceHeight();
        int x = component.getPositionX();
        int y = component.getPositionY();
        if (x - component.getWidth() / 2 - parameters.getComponentSpacing() - parameters.getRoutingSpacing() < 0) {
            x = component.getWidth() / 2 + parameters.getComponentSpacing() + parameters.getRoutingSpacing();
        }
        if (x + component.getWidth() / 2 + parameters.getComponentSpacing() + parameters.getRoutingSpacing() >= deviceWidth) {
            x = deviceWidth - 1 - component.getWidth() / 2 - parameters.getComponentSpacing() - parameters.getRoutingSpacing();
        }
        if (y - component.getHeight() / 2 - parameters.getComponentSpacing() - parameters.getRoutingSpacing() < 0) {
            y = component.getHeight() / 2 + parameters.getComponentSpacing() + parameters.getRoutingSpacing();
        }
        if (y + component.getHeight() / 2 + parameters.getComponentSpacing() + parameters.getRoutingSpacing() >= deviceHeight) {
            y = deviceHeight - 1 - component.getHeight() / 2 - parameters.getComponentSpacing() - parameters.getRoutingSpacing();
        }
        component.setPosition(new Point2D(x, y));
    }
}
