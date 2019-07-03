package org.cardygan.ilp.model;

import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.Constraint;
import org.cardygan.ilp.api.model.IntVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Objective;
import org.cardygan.ilp.api.solver.ChocoSolver;
import org.cardygan.ilp.api.solver.CplexSolver;
import org.cardygan.ilp.api.solver.GurobiSolver;
import org.cardygan.ilp.api.solver.Solver;
import org.cardygan.ilp.internal.util.IlpUtil;
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
public class IntCstrLangTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Solver[]{new GurobiSolver()},
                new Solver[]{new CplexSolver()},
                new Solver[]{new ChocoSolver.ChocoSolverBuilder()
                        .withObjectiveBounds(-99999, 99999)
                        .withDefaultIntVarBounds(0, 99999)
                        .build()});
    }


    private Solver solver;

    public IntCstrLangTest(Solver solver) {
        this.solver = solver;
    }

    @Test
    public void basicAnd() {
        Model model = new Model();

        IntVar v1 = model.newIntVar("v1");
        IntVar v2 = model.newIntVar("v2");
        model.newConstraint("name", and(leq(v1, param(3)), geq(v2, param(2))));

        model.newObjective(true, sum(v1));

        // test without M method
        Result res = model.solve(solver);
        assertEquals(new Double(3), res.getObjVal().get());
        assertTrue(res.getSolutions().get(v2) >= 2);

        // test with M method
        model.setM(1000);
        res = model.solve(solver);
        assertEquals(new Double(3), res.getObjVal().get());
        assertTrue(res.getSolutions().get(v2) >= 2);
    }

    @Test
    public void basicNot() {
        Model model = new Model();


        IntVar v1 = model.newIntVar("v1");
        model.newConstraint("name", not(geq(v1, param(3))));

        model.newObjective(true, sum(v1));

        Result res = model.solve(solver);
        assertEquals(new Double(2), res.getObjVal().get());
        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);
    }

    @Test
    public void equality() {
        Model model = new Model();

        IntVar f1 = model.newIntVar("f1");

        model.newConstraint("cstr1", eq(param(3), f1));

        model.newObjective(true, sum(f1));

        Result res = model.solve(solver);

        assertEquals(new Double(3), res.getObjVal().get());
        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);
    }

    @Test
    public void equality2() {
        Model model = new Model();


        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");

        model.newConstraint("cstr1", eq(f2, f1));
        model.newConstraint("cstr2", leq(f2, param(3)));

        model.newObjective(true, sum(f1));

        Result res = model.solve(solver);

        assertEquals(new Double(3), res.getObjVal().get());
        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);
    }

    @Test
    public void equality3() {
        Model model = new Model();


        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");

        model.newConstraint("cstr1", and(eq(f2, f1), leq(f2, param(3))));


        Objective obj = model.newObjective(true, sum(f1));

        Result res = model.solve(solver);

        assertEquals(new Double(3), res.getObjVal().get());
        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);
    }

    @Test
    public void equality4() {
        Model model = new Model();

        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");

        model.newConstraint("cstr1", and(eq(param(3), sum(f1, f2)), eq(f2, param(1))));


        model.newObjective(true, sum(f1));

        Result res = model.solve(solver);

        assertEquals(new Double(2), res.getObjVal().get());
        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);
        assertFalse(res.getStatistics().getStatus() == Result.SolverStatus.UNBOUNDED);
    }

    @Test
    public void implication1() {
        Model model = new Model();

        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");

        model.newConstraint("cstr1", impl(eq(param(3), f1), eq(f2, param(11))));
        model.newConstraint("cstr2", eq(param(3), f1));

        Objective obj = model.newObjective(true, sum(f2));

        Result res = model.solve(solver);

        assertEquals(new Double(11), res.getObjVal().get());
        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);
        assertFalse(res.getStatistics().getStatus() == Result.SolverStatus.UNBOUNDED);
    }

    @Test
    public void implication2() throws NoSuchFieldException, IllegalAccessException {


        Model model = new Model();

        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");
        IntVar f3 = model.newIntVar("f3");

        model.newConstraint("cstr1", impl(and(eq(param(3), sum(f1, f2))),
                eq(param(11), f3)));
        model.newConstraint("cstr2", eq(param(2), f1));
        model.newConstraint("cstr2", eq(param(1), f2));

        model.newObjective(true, sum(f3));

        Result res = model.solve(solver);

        assertEquals(new Double(11), res.getObjVal().get());
        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);
        assertFalse(res.getStatistics().getStatus() == Result.SolverStatus.UNBOUNDED);
    }


    @Test
    public void implication3() {
        Model model = new Model();

        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");
        IntVar f3 = model.newIntVar("f3");

        model.newConstraint("cstr1", impl(and(eq(param(2), f1), eq(param(1), f2)),
                eq(param(11), f3)));
        model.newConstraint("cstr2", eq(param(2), f1));
        model.newConstraint("cstr2", eq(param(1), f2));

        model.newObjective(true, sum(f3));

        Result res = model.solve(solver);

        assertEquals(new Double(11), res.getObjVal().get());
        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);
        assertFalse(res.getStatistics().getStatus() == Result.SolverStatus.UNBOUNDED);
    }

    @Test
    public void implication() {
        Model model = new Model();

        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");
        IntVar f3 = model.newIntVar("f3");

        Constraint cstr1 = model.newConstraint("cstr1",
                impl(and(eq(param(3), sum(f1, f2)), eq(f1, param(1))),
                        eq(param(3), f3)));

        model.newConstraint("cstr2", eq(f1, param(1)));
        model.newConstraint("cstr3", eq(f2, param(2)));

        model.newObjective(true, sum(f3));

        Result res = model.solve(solver);

        assertEquals(new Double(3), res.getObjVal().get());
        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);
    }


    @Test
    public void testModel1() {
        Model model = new Model();
        IntVar f0 = model.newIntVar("f0", 0, 5);
        IntVar f1 = model.newIntVar("f1", 0, 7);
        IntVar f2 = model.newIntVar("f2", 0, 8);

        model.newConstraint("c1",
                impl(and(eq(p(3), sum(f0, f1)), eq(f0, p(1))), eq(f2, p(2)))
        );


        model.newObjective(true, f0);

        Result res = model.solve(solver);

        assertTrue(5d == res.getObjVal().get());
        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);
    }

    @Test
    public void testModel2() {
        Model model = new Model();
        IntVar f0 = model.newIntVar("f0", 0, 5);
        IntVar f2 = model.newIntVar("f2", 0, 8);
        IntVar f3 = model.newIntVar("f3");

        model.newConstraint("c1",
                and(
                        impl(eq(p(3), f2), eq(f3, p(2))),
                        impl(not(eq(p(3), f2)), eq(f3, p(3)))
                )
        );
        model.newConstraint("c2", eq(f2, p(3)));

        model.newObjective(true, f0);

        Result res = model.solve(solver);

        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);
        assertEquals(new Double(2d), res.getSolutions().get(f3));
    }

    @Test
    public void testModel3() {
        Model model = new Model();
        IntVar v0 = model.newIntVar();
        IntVar v1 = model.newIntVar();
        IntVar v2 = model.newIntVar();
        IntVar v3 = model.newIntVar();
        IntVar v4 = model.newIntVar();
        IntVar v5 = model.newIntVar();
        IntVar v6 = model.newIntVar();


        model.newConstraint("c1", eq(v4, v1));
        model.newConstraint("c2", eq(v5, v3));
        model.newConstraint("c3", eq(v6, v3));

        model.newConstraint("c4",
                and(
                        impl(eq(v4, p(2)), eq(v5, p(3))),
                        impl(not(eq(v4, p(2))), eq(v6, p(1)))
                )
        );
        model.newConstraint("c5", and(leq(p(1), v0), leq(v0, p(1))));
        model.newConstraint("c6", leq(mult(p(1), v0), sum(v1, v2, v3)));
        model.newConstraint("c7", and(leq(mult(p(1), v0), v1), leq(v1, mult(p(6), v0))));
        model.newConstraint("c8", and(leq(mult(p(1), v0), v2), leq(v2, mult(p(3), v0))));
        model.newConstraint("c9", and(leq(mult(p(1), v0), v3), leq(v3, mult(p(4), v0))));


        model.newObjective(true, v3);

//        String json = IlpUtil.persistToJson(model);
//        model = IlpUtil.readFromJson(json);
//        System.out.println(json);

        Result res = model.solve(solver);

        assertTrue(res.getStatistics().getStatus() == Result.SolverStatus.OPTIMAL);

        assertEquals(new Double(3d), res.getSolutions().get(v3));
    }

}
