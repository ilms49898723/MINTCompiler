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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseComponent that = (BaseComponent) o;

        if (getIdentifier() != null ? !getIdentifier().equals(that.getIdentifier()) : that.getIdentifier() != null) {
            return false;
        }
        if (getType() != that.getType()) {
            return false;
        }
        if (getLayer() != that.getLayer()) {
            return false;
        }
        if (getWidth() != null ? !getWidth().equals(that.getWidth()) : that.getWidth() != null) {
            return false;
        }
        if (getHeight() != null ? !getHeight().equals(that.getHeight()) : that.getHeight() != null) {
            return false;
        }
        if (getPosition() != null ? !getPosition().equals(that.getPosition()) : that.getPosition() != null) {
            return false;
        }
        return mPorts != null ? mPorts.equals(that.mPorts) : that.mPorts == null;
    }

    @Override
    public int hashCode() {
        int result = getIdentifier() != null ? getIdentifier().hashCode() : 0;
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getLayer() != null ? getLayer().hashCode() : 0);
        result = 31 * result + (getWidth() != null ? getWidth().hashCode() : 0);
        result = 31 * result + (getHeight() != null ? getHeight().hashCode() : 0);
        result = 31 * result + (getPosition() != null ? getPosition().hashCode() : 0);
        result = 31 * result + (mPorts != null ? mPorts.hashCode() : 0);
        return result;
    }
}
