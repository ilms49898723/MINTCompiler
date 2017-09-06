package com.github.ilms49898723.fluigi.device.component;

import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseComponent {
    private String mIdentifier;
    private ComponentType mType;
    private ComponentLayer mLayer;
    private Integer mWidth;
    private Integer mHeight;
    private Point2D mPosition;
    private Map<Integer, Point2D> mPorts;

    public BaseComponent(String identifier, ComponentType type, ComponentLayer layer) {
        mIdentifier = identifier;
        mType = type;
        mLayer = layer;
        mPosition = new Point2D(0, 0);
        mPorts = new HashMap<>();
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public ComponentType getType() {
        return mType;
    }

    public ComponentLayer getLayer() {
        return mLayer;
    }

    public Integer getWidth() {
        return mWidth;
    }

    public void setWidth(Integer width) {
        mWidth = width;
    }

    public Integer getHeight() {
        return mHeight;
    }

    public void setHeight(Integer height) {
        mHeight = height;
    }

    public Point2D getPosition() {
        return mPosition;
    }

    public void setPosition(Point2D position) {
        mPosition = position;
    }

    public void setPositionX(double x) {
        mPosition = new Point2D(x, mPosition.getY());
    }

    public void setPositionY(double y) {
        mPosition = new Point2D(mPosition.getX(), y);
    }

    public abstract void draw();
}
