package com.github.ilms49898723.fluigi.mintparse;

import com.github.ilms49898723.fluigi.antlr.UFBaseListener;
import com.github.ilms49898723.fluigi.antlr.UFParser;
import com.github.ilms49898723.fluigi.device.component.*;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
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
            int radius = Integer.parseInt(ctx.radiusParam().radius.getText());
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
        int width = Integer.parseInt(ctx.widthParam().width.getText());
        Channel channel = new Channel(channelId, mCurrentLayer, width);
        if (!mSymbolTable.put(channelId, channel)) {
            ErrorHandler.printError(mFilename, ctx.ufname().ID(), ErrorMessages.E_DUPLICATED_IDENTIFIER);
            setInvalid();
        }
        String sourceId = ctx.component1.getText();
        String targetId = ctx.component2.getText();
        int sourcePort = Integer.parseInt(ctx.port1.getText());
        int targetPort = Integer.parseInt(ctx.port2.getText());
        mDeviceGraph.addEdge(sourceId, sourcePort, targetId, targetPort, channelId);
    }

    @Override
    public void exitNodeStat(UFParser.NodeStatContext ctx) {
        for (UFParser.UfnameContext ufname : ctx.ufnames().ufname()) {
            TerminalNode terminalNode = ufname.ID();
            String nodeIdentifier = terminalNode.getText();
            Node node = new Node(nodeIdentifier, mCurrentLayer, (int) mParameters.getMinResolution());
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
    public void exitCellTrapStat(UFParser.CellTrapStatContext ctx) {
        int numChambers = 0;
        int chamberWidth = 0;
        int chamberLength = 0;
        int chamberSpacing = 0;
        int channelWidth = 0;
        for (UFParser.CellTrapStatParamContext par : ctx.cellTrapStatParams().cellTrapStatParam()) {
            if (par.numChambersParam() != null) {
                numChambers = Integer.parseInt(par.numChambersParam().num_chambers.getText());
            }
            if (par.chamberWidthParam() != null) {
                chamberWidth = Integer.parseInt(par.chamberWidthParam().chamber_width.getText());
            }
            if (par.chamberLengthParam() != null) {
                chamberLength = Integer.parseInt(par.chamberLengthParam().chamber_length.getText());
            }
            if (par.chamberSpacingParam() != null) {
                chamberSpacing = Integer.parseInt(par.chamberSpacingParam().chamber_spacing.getText());
            }
            if (par.channelWidthParam() != null) {
                channelWidth = Integer.parseInt(par.channelWidthParam().channel_width.getText());
            }
        }
        if (ctx.type.getText().equals("SQUARE CELL TRAP")) {
            for (UFParser.UfnameContext node : ctx.ufnames().ufname()) {
                String identifier = node.ID().getText();
                SquareCellTrap cellTrap = new SquareCellTrap(identifier, mCurrentLayer, chamberWidth, chamberLength, channelWidth);
                if (!mSymbolTable.put(identifier, cellTrap)) {
                    ErrorHandler.printError(mFilename, node.ID(), ErrorMessages.E_DUPLICATED_IDENTIFIER);
                    setInvalid();
                }
                for (int i = 1; i <= 4; ++i) {
                    mDeviceGraph.addVertex(identifier, i);
                }
            }
        } else {
            for (UFParser.UfnameContext node : ctx.ufnames().ufname()) {
                String identifier = node.ID().getText();
                LongCellTrap cellTrap = new LongCellTrap(identifier, mCurrentLayer, numChambers, chamberWidth, chamberLength, chamberSpacing, channelWidth);
                if (!mSymbolTable.put(identifier, cellTrap)) {
                    ErrorHandler.printError(mFilename, node.ID(), ErrorMessages.E_DUPLICATED_IDENTIFIER);
                    setInvalid();
                }
                for (int i = 1; i <= 2; ++i) {
                    mDeviceGraph.addVertex(identifier, i);
                }
            }
        }
    }

    @Override
    public void exitMixerStat(UFParser.MixerStatContext ctx) {
        String mixerIdentifier = ctx.ufname().ID().getText();
        int numOfBends = 0;
        int bendSpacing = 0;
        int bendLength = 0;
        int channelWidth = 0;
        for (UFParser.MixerStatParamContext par : ctx.mixerStatParams().mixerStatParam()) {
            if (par.numBendsParam() != null) {
                numOfBends = Integer.parseInt(par.numBendsParam().number_bends.getText());
            }
            if (par.bendSpacingParam() != null) {
                bendSpacing = Integer.parseInt(par.bendSpacingParam().bend_spacing.getText());
            }
            if (par.bendLengthParam() != null) {
                bendLength = Integer.parseInt(par.bendLengthParam().bend_length.getText());
            }
            if (par.channelWidthParam() != null) {
                channelWidth = Integer.parseInt(par.channelWidthParam().channel_width.getText());
            }
        }
        Mixer mixer = new Mixer(mixerIdentifier, mCurrentLayer, numOfBends, bendSpacing, bendLength, channelWidth);
        if (!mSymbolTable.put(mixerIdentifier, mixer)) {
            ErrorHandler.printError(mFilename, ctx.ufname().ID(), ErrorMessages.E_DUPLICATED_IDENTIFIER);
            setInvalid();
        }
        mDeviceGraph.addVertex(mixerIdentifier, 1);
        mDeviceGraph.addVertex(mixerIdentifier, 2);
    }
    
    @Override
    public void exitTreeStat(UFParser.TreeStatContext ctx) {
    	String treeIdentifier = ctx.ufname().ID().getText();
    	int inNum = 0;
    	int outNum = 0;
    	int spacing = 0;
    	int channelWidth = 0;
    	if (ctx.n1 != null) {
    		inNum = Integer.parseInt(ctx.n1.getText());
    	}
    	if (ctx.n2 != null) {
    		outNum = Integer.parseInt(ctx.n2.getText());
    	}
    	for (UFParser.TreeStatParamContext par : ctx.treeStatParams().treeStatParam()) {
    		if (par.spacingParam() != null) {
    			spacing = Integer.parseInt(par.spacingParam().spacing.getText());
    		}
    		if (par.flowChannelWidthParam() != null) {
    			channelWidth = Integer.parseInt(par.flowChannelWidthParam().flow_channel_width.getText());
    		}
    	}
    	
    	Tree tree = new Tree(treeIdentifier, mCurrentLayer, inNum, outNum, spacing, channelWidth);
    	if(!mSymbolTable.put(treeIdentifier, tree)) {
    		ErrorHandler.printError(mFilename, ctx.ufname().ID(), ErrorMessages.E_DUPLICATED_IDENTIFIER);
    		setInvalid();
    	}
    	for (int i = 1 ; i <= inNum + outNum ; i++) {
    		mDeviceGraph.addVertex(treeIdentifier, i);
    	}
    }
    
    @Override
    public void exitRotaryStat(UFParser.RotaryStatContext ctx) {
    	String rotaryIdentifier = ctx.ufname().ID().getText();
    	int radius = 0;
    	int flowChannelWidth = 0;
    	for (UFParser.RotaryStatParamContext par : ctx.rotaryStatParams().rotaryStatParam()) {
    		if(par.radiusParam() != null) {
    			radius = Integer.parseInt(par.radiusParam().radius.getText());
    		}
    		if(par.flowChannelWidthParam() != null) {
    			flowChannelWidth = Integer.parseInt(par.flowChannelWidthParam().flow_channel_width.getText());
    		}
    	}
    	
    	RotaryPump rotarypump = new RotaryPump(rotaryIdentifier, mCurrentLayer, radius, flowChannelWidth);
    	if(!mSymbolTable.put(rotaryIdentifier, rotarypump)) {
    		ErrorHandler.printError(mFilename, ctx.ufname().ID(), ErrorMessages.E_DUPLICATED_IDENTIFIER);
    		setInvalid();
    	}
    	for (int i = 1 ; i <= 2 ; i++) {
    		mDeviceGraph.addVertex(rotaryIdentifier, i);
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
