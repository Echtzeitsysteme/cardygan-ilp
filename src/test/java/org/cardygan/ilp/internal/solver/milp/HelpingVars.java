package org.cardygan.ilp.internal.solver.milp;

import org.cardygan.ilp.api.model.ArithExpr;
import org.cardygan.ilp.api.model.Model;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.cardygan.ilp.api.util.ExprDsl.*;
import static org.junit.Assert.assertEquals;

public class HelpingVars {


    @Test
    public void testHelpingVars() {
        Model model = new Model(new GurobiSolver());

        model.newConstraint(leq(mult(param(1), model.newBinaryVar("test")), param(1)));
        model.newConstraint(leq(mult(param(1), model.newBinaryVar("test2")), sum(param(2), param(1))));

        List<ArithExpr> expr = new ArrayList<>();
        expr.add(mult(param(1), model.newBinaryVar("a")));
        expr.add(mult(param(1), model.newBinaryVar("b")));
        model.newConstraint(eq(param(1), sum(expr)));
        model.newConstraint(leq(sum(expr), param(1)));

        model.optimize();

        assertEquals(4, model.getNumVars());
        assertEquals(4, model.getNumConstrs());

    }


}
