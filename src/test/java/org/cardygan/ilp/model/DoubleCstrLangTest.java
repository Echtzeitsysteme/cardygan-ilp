package org.cardygan.ilp.model;

import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.*;
import org.cardygan.ilp.api.solver.ChocoSolver;
import org.cardygan.ilp.api.solver.CplexSolver;
import org.cardygan.ilp.api.solver.GurobiSolver;
import org.cardygan.ilp.api.solver.Solver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.cardygan.ilp.api.util.ExprDsl.*;
import static org.junit.Assert.*;

/**
 * Created by markus on 18.02.17.
 */
@RunWith(Parameterized.class)
public class DoubleCstrLangTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Solver[]{GurobiSolver.create().build()},
                new Solver[]{CplexSolver.create().build()});
    }

    private Solver solver;

    public DoubleCstrLangTest(Solver solver) {
        this.solver = solver;
    }


    @Test
    public void complexImplication() {
        Model model = new Model();
        BinaryVar f0 = model.newBinaryVar("f0");
        Var w0 = model.newDoubleVar("w0");

        model.newConstraint("c1", impl(f0, eq(sum(w0), p(0.1))));
        model.newConstraint("c2", impl(not(f0), eq(p(0), w0)));

        model.newObjective(true, w0);


        Result res = model.solve(solver);

        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);
    }


    @Test
    public void testModel4() {
        Model model = new Model();

        BinaryVar v0 = model.newBinaryVar();
        DoubleVar v1 = model.newDoubleVar();

        model.newConstraint("c1", eq(v0, p(1)));
        model.newConstraint("c2", impl(v0, leq(v1, p(5.303304908059076))));
        model.newConstraint("c3", impl(not(v0), eq(v1, p(0))));
        model.newConstraint("c4", eq(v1, p(0.6931471805599453)));

        model.newObjective(true, v1);

        Result res = model.solve(solver);

        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);

        assertEquals(new Double(0.6931471805599453), res.getSolutions().get(v1));
    }

}
