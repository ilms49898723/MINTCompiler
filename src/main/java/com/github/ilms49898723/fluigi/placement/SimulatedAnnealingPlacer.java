package com.github.ilms49898723.fluigi.placement;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.Random;

public class SimulatedAnnealingPlacer extends BasePlacer {
    private static final int CHANNEL_COST = 1;
    private static final int OVERLAP_COST = 10;
    private static final int NUM_MOVES_PER_TEMP_PER_COMPONENT = 50;

    private Random mRandom;

    public SimulatedAnnealingPlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
        super(symbolTable, deviceGraph, parameters);
        mRandom = new Random();
    }

    @Override
    public void start() {
        double cost = 0;
        double temp = 0;
        for (int i = 0; i < 20; ++i) {
            int c = randomPlacement();
            System.out.println("Initial placement #" + (i + 1) + ": " + c);
            temp += c;
            cost = c;
        }
        temp /= 20;
        do {
            doPlacement(cost, temp);
        } while (hasOverlap());
    }

    private void doPlacement(double cost, double temp) {
        int rangeX = mParameters.getMaxDeviceWidth();
        int rangeY = mParameters.getMaxDeviceHeight();
        List<BaseComponent> components = mSymbolTable.getComponentsExceptChannel();
        while (temp > 0.005 * cost / components.size() && temp > 2) {
            int rejected = 0;
            for (int i = 0; i < components.size() * NUM_MOVES_PER_TEMP_PER_COMPONENT; ++i) {
                BaseComponent c = components.get(mRandom.nextInt(components.size()));
                Point2D original = c.getPosition();
                if (mRandom.nextDouble() <= 0.3) {
                    int newX = (int) (c.getPositionX() + nextRandom() * rangeX);
                    c.setPositionX(newX);
                    Point2DUtil.adjustComponent(c, mParameters);
                } else if (mRandom.nextDouble() <= 0.6) {
                    int newY = (int) (c.getPositionY() + nextRandom() * rangeY);
                    c.setPositionY(newY);
                    Point2DUtil.adjustComponent(c, mParameters);
                } else {
                    if (c.supportRotate()) {
                        c.rotate();
                        Point2DUtil.adjustComponent(c, mParameters);
                    }
                }
                double newCost = getCost();
                if (newCost > cost) {
                    if (mRandom.nextDouble() >= Math.exp(cost - newCost) / temp) {
                        c.setPosition(original);
                        ++rejected;
                    }
                }
            }
            double rateAccept = ((double) (components.size() * NUM_MOVES_PER_TEMP_PER_COMPONENT - rejected)) / (components.size() * NUM_MOVES_PER_TEMP_PER_COMPONENT);
            if (rateAccept > 0.96) {
                temp *= 0.5;
            } else if (rateAccept > 0.8) {
                temp *= 0.9;
            } else if (rateAccept > 0.15) {
                temp *= 0.95;
            } else {
                temp *= 0.8;
            }
            System.out.println("cost: " + getCost() + ", accept rate: " + String.format("%.2f", rateAccept));
        }
    }

    private int randomPlacement() {
        Random random = new Random();
        for (BaseComponent component : mSymbolTable.getComponentsExceptChannel()) {
            component.setPositionX(random.nextInt(mParameters.getMaxDeviceWidth()));
            component.setPositionY(random.nextInt(mParameters.getMaxDeviceHeight()));
            Point2DUtil.adjustComponent(component, mParameters);
        }
        return getCost();
    }

    private int getCost() {
        int result = 0;
        for (DeviceEdge edge : mDeviceGraph.getAllEdges()) {
            DeviceComponent source = mDeviceGraph.getEdgeSource(edge);
            DeviceComponent target = mDeviceGraph.getEdgeTarget(edge);
            Point2D srcPort = mSymbolTable.get(source.getIdentifier()).getPort(source.getPortNumber());
            Point2D dstPort = mSymbolTable.get(target.getIdentifier()).getPort(target.getPortNumber());
            Point2D distance = dstPort.subtract(srcPort);
            result += CHANNEL_COST * (Math.abs((int) distance.getX()) + Math.abs((int) distance.getY())) / mParameters.getMinResolution();
        }
        List<BaseComponent> components = mSymbolTable.getComponentsExceptChannel();
        for (int i = 0; i < components.size(); ++i) {
            for (int j = 0; j < components.size(); ++j) {
                if (i != j) {
                    BaseComponent a = components.get(i);
                    BaseComponent b = components.get(j);
                    Point2D overlap = Point2DUtil.calculateOverlap(a, b, mParameters);
                    result += OVERLAP_COST * ((int) overlap.getX()) * ((int) overlap.getY());
                }
            }
        }
        return result;
    }

    private boolean hasOverlap() {
        List<BaseComponent> components = mSymbolTable.getComponentsExceptChannel();
        for (int i = 0; i < components.size(); ++i) {
            for (int j = 0; j < components.size(); ++j) {
                if (i != j) {
                    BaseComponent a = components.get(i);
                    BaseComponent b = components.get(j);
                    Point2D overlap = Point2DUtil.calculateOverlap(a, b, mParameters);
                    int x = (int) overlap.getX();
                    int y = (int) overlap.getY();
                    if (x != 0 || y != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private double nextRandom() {
        double v = mRandom.nextDouble();
        return (v * 2) - 1.0;
    }
}
