package com.github.ilms49898723.fluigi.mintparse;

import com.github.ilms49898723.fluigi.antlr.UFBaseListener;
import com.github.ilms49898723.fluigi.antlr.UFParser;
import com.github.ilms49898723.fluigi.device.SymbolTable;
import com.github.ilms49898723.fluigi.device.component.ComponentLayer;
import com.github.ilms49898723.fluigi.device.component.Port;
import com.github.ilms49898723.fluigi.errorhandler.ErrorHandler;
import com.github.ilms49898723.fluigi.errorhandler.ErrorMessages;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class UFProcessor extends UFBaseListener {
    private boolean mValid;
    private String mFilename;
    private String mDeviceName;
    private Parameters mParameters;
    private SymbolTable mSymbolTable;
    private ComponentLayer mCurrentLayer;

    public UFProcessor(String filename, Parameters parameters, SymbolTable symbolTable) {
        mValid = true;
        mFilename = filename;
        mParameters = parameters;
        mSymbolTable = symbolTable;
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
            Port port = new Port(portIdentifier, mCurrentLayer);
            if (!mSymbolTable.put(portIdentifier, port)) {
                ErrorHandler.printError(mFilename, terminalNode, ErrorMessages.E_DUPLICATED_IDENTIFIER);
                setInvalid();
            }

        }
    }

    @Override
    public void exitChannelStat(UFParser.ChannelStatContext ctx) {

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
