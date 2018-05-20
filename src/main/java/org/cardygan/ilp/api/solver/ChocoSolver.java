package org.cardygan.ilp.api.solver;

import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.Model;

public class ChocoSolver implements Solver {

    @Override
    public Result solve(Model model) {
        org.chocosolver.solver.Model chocoModel = new org.chocosolver.solver.Model("my first problem");

//        chocoModel.intVar
        return null;
    }
}
