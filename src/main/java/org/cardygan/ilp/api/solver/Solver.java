package org.cardygan.ilp.api.solver;


import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.Result;

public interface Solver {

    Result solve(Model model);

}
