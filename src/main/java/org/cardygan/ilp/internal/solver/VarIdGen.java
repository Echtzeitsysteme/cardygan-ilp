package org.cardygan.ilp.internal.solver;

public class VarIdGen extends IdGen {

    private final static String PREFIX = "v";


    public VarIdGen(Solver provider) {
        super(provider, PREFIX);
    }

    @Override
    public boolean hasId(Solver provider, String id) {
        return provider.hasVar(id);
    }

    public String genNew() {
        while (provider.hasVar(prefix + nameCounter.get())) {
            nameCounter.incrementAndGet();
        }
        return prefix + nameCounter.get();
    }
}
