package com.github.ilms49898723.fluigi.device;

import com.github.ilms49898723.fluigi.antlr.UFLexer;
import com.github.ilms49898723.fluigi.antlr.UFParser;
import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.point.Point2DUtil;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.graph.GraphEdge;
import com.github.ilms49898723.fluigi.device.graph.GraphUtil;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.errorhandler.ErrorHandler;
import com.github.ilms49898723.fluigi.errorhandler.ErrorMessages;
import com.github.ilms49898723.fluigi.mintparse.UFProcessor;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.placement.forcedirected.ForceDirectedPlacer;
import com.github.ilms49898723.fluigi.placement.graphpartition.GraphPartitionPlacer;
import com.github.ilms49898723.fluigi.placement.terminalpropagation.TerminalPropagator;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import com.github.ilms49898723.fluigi.routing.BaseRouter;
import com.github.ilms49898723.fluigi.routing.hadlock.HadlockRouter;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.jgrapht.Graph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        prePlacementCleanUp();
        BasePlacer placer = new GraphPartitionPlacer(mSymbolTable, mDeviceGraph, mParameters);
        placer.placement();

        for (BaseComponent component : mSymbolTable.getComponents()) {
            Point2DUtil.adjustComponent(component, mParameters);
        }

        BasePlacer iterativePlacer = new ForceDirectedPlacer(mSymbolTable, mDeviceGraph, mParameters);

        BasePlacer propagator = new TerminalPropagator(mSymbolTable, mDeviceGraph, mParameters);
        propagator.placement();
        BaseRouter router = new HadlockRouter(mSymbolTable, mDeviceGraph, mParameters);
        router.routing();

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

    private void prePlacementCleanUp() {
        Graph<String, GraphEdge> graph = GraphUtil.constructGraph(mDeviceGraph.getGraph());
        List<String> toRemove = new ArrayList<>();
        List<DeviceComponent> toRemoveVertex = new ArrayList<>();
        for (String identifier : mSymbolTable.keySet()) {
            if (graph.containsVertex(identifier) && graph.edgesOf(identifier).isEmpty()) {
                ErrorHandler.printWarning(mInputFilename, identifier, ErrorMessages.E_NO_CHANNEL_CONNECTED);
                toRemove.add(identifier);
            }
        }
        for (DeviceComponent component : mDeviceGraph.vertexSet()) {
            if (toRemove.contains(component.getIdentifier())) {
                toRemoveVertex.add(component);
            }
        }
        for (String id : toRemove) {
            mSymbolTable.remove(id);
        }
        for (DeviceComponent vertex : toRemoveVertex) {
            mDeviceGraph.removeVertex(vertex);
        }
    }

    private void outputPng() {
        int width = mParameters.getMaxDeviceWidth();
        int height = mParameters.getMaxDeviceHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D png = (Graphics2D) image.getGraphics();
        png.setColor(Color.WHITE);
        png.fillRect(0, 0,width, height);
        drawComponent(png);
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
        drawComponent(svg);
        try {
            SVGUtils.writeToSVG(new File(mOutputFilename + ".svg"), svg.getSVGElement());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawComponent(Graphics2D png) {
        for (BaseComponent component : mSymbolTable.getComponents(ComponentLayer.FLOW)) {
            component.draw(png);
        }
        for (BaseComponent component : mSymbolTable.getChannels(ComponentLayer.FLOW)) {
            component.draw(png);
        }
        for (BaseComponent component : mSymbolTable.getComponents(ComponentLayer.CONTROL)) {
            component.draw(png);
        }
        for (BaseComponent component : mSymbolTable.getChannels(ComponentLayer.CONTROL)) {
            component.draw(png);
        }
    }
}
