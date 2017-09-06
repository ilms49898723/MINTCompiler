package com.github.ilms49898723.fluigi.device;

import com.github.ilms49898723.fluigi.antlr.UFLexer;
import com.github.ilms49898723.fluigi.antlr.UFParser;
import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.mintparse.UFProcessor;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DeviceProcessor {
    private String mInputFilename;
    private String mOutputFilename;
    private Parameters mParameters;
    private SymbolTable mSymbolTable;
    private DeviceGraph mDeviceGraph;
    private UFProcessor mProcessor;

    public DeviceProcessor(String inputFile, String outputFile, Parameters parameters) {
        mInputFilename = inputFile;
        mOutputFilename = outputFile;
        mParameters = parameters;
        mSymbolTable = new SymbolTable();
        mDeviceGraph = new DeviceGraph();
    }

    public void start() {
        parseMint();
        if (!mProcessor.isValid()) {
            System.exit(1);
        }
        mDeviceGraph.dump();
        // placement
        // routing
        // design rule check
        // output
        outputPng();
        outputSvg();
    }

    public void parseMint() {
        try {
            UFLexer lexer = new UFLexer(CharStreams.fromFileName(mInputFilename));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            UFParser parser = new UFParser(tokens);
            UFParser.UfContext context = parser.uf();
            ParseTreeWalker walker = new ParseTreeWalker();
            mProcessor = new UFProcessor(mInputFilename, mParameters, mSymbolTable, mDeviceGraph);
            walker.walk(mProcessor, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void outputPng() {
        BufferedImage image = new BufferedImage(
                (int) mParameters.getMaxDeviceWidth(),
                (int) mParameters.getMaxDeviceHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D png = (Graphics2D) image.getGraphics();
        for (String identifier : mSymbolTable.keySet()) {
            BaseComponent component = mSymbolTable.get(identifier);
            component.drawPng(png);
        }
        File outputFile = new File(mOutputFilename + ".png");
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void outputSvg() {
        SVGGraphics2D svg = new SVGGraphics2D(
                (int) mParameters.getMaxDeviceWidth(),
                (int) mParameters.getMaxDeviceHeight()
        );
        for (String identifier : mSymbolTable.keySet()) {
            BaseComponent component = mSymbolTable.get(identifier);
            component.drawSvg(svg);
        }
        try {
            SVGUtils.writeToSVG(new File(mOutputFilename + ".svg"), svg.getSVGElement());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
