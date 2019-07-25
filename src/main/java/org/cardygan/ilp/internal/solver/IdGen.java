package org.cardygan.ilp.internal.solver;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class IdGen {

    final Solver provider;
    final String prefix;

    AtomicInteger nameCounter = new AtomicInteger(0);

    public IdGen(Solver provider, String prefix) {
        this.provider = provider;
        this.prefix = prefix;
    }


    public String checkOrGenNewIfNull(final String name) {
        if (name == null) {
            return genNew();
        } else {
            if (hasId(provider, name)) {
                throw new IllegalStateException("Element with name \"" + name + "\" is already defined.");
            }
            return name;
        }
    }

    public abstract boolean hasId(Solver provider, String id);

    public abstract String genNew();


}
