package org.cardygan.ilp.model;

import org.cardygan.ilp.api.model.ArithExpr;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.solver.GurobiSolver;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.cardygan.ilp.api.util.ExprDsl.*;

public class HelpingVars {


    @Test
    public void testHelpingVars() {
        Model model = new Model();

        model.newConstraint(leq(mult(param(1), model.newBinaryVar("test")), param(1)));
        model.newConstraint(leq(mult(param(1), model.newBinaryVar("test2")), sum(param(2), param(1))));

        List<ArithExpr> expr = new ArrayList<>();
        expr.add(mult(param(1), model.newBinaryVar("1")));
        expr.add(mult(param(1), model.newBinaryVar("2")));
        model.newConstraint(eq(param(1), sum(expr)));
        model.newConstraint(leq(sum(expr), param(1)));

        model.solve(GurobiSolver.create().withLogging(true).build());

        model.copy();


    }


}
