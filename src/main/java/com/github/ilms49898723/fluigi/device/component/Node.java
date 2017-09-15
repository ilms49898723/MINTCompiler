package com.github.ilms49898723.fluigi.device.component;

import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.ComponentType;
import javafx.geometry.Point2D;

import java.awt.*;

public class Node extends BaseComponent {
    private int mLength;

    public Node(String identifier, ComponentLayer layer, int length) {
        super(identifier, layer, ComponentType.NODE);
        mLength = length;
        addPort(1, new Point2D(0.0, mLength / 2));
        addPort(2, new Point2D(mLength / 2, 0.0));
        addPort(3, new Point2D(0.0, -mLength / 2));
        addPort(4, new Point2D(-mLength / 2, 0.0));
        setWidth(mLength);
        setHeight(mLength);
    }

    @Override
    public boolean supportRotate() {
        return false;
    }

    @Override
    public void rotate() {

    }

    @Override
    public void draw(Graphics2D g) {
        int x = (int) getPosition().getX();
        int y = (int) getPosition().getY();
        int size = mLength / 2;
        g.setPaint(Color.BLUE);
        g.fillRect(x - size, y - size, mLength, mLength);
        g.fillRect(x - size, y - size, mLength, mLength);
    }
}
