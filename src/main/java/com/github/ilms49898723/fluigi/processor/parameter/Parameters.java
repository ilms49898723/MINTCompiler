package com.github.ilms49898723.fluigi.processor.parameter;

import com.github.ilms49898723.fluigi.antlr.ParameterLexer;
import com.github.ilms49898723.fluigi.antlr.ParameterParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class Parameters {
    private double mMinResolution;
    private double mMaxDeviceWidth;
    private double mMaxDeviceHeight;
    private double mRoutingSpacing;
    private double mPortSpacing;
    private double mChannelSpacing;
    private double mValveSpacing;
    private double mComponentSpacing;

    public Parameters() {
        initialize();
    }

    public Parameters(String filename) {
        initialize();
        try {
            ParameterLexer lexer = new ParameterLexer(CharStreams.fromFileName(filename));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ParameterParser parser = new ParameterParser(tokens);
            ParameterParser.ParameterContext context = parser.parameter();
            ParseTreeWalker walker = new ParseTreeWalker();
            ParameterProcessor processor = new ParameterProcessor(this, filename);
            walker.walk(processor, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        mMinResolution = 0.1;
        mMaxDeviceWidth = 500.0;
        mMaxDeviceHeight = 500.0;
        mRoutingSpacing = 1.0;
        mPortSpacing = 1.0;
        mChannelSpacing = 0.1;
        mValveSpacing = 1.0;
        mComponentSpacing = 1.0;
    }

    public double getMinResolution() {
        return mMinResolution;
    }

    public void setMinResolution(double minResolution) {
        mMinResolution = minResolution;
    }

    public double getMaxDeviceWidth() {
        return mMaxDeviceWidth;
    }

    public void setMaxDeviceWidth(double maxDeviceWidth) {
        mMaxDeviceWidth = maxDeviceWidth;
    }

    public double getMaxDeviceHeight() {
        return mMaxDeviceHeight;
    }

    public void setMaxDeviceHeight(double maxDeviceHeight) {
        mMaxDeviceHeight = maxDeviceHeight;
    }

    public double getRoutingSpacing() {
        return mRoutingSpacing;
    }

    public void setRoutingSpacing(double routingSpacing) {
        mRoutingSpacing = routingSpacing;
    }

    public double getPortSpacing() {
        return mPortSpacing;
    }

    public void setPortSpacing(double portSpacing) {
        mPortSpacing = portSpacing;
    }

    public double getChannelSpacing() {
        return mChannelSpacing;
    }

    public void setChannelSpacing(double channelSpacing) {
        mChannelSpacing = channelSpacing;
    }

    public double getValveSpacing() {
        return mValveSpacing;
    }

    public void setValveSpacing(double valveSpacing) {
        mValveSpacing = valveSpacing;
    }

    public double getComponentSpacing() {
        return mComponentSpacing;
    }

    public void setComponentSpacing(double componentSpacing) {
        mComponentSpacing = componentSpacing;
    }
}
