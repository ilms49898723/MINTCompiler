package com.github.ilms49898723.fluigi.mintparse;

import com.github.ilms49898723.fluigi.antlr.UFBaseListener;
import com.github.ilms49898723.fluigi.antlr.UFParser;
import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.Channel;
import com.github.ilms49898723.fluigi.device.component.Node;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.component.Port;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.errorhandler.ErrorHandler;
import com.github.ilms49898723.fluigi.errorhandler.ErrorMessages;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UFProcessor extends UFBaseListener {
    private boolean mValid;
    private String mFilename;
    private String mDeviceName;
    private Parameters mParameters;
    private SymbolTable mSymbolTable;
    private DeviceGraph mDeviceGraph;
    private ComponentLayer mCurrentLayer;

    public UFProcessor(String filename, Parameters parameters, SymbolTable symbolTable, DeviceGraph deviceGraph) {
        mValid = true;
        mFilename = filename;
        mParameters = parameters;
        mSymbolTable = symbolTable;
        mDeviceGraph = deviceGraph;
        mCurrentLayer = ComponentLayer.UNDEFINED;
    }

    public String getFilename() {
        return mFilename;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    @Override
    public void exitHeader(UFParser.HeaderContext ctx) {
        mDeviceName = ctx.ufname().getText();
    }

    @Override
    public void enterFlowBlock(UFParser.FlowBlockContext ctx) {
        mCurrentLayer = ComponentLayer.FLOW;
    }

    @Override
    public void exitFlowBlock(UFParser.FlowBlockContext ctx) {
        mCurrentLayer = ComponentLayer.UNDEFINED;
    }

    @Override
    public void enterControlBlock(UFParser.ControlBlockContext ctx) {
        mCurrentLayer = ComponentLayer.CONTROL;
    }

    @Override
    public void exitControlBlock(UFParser.ControlBlockContext ctx) {
        mCurrentLayer = ComponentLayer.UNDEFINED;
    }

    @Override
    public void exitPortStat(UFParser.PortStatContext ctx) {
        for (UFParser.UfnameContext ufname : ctx.ufnames().ufname()) {
            TerminalNode terminalNode = ufname.ID();
            String portIdentifier = terminalNode.getText();
            double radius = Double.parseDouble(ctx.radiusParam().radius.getText());
            Port port = new Port(portIdentifier, mCurrentLayer, radius);
            if (!mSymbolTable.put(portIdentifier, port)) {
                ErrorHandler.printError(mFilename, terminalNode, ErrorMessages.E_DUPLICATED_IDENTIFIER);
                setInvalid();
            }
            for (int i = 1; i <= 4; ++i) {
                mDeviceGraph.addVertex(portIdentifier, i);
            }
        }
    }

    @Override
    public void exitChannelStat(UFParser.ChannelStatContext ctx) {
        List<Token> idCheckList = new ArrayList<>();
        List<Token> portCheckList = new ArrayList<>();
        idCheckList.addAll(Arrays.asList(ctx.component1, ctx.component2));
        portCheckList.addAll(Arrays.asList(ctx.port1, ctx.port2));
        for (int i = 0; i < idCheckList.size(); ++i) {
            Token idToken = idCheckList.get(i);
            Token portToken = portCheckList.get(i);
            if (!mSymbolTable.containsKey(idToken.getText())) {
                ErrorHandler.printError(mFilename, idToken, ErrorMessages.E_UNDEFINED_IDENTIFIER);
                setInvalid();
            }
            BaseComponent component = mSymbolTable.get(idToken.getText());
            if (!component.hasPort(Integer.parseInt(portToken.getText()))) {
                ErrorHandler.printError(mFilename, portToken, ErrorMessages.E_UNDEFINED_PORT);
                setInvalid();
            }
        }
        String channelId = ctx.ufname().ID().getText();
        Channel channel = new Channel(channelId, mCurrentLayer);
        if (!mSymbolTable.put(channelId, channel)) {
            ErrorHandler.printError(mFilename, ctx.ufname().ID(), ErrorMessages.E_DUPLICATED_IDENTIFIER);
        }
        String sourceId = ctx.component1.getText();
        String targetId = ctx.component2.getText();
        int sourcePort = Integer.parseInt(ctx.port1.getText());
        int targetPort = Integer.parseInt(ctx.port2.getText());
        mDeviceGraph.addEdge(sourceId, sourcePort, targetId, targetPort);
    }

    @Override
    public void exitNodeStat(UFParser.NodeStatContext ctx) {
        for (UFParser.UfnameContext ufname : ctx.ufnames().ufname()) {
            TerminalNode terminalNode = ufname.ID();
            String nodeIdentifier = terminalNode.getText();
            Node node = new Node(nodeIdentifier, mCurrentLayer);
            if (!mSymbolTable.put(nodeIdentifier, node)) {
                ErrorHandler.printError(mFilename, terminalNode, ErrorMessages.E_DUPLICATED_IDENTIFIER);
                setInvalid();
            }
            for (int i = 1; i <= 4; ++i) {
                mDeviceGraph.addVertex(nodeIdentifier, i);
            }
        }
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        super.visitErrorNode(node);
        System.exit(1);
    }

    private void setInvalid() {
        mValid = false;
    }

    public boolean isValid() {
        return mValid;
    }
}
