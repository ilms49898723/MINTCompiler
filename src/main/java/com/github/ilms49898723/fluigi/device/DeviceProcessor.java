package com.github.ilms49898723.fluigi.device;

import com.github.ilms49898723.fluigi.antlr.UFLexer;
import com.github.ilms49898723.fluigi.antlr.UFParser;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.mintparse.UFProcessor;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class DeviceProcessor {
    private String mFilename;
    private Parameters mParameters;
    private SymbolTable mSymbolTable;
    private DeviceGraph mDeviceGraph;
    private UFProcessor mProcessor;

    public DeviceProcessor(String filename, Parameters parameters) {
        mFilename = filename;
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
    }

    public void parseMint() {
        try {
            UFLexer lexer = new UFLexer(CharStreams.fromFileName(mFilename));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            UFParser parser = new UFParser(tokens);
            UFParser.UfContext context = parser.uf();
            ParseTreeWalker walker = new ParseTreeWalker();
            mProcessor = new UFProcessor(mFilename, mParameters, mSymbolTable, mDeviceGraph);
            walker.walk(mProcessor, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
