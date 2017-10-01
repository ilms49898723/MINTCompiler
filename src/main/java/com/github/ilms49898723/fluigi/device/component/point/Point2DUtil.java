package com.github.ilms49898723.fluigi.device.component.point;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.PortDirection;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
        double newX = (-1) * point.getY();
        double newY = point.getX();
        return new Point2D(newX, newY);
    }

    public static void rotateDevice(List<Point2DPair> points, Map<Integer, Point2D> ports, Map<Integer, PortDirection> directions) {
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
        for (int identifier : directions.keySet()) {
            PortDirection direction = directions.get(identifier);
            switch (direction) {
                case TOP:
                    directions.put(identifier, PortDirection.RIGHT);
                    break;
                case BOTTOM:
                    directions.put(identifier, PortDirection.LEFT);
                    break;
                case LEFT:
                    directions.put(identifier, PortDirection.TOP);
                    break;
                case RIGHT:
                    directions.put(identifier, PortDirection.BOTTOM);
                    break;
            }
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
            int w = (int) (points.get(i).getPointB().subtract(pt)).getX();
            int h = (int) (points.get(i).getPointB().subtract(pt)).getY();
            pt = pt.add(pivot);
            g.setColor(colors.get(i));
            g.fillRect((int) pt.getX(), (int) pt.getY(), w, h);
        }
    }

    public static void drawPort(Point2D portPosition, Point2D devicePosition, PortDirection direction, int channelWidth, Graphics2D g, Color color) {
        Point2D pa = Point2D.ZERO;
        Point2D pb = Point2D.ZERO;
        switch (direction) {
            case TOP:
                pa = portPosition.subtract(channelWidth / 2, 0.0);
                pb = devicePosition.add(channelWidth / 2, 0.0);
                break;
            case BOTTOM:
                pa = devicePosition.subtract(channelWidth / 2, 0.0);
                pb = portPosition.add(channelWidth / 2, 0.0);
                break;
            case LEFT:
                pa = portPosition.subtract(0.0, channelWidth / 2);
                pb = devicePosition.add(0.0, channelWidth / 2);
                break;
            case RIGHT:
                pa = devicePosition.subtract(0.0, channelWidth / 2);
                pb = portPosition.add(0.0, channelWidth / 2);
                break;
        }
        Point2D size = pb.subtract(pa);
        g.setColor(color);
        g.fillRect((int) pa.getX(), (int) pa.getY(), (int) size.getX(), (int) size.getY());
    }

    public static Point2DPair getPortPoints(Point2D portPosition, Point2D devicePosition, PortDirection direction, int channelWidth) {
        Point2D pa = Point2D.ZERO;
        Point2D pb = Point2D.ZERO;
        switch (direction) {
            case TOP:
                pa = portPosition.subtract(channelWidth / 2, 0.0);
                pb = devicePosition.add(channelWidth / 2, 0.0);
                break;
            case BOTTOM:
                pa = devicePosition.subtract(channelWidth / 2, 0.0);
                pb = portPosition.add(channelWidth / 2, 0.0);
                break;
            case LEFT:
                pa = portPosition.subtract(0.0, channelWidth / 2);
                pb = devicePosition.add(0.0, channelWidth / 2);
                break;
            case RIGHT:
                pa = devicePosition.subtract(0.0, channelWidth / 2);
                pb = portPosition.add(0.0, channelWidth / 2);
                break;
        }
        return new Point2DPair(pa, pb);
    }

    public static boolean isOverlapped(BaseComponent a, BaseComponent b, Parameters parameters) {
        int x1 = a.getPositionX();
        int y1 = a.getPositionY();
        int x2 = b.getPositionX();
        int y2 = b.getPositionY();
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        int w = a.getWidth() / 2 + b.getWidth() / 2 + parameters.getComponentSpacing();
        int h = a.getHeight() / 2 + b.getHeight() / 2 + parameters.getComponentSpacing();
        return (dx <= w && dy <= h);
    }

    public static Point2D calculateOverlap(BaseComponent a, BaseComponent b, Parameters parameters) {
        int x1 = a.getPositionX();
        int y1 = a.getPositionY();
        int x2 = b.getPositionX();
        int y2 = b.getPositionY();
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        int w = a.getWidth() / 2 + b.getWidth() / 2 + parameters.getComponentSpacing();
        int h = a.getHeight() / 2 + b.getHeight() / 2 + parameters.getComponentSpacing();
        boolean overlap = (dx <= w && dy <= h);
        return (overlap ? new Point2D(dx, dy) : Point2D.ZERO);
    }

    public static void adjustComponent(BaseComponent component, Parameters parameters) {
        int deviceWidth = parameters.getMaxDeviceWidth();
        int deviceHeight = parameters.getMaxDeviceHeight();
        int x = component.getPositionX();
        int y = component.getPositionY();
        if (x - component.getWidth() / 2 - parameters.getComponentSpacing() - parameters.getRoutingSpacing() - parameters.getChannelSpacing() < 0) {
            x = component.getWidth() / 2 + parameters.getComponentSpacing() + parameters.getRoutingSpacing() + parameters.getChannelSpacing();
        }
        if (x + component.getWidth() / 2 + parameters.getComponentSpacing() + parameters.getRoutingSpacing() + parameters.getChannelSpacing() >= deviceWidth) {
            x = deviceWidth - 1 - component.getWidth() / 2 - parameters.getComponentSpacing() - parameters.getRoutingSpacing() - parameters.getChannelSpacing();
        }
        if (y - component.getHeight() / 2 - parameters.getComponentSpacing() - parameters.getRoutingSpacing() - parameters.getChannelSpacing() < 0) {
            y = component.getHeight() / 2 + parameters.getComponentSpacing() + parameters.getRoutingSpacing() + parameters.getChannelSpacing();
        }
        if (y + component.getHeight() / 2 + parameters.getComponentSpacing() + parameters.getRoutingSpacing() + parameters.getChannelSpacing() >= deviceHeight) {
            y = deviceHeight - 1 - component.getHeight() / 2 - parameters.getComponentSpacing() - parameters.getRoutingSpacing() - parameters.getChannelSpacing();
        }
        component.setPosition(new Point2D(x, y));
    }

    public static int manhattanDistance(Point2D a, Point2D b) {
        Point2D delta = a.subtract(b);
        return ((int) Math.abs(delta.getX())) + ((int) Math.abs(delta.getY()));
    }

    public static void outputPng(String filename, SymbolTable symbolTable, Parameters parameters) {
        int width = parameters.getMaxDeviceWidth();
        int height = parameters.getMaxDeviceHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D png = (Graphics2D) image.getGraphics();
        png.setColor(Color.WHITE);
        png.fillRect(0, 0, width, height);
        png.setColor(Color.WHITE);
        png.fillRect(0, 0, width, height);
        drawComponent(symbolTable, png);
        File outputFile = new File(filename + ".png");
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void drawComponent(SymbolTable symbolTable, Graphics2D g) {
        for (BaseComponent component : symbolTable.getComponents(ComponentLayer.FLOW)) {
            component.draw(g);
        }
        for (BaseComponent component : symbolTable.getChannels(ComponentLayer.FLOW)) {
            component.draw(g);
        }
        for (BaseComponent component : symbolTable.getComponents(ComponentLayer.CONTROL)) {
            component.draw(g);
        }
        for (BaseComponent component : symbolTable.getChannels(ComponentLayer.CONTROL)) {
            component.draw(g);
        }
    }
}
