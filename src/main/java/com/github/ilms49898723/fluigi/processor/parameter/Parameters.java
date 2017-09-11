package com.github.ilms49898723.fluigi.processor.parameter;

import com.github.ilms49898723.fluigi.antlr.ParameterLexer;
import com.github.ilms49898723.fluigi.antlr.ParameterParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class Parameters {
    private int mMinResolution;
    private int mMaxDeviceWidth;
    private int mMaxDeviceHeight;
    private int mRoutingSpacing;
    private int mPortSpacing;
    private int mChannelSpacing;
    private int mValveSpacing;
    private int mComponentSpacing;

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
        mMinResolution = 20;
        mMaxDeviceWidth = 1000;
        mMaxDeviceHeight = 1000;
        mRoutingSpacing = 10;
        mPortSpacing = 10;
        mChannelSpacing = 10;
        mValveSpacing = 10;
        mComponentSpacing = 10;
    }

    public int getMinResolution() {
        return mMinResolution;
    }

    public void setMinResolution(int minResolution) {
        mMinResolution = minResolution;
    }

    public int getMaxDeviceWidth() {
        return mMaxDeviceWidth;
    }

    public void setMaxDeviceWidth(int maxDeviceWidth) {
        mMaxDeviceWidth = maxDeviceWidth;
    }

    public int getMaxDeviceHeight() {
        return mMaxDeviceHeight;
    }

    public void setMaxDeviceHeight(int maxDeviceHeight) {
        mMaxDeviceHeight = maxDeviceHeight;
    }

    public int getRoutingSpacing() {
        return mRoutingSpacing;
    }

    public void setRoutingSpacing(int routingSpacing) {
        mRoutingSpacing = routingSpacing;
    }

    public int getPortSpacing() {
        return mPortSpacing;
    }

    public void setPortSpacing(int portSpacing) {
        mPortSpacing = portSpacing;
    }

    public int getChannelSpacing() {
        return mChannelSpacing;
    }

    public void setChannelSpacing(int channelSpacing) {
        mChannelSpacing = channelSpacing;
    }

    public int getValveSpacing() {
        return mValveSpacing;
    }

    public void setValveSpacing(int valveSpacing) {
        mValveSpacing = valveSpacing;
    }

    public int getComponentSpacing() {
        return mComponentSpacing;
    }

    public void setComponentSpacing(int componentSpacing) {
        mComponentSpacing = componentSpacing;
    }
}
