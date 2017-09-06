package com.github.ilms49898723.fluigi.errorhandler;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ErrorHandler {
    public static void printError(String filename, TerminalNode node, String message) {
        System.err.println("In file " + filename);
        System.err.println("At line " + node.getSymbol().getLine() + ":");
        System.err.println(node.getText() + ": " + message);
    }

    public static void printErrorAndExit(String filename, TerminalNode node, String message) {
        printError(filename, node, message);
        System.exit(1);
    }
}
