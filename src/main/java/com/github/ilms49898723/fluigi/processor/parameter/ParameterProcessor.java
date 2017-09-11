package com.github.ilms49898723.fluigi.processor.parameter;

import com.github.ilms49898723.fluigi.antlr.ParameterBaseListener;
import com.github.ilms49898723.fluigi.antlr.ParameterParser;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ParameterProcessor extends ParameterBaseListener {
    private String mFilename;
    private Parameters mParameters;

    public ParameterProcessor(Parameters parameters, String filename) {
        mParameters = parameters;
        mFilename = filename;
    }

    @Override
    public void exitAssign(ParameterParser.AssignContext ctx) {
        String value = ctx.VALUE().getText();
        switch (ctx.ID().getText()) {
            case "minResolution":
                if (isInteger(ctx.VALUE())) {
                    mParameters.setMinResolution(Integer.parseInt(value));
                }
                break;
            case "maxDeviceWidth":
                if (isInteger(ctx.VALUE())) {
                    mParameters.setMaxDeviceWidth(Integer.parseInt(value));
                }
                break;
            case "maxDeviceHeight":
                if (isInteger(ctx.VALUE())) {
                    mParameters.setMaxDeviceHeight(Integer.parseInt(value));
                }
                break;
            case "routingSpacing":
                if (isInteger(ctx.VALUE())) {
                    mParameters.setRoutingSpacing(Integer.parseInt(value));
                }
                break;
            case "portSpacing":
                if (isInteger(ctx.VALUE())) {
                    mParameters.setPortSpacing(Integer.parseInt(value));
                }
                break;
            case "channelSpacing":
                if (isInteger(ctx.VALUE())) {
                    mParameters.setChannelSpacing(Integer.parseInt(value));
                }
                break;
            case "valveSpacing":
                if (isInteger(ctx.VALUE())) {
                    mParameters.setValveSpacing(Integer.parseInt(value));
                }
                break;
            case "componentSpacing":
                if (isInteger(ctx.VALUE())) {
                    mParameters.setComponentSpacing(Integer.parseInt(value));
                }
                break;
        }
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        super.visitErrorNode(node);
        System.exit(1);
    }

    private boolean isInteger(TerminalNode value) {
        Integer result = Ints.tryParse(value.getText());
        if (result == null) {
            System.err.println("In file " + mFilename);
            System.err.println("At line " + value.getSymbol().getLine() + ":");
            System.err.println(value + " is not a valid integer.");
        }
        return (result != null);
    }

    private boolean isFloat(TerminalNode value) {
        Double result = Doubles.tryParse(value.getText());
        if (result == null) {
            System.err.println("In file " + mFilename);
            System.err.println("At line " + value.getSymbol().getLine() + ":");
            System.err.println(value + " is not a valid floating number.");
        }
        return (result != null);
    }
}
