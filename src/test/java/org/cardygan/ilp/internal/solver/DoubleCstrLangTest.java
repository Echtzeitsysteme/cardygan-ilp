package org.cardygan.ilp.internal.solver;

import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.DoubleVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.internal.solver.milp.BigMBasedCstrGenerator;
import org.cardygan.ilp.internal.solver.milp.GurobiSolver;
import org.cardygan.ilp.internal.solver.milp.SosBasedCstrGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.cardygan.ilp.api.util.ExprDsl.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Created by markus on 18.02.17.
 */
@RunWith(Parameterized.class)
public class DoubleCstrLangTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Solver.SolverBuilder[]{new GurobiSolver.GurobiSolverBuilder().withMILPConstrGenerator(new SosBasedCstrGenerator())},
                new Solver.SolverBuilder[]{new GurobiSolver.GurobiSolverBuilder().withMILPConstrGenerator(new BigMBasedCstrGenerator(1000))}
        );
    }


    private Solver.SolverBuilder data;

    public DoubleCstrLangTest(Solver.SolverBuilder data) {
        this.data = data;
    }

    private Model getModel() {
        Solver solver = data.build();
        return new Model(solver);
    }


    @Test
    public void complexImplication() {
        Model model = getModel();
        BinaryVar f0 = model.newBinaryVar("f0");
        Var w0 = model.newDoubleVar("w0");

        model.newConstraint("c1", impl(f0, eq(sum(w0), p(0.1))));
        model.newConstraint("c2", impl(not(f0), eq(p(0), w0)));

        model.newObjective(true, w0);


        Result res = model.optimize();

        assertSame(Result.SolverStatus.OPTIMAL, res.getStatus());
    }


    @Test
    public void testModel4() {
        Model model = getModel();

        BinaryVar v0 = model.newBinaryVar();
        DoubleVar v1 = model.newDoubleVar();

        model.newConstraint("c1", eq(v0, p(1)));
        model.newConstraint("c2", impl(v0, leq(v1, p(5.303304908059076))));
        model.newConstraint("c3", impl(not(v0), eq(v1, p(0))));
        model.newConstraint("c4", eq(v1, p(0.6931471805599453)));

        model.newObjective(true, v1);

        Result res = model.optimize();

        assertSame(Result.SolverStatus.OPTIMAL, res.getStatus());

        assertEquals(0.6931471805599453, model.getVal(v1), 0);
    }

}
