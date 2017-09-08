package com.github.ilms49898723.fluigi.device;

import com.github.ilms49898723.fluigi.antlr.UFLexer;
import com.github.ilms49898723.fluigi.antlr.UFParser;
import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.mintparse.UFProcessor;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.placement.DummyPlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import com.github.ilms49898723.fluigi.routing.BaseRouter;
import com.github.ilms49898723.fluigi.routing.DummyRouter;
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
        BasePlacer placer = new DummyPlacer(mSymbolTable, mDeviceGraph, mParameters);
        placer.start();
        BaseRouter router = new DummyRouter(mSymbolTable, mDeviceGraph, mParameters);
        router.start();
        outputPng();
        outputSvg();
    }

    private void parseMint() {
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
        int width = (int) mParameters.getMaxDeviceWidth();
        int height = (int) mParameters.getMaxDeviceHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D png = (Graphics2D) image.getGraphics();
        png.setColor(Color.WHITE);
        png.fillRect(0, 0,width, height);
        for (String identifier : mSymbolTable.keySet()) {
            BaseComponent component = mSymbolTable.get(identifier);
            component.draw(png);
        }
        File outputFile = new File(mOutputFilename + ".png");
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void outputSvg() {
        int width = (int) mParameters.getMaxDeviceWidth();
        int height = (int) mParameters.getMaxDeviceHeight();
        SVGGraphics2D svg = new SVGGraphics2D(width, height);
        for (String identifier : mSymbolTable.keySet()) {
            BaseComponent component = mSymbolTable.get(identifier);
            component.draw(svg);
        }
        try {
            SVGUtils.writeToSVG(new File(mOutputFilename + ".svg"), svg.getSVGElement());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
