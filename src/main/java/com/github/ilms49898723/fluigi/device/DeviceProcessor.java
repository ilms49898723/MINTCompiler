package com.github.ilms49898723.fluigi.device;

import com.github.ilms49898723.fluigi.antlr.UFLexer;
import com.github.ilms49898723.fluigi.antlr.UFParser;
import com.github.ilms49898723.fluigi.mintparse.UFProcessor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class DeviceProcessor {
    private String mFilename;

    public DeviceProcessor(String filename) {
        mFilename = filename;
    }

    public void start() {
        try {
            UFLexer lexer = new UFLexer(CharStreams.fromFileName(mFilename));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            UFParser parser = new UFParser(tokens);
            UFParser.UfContext context = parser.uf();
            ParseTreeWalker walker = new ParseTreeWalker();
            UFProcessor processor = new UFProcessor();
            walker.walk(processor, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
