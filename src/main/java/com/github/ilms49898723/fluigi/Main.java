package com.github.ilms49898723.fluigi;

import com.github.ilms49898723.fluigi.argparse.ArgumentParser;
import com.github.ilms49898723.fluigi.processor.MainProcessor;

public class Main {
    public static void main(String[] args) {
        ArgumentParser argumentParser = new ArgumentParser(args);
        String inputFilename = argumentParser.getInputFilename();
        String paramFilename = argumentParser.getParamFilename();
        String outputFilename = argumentParser.getOutputFilename();
        MainProcessor mainProcessor = new MainProcessor(inputFilename, paramFilename, outputFilename);
        mainProcessor.start();
    }
}
