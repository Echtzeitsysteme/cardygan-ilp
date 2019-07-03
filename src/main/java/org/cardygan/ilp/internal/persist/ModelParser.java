package org.cardygan.ilp.internal.persist;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.cardygan.ilp.internal.persist.antlr.IlpLexer;
import org.cardygan.ilp.internal.persist.antlr.IlpParser;

import java.util.Collections;
import java.util.List;


public final class ModelParser {

    public static IlpParser.ModelContext parseFromString(String modelString) {
        return parseFromString(modelString, Collections.emptyList());
    }

    public static IlpParser.ModelContext parseFromString(String modelString, List<ANTLRErrorListener> listeners) {

        IlpLexer lexer = new IlpLexer(CharStreams.fromString(modelString));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        IlpParser parser = new IlpParser(tokens);

        for (ANTLRErrorListener listener : listeners) {
            parser.addErrorListener(listener);
        }

        return parser.model();
    }

}
