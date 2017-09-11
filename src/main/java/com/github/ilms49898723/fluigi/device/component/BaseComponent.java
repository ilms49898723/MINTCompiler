package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseComponent {
    private String mIdentifier;
    private ComponentType mType;
    private ComponentLayer mLayer;
    private int mWidth;
    private int mHeight;
    private Point2D mPosition;
    protected Map<Integer, Point2D> mPorts;

    public BaseComponent(String identifier, ComponentType type, ComponentLayer layer) {
        mIdentifier = identifier;
        mType = type;
        mLayer = layer;
        mPosition = new Point2D(0.0, 0.0);
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

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public Point2D getPosition() {
        return mPosition;
    }

    public int getPositionX() {
        return (int) mPosition.getX();
    }

    public int getPositionY() {
        return (int) mPosition.getY();
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

    public Point2D getLeftTopPoint() {
        return new Point2D(getPositionX() - getWidth() / 2, getPositionY() - getHeight() / 2);
    }

    public int getLeftTopX() {
        return (int) (getLeftTopPoint().getX());
    }

    public int getLeftTopY() {
        return (int) (getLeftTopPoint().getY());
    }

    public Point2D getRightBottomPoint() {
        return getLeftTopPoint().add(getWidth(), getHeight());
    }

    public int getRightBottomX() {
        return (int) (getRightBottomPoint().getX());
    }

    public int getRightBottomY() {
        return (int) (getRightBottomPoint().getY());
    }

    public boolean hasPort(int id) {
        return mPorts.containsKey(id);
    }

    public void addPort(int id, Point2D port) {
        mPorts.put(id, port);
    }

    public Point2D getPort(int id) {
        return mPorts.get(id).add(getPosition());
    }

    public void setPorts(Map<Integer, Point2D> ports) {
        mPorts = ports;
    }

    public Map<Integer, Point2D> getPorts() {
        return mPorts;
    }

    protected void rotateWidthHeight() {
        int temp = mHeight;
        mHeight = mWidth;
        mWidth = temp;
    }

    public abstract boolean supportRotate();

    public abstract void rotate();

    public abstract void draw(Graphics2D g);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseComponent component = (BaseComponent) o;

        if (getWidth() != component.getWidth()) {
            return false;
        }
        if (getHeight() != component.getHeight()) {
            return false;
        }
        if (getIdentifier() != null ? !getIdentifier().equals(component.getIdentifier()) : component.getIdentifier() != null) {
            return false;
        }
        if (getType() != component.getType()) {
            return false;
        }
        if (getLayer() != component.getLayer()) {
            return false;
        }
        if (getPosition() != null ? !getPosition().equals(component.getPosition()) : component.getPosition() != null) {
            return false;
        }
        return getPorts() != null ? getPorts().equals(component.getPorts()) : component.getPorts() == null;
    }

    @Override
    public int hashCode() {
        int result = getIdentifier() != null ? getIdentifier().hashCode() : 0;
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getLayer() != null ? getLayer().hashCode() : 0);
        result = 31 * result + getWidth();
        result = 31 * result + getHeight();
        result = 31 * result + (getPosition() != null ? getPosition().hashCode() : 0);
        result = 31 * result + (getPorts() != null ? getPorts().hashCode() : 0);
        return result;
    }
}
