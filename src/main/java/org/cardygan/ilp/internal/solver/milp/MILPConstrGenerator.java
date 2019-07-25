package org.cardygan.ilp.internal.solver.milp;

import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.bool.BoolExpr;

public interface MILPConstrGenerator {

    LinearConstr[] transform(final BoolExpr boolExpr, final Model model);

}
