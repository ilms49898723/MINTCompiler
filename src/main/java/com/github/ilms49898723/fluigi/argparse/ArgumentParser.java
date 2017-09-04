package com.github.ilms49898723.fluigi.argparse;

import org.apache.commons.cli.*;

public class ArgumentParser {
    private CommandLine mCommandLine;

    public ArgumentParser(String[] args) {
        Options options = new Options();
        Option ufInput = new Option("m", "MINT description file");
        Option paramInput = new Option("i", true, "parameter file");
        Option layoutOutput = new Option("o", true, "output filename");
        ufInput.setArgName("FILE");
        ufInput.setRequired(true);
        ufInput.setArgs(1);
        paramInput.setArgName("FILE");
        paramInput.setRequired(false);
        paramInput.setArgs(1);
        layoutOutput.setArgName("FILE");
        layoutOutput.setRequired(true);
        layoutOutput.setArgs(1);
        options.addOption(ufInput);
        options.addOption(paramInput);
        options.addOption(layoutOutput);
        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        try {
            mCommandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            helpFormatter.printHelp("Fluigi", options, true);
            System.exit(1);
        }
    }

    public String getInputFilename() {
        return mCommandLine.getOptionValue("m");
    }

    public String getParamFilename() {
        return mCommandLine.getOptionValue("i");
    }

    public String getOutputFilename() {
        return mCommandLine.getOptionValue("o");
    }
}
