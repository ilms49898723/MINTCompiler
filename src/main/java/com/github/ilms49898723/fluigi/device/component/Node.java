package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import com.github.ilms49898723.fluigi.device.symbol.PortDirection;
import javafx.geometry.Point2D;

import java.awt.*;

public class Node extends BaseComponent {
    private int mLength;

    public Node(String identifier, ComponentLayer layer, int length) {
        super(identifier, layer, ComponentType.NODE);
        mLength = length;
        addPort(1, new Point2D(0.0, -mLength / 2), PortDirection.TOP);
        addPort(2, new Point2D(0.0, mLength / 2), PortDirection.BOTTOM);
        addPort(3, new Point2D(-mLength / 2, 0.0), PortDirection.LEFT);
        addPort(4, new Point2D(mLength / 2, 0.0), PortDirection.RIGHT);
        setWidth(mLength);
        setHeight(mLength);
    }

    @Override
    public boolean supportRotate() {
        return false;
    }

    @Override
    public void draw(Graphics2D g) {
        int x = (int) getPosition().getX();
        int y = (int) getPosition().getY();
        int size = mLength / 2;
        g.setPaint(Color.BLUE);
        g.fillRect(x - size, y - size, mLength, mLength);
        g.fillRect(x - size, y - size, mLength, mLength);
        for (int portId : mPortChannelWidth.keySet()) {
            if (mPortChannelWidth.get(portId) != -1) {
                Point2DUtil.drawPort(
                        getPort(portId),
                        getPosition(),
                        mPortDirection.get(portId),
                        mPortChannelWidth.get(portId),
                        g,
                        (getLayer() == ComponentLayer.FLOW) ? Color.BLUE : Color.RED
                );
            }
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
        if (!super.equals(o)) {
            return false;
        }

        Node node = (Node) o;

        return mLength == node.mLength;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + mLength;
        return result;
    }
}
