package org.cardygan.ilp.api;


import org.cardygan.ilp.api.expr.Var;

public interface Solver {

    Result solveProblem(Model model);

    double getVal(Var targetVar);

}
