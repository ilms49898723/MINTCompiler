package com.github.ilms49898723.fluigi.errorhandler;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ErrorHandler {
    public static void printError(String filename, TerminalNode node, String message) {
        System.err.println("In file " + filename);
        System.err.println("At line " + node.getSymbol().getLine() + ":");
        System.err.println(node.getText() + ": " + message);
    }

    public static void printError(String filename, Token token, String message) {
        System.err.println("In file " + filename);
        System.err.println("At line " + token.getLine() + ":");
        System.err.println(token.getText() + ": " + message);
    }

    public static void printWarning(String filename, String token, String message) {
        System.out.println("Warning: In file " + filename);
        System.out.println("         " + token + ": " + message);
    }

    public static void printErrorAndExit(String filename, TerminalNode node, String message) {
        printError(filename, node, message);
        System.exit(1);
    }

    public static void printErrorAndExit(String filename, Token token, String message) {
        printError(filename, token, message);
        System.exit(1);
    }
}
