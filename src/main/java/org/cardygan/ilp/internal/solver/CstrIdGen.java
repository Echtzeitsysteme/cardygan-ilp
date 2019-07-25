package org.cardygan.ilp.internal.solver;

public class CstrIdGen extends IdGen {

    private final static String PREFIX = "ctmp";

    public CstrIdGen(Solver provider) {
        super(provider, PREFIX);
    }

    @Override
    public boolean hasId(Solver provider, String id) {
        return provider.hasConstraint(id);
    }

    public String genNew() {
        while (provider.hasConstraint(prefix + nameCounter.get())) {
            nameCounter.incrementAndGet();
        }
        return prefix + nameCounter.get();
    }
}
