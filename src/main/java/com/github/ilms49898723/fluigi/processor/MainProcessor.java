package com.github.ilms49898723.fluigi.processor;

import com.github.ilms49898723.fluigi.device.DeviceProcessor;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;

public class MainProcessor {
    private String mInputFilename;
    private String mParamFilename;
    private String mOutputFilename;
    private Parameters mParameters;

    public MainProcessor(String inputFilename, String paramFilename, String outputFilename) {
        mInputFilename = inputFilename;
        mParamFilename = paramFilename;
        mOutputFilename = outputFilename;
    }

    public void start() {
        parseParameter();
        parseMint();
    }

    private void parseParameter() {
        if (mParamFilename != null) {
            mParameters = new Parameters(mParamFilename);
        } else {
            mParameters = new Parameters();
        }
    }

    private void parseMint() {
        DeviceProcessor deviceProcessor = new DeviceProcessor(mInputFilename, mOutputFilename, mParameters);
        deviceProcessor.start();
    }
}
