package org.cardygan.ilp.internal.persist;

import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.internal.persist.antlr.IlpParser;
import org.cardygan.ilp.internal.persist.visitors.TopLevelVisitor;
import org.cardygan.ilp.internal.util.ResourceUtil;

import java.io.*;

public final class ModelReader {


    public static Model readFromFile(File file) throws IOException {
        return readFromInputStream(new FileInputStream(file));
    }

    public static Model readFromClasspath(String cpFile) throws IOException {
        return readFromInputStream(ModelReader.class.getResourceAsStream(cpFile));
    }

    public static Model readFromInputStream(InputStream inputStream) throws IOException {
        return readFromString(ResourceUtil.transformStreamToString(inputStream));
    }

    public static Model readFromString(String modelString) {

        IlpParser.ModelContext ctx = ModelParser.parseFromString(modelString);

        Model ret = new Model();

        TopLevelVisitor visitor = new TopLevelVisitor(ret);
        ctx.accept(visitor);

        return ret;
    }

}
