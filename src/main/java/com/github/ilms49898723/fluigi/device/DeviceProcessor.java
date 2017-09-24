package com.github.ilms49898723.fluigi.device;

import com.github.ilms49898723.fluigi.antlr.UFLexer;
import com.github.ilms49898723.fluigi.antlr.UFParser;
import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.mintparse.UFProcessor;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.placement.graphpartition.GraphPartitionPlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import com.github.ilms49898723.fluigi.routing.BaseRouter;
import com.github.ilms49898723.fluigi.routing.HadlockRouter;
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
    private static final int MAX_ITERATION = 10;

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
        BasePlacer placer = new GraphPartitionPlacer(mSymbolTable, mDeviceGraph, mParameters);
        placer.placement();

        for (BaseComponent component : mSymbolTable.getComponents()) {
            Point2DUtil.adjustComponent(component, mParameters);
        }

        BaseRouter router = new HadlockRouter(mSymbolTable, mDeviceGraph, mParameters);
        router.routing();

//        int counter = 0;
//        boolean placementAndRoutingResult = false;
//        while (counter < MAX_ITERATION) {
//            ++counter;
//            boolean result;
//            BasePlacer placer = new SimulatedAnnealingPlacer(mSymbolTable, mDeviceGraph, mParameters);
//            result = placer.placement();
//            if (!result) {
//                continue;
//            }
//            BaseRouter router = new HadlockRouter(mSymbolTable, mDeviceGraph, mParameters);
//            result = router.routing();
//            if (result) {
//                placementAndRoutingResult = true;
//                break;
//            }
//        }
//        if (!placementAndRoutingResult) {
//            System.err.println("Placement or Routing error!");
//        }
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
        int width = mParameters.getMaxDeviceWidth();
        int height = mParameters.getMaxDeviceHeight();
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
        int width = mParameters.getMaxDeviceWidth();
        int height = mParameters.getMaxDeviceHeight();
        SVGGraphics2D svg = new SVGGraphics2D(width, height);
        svg.setColor(Color.WHITE);
        svg.fillRect(0, 0, width, height);
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
