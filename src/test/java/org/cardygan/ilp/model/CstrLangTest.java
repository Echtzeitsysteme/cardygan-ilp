package org.cardygan.ilp.model;

import org.cardygan.ilp.api.*;
import org.junit.Before;
import org.junit.Test;

import static org.cardygan.ilp.api.util.ExprDsl.*;
import static org.junit.Assert.*;

/**
 * Created by markus on 18.02.17.
 */
public class CstrLangTest {

    CplexSolver solver;

    @Before
    public void init() {
        solver = new CplexSolver();
    }

    @Test
    public void basicAnd() {
        Model model = new Model();

        Constraint cstr1 = model.newConstraint("name");
        IntVar v1 = model.newIntVar("v1");
        IntVar v2 = model.newIntVar("v2");
        cstr1.setExpr(and(leq(v1, param(3)), geq(v2, param(2))));

        Objective obj = model.newObjective(true);
        obj.setExpr(sum(v1));

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

        Constraint cstr1 = model.newConstraint("name");
        IntVar v1 = model.newIntVar("v1");
        cstr1.setExpr(not(geq(v1, param(3))));

        Objective obj = model.newObjective(true);
        obj.setExpr(sum(v1));

//        model.setM(1000);
        Result res = model.solve(solver);
        assertEquals(new Double(2), res.getObjVal().get());
        assertTrue(res.getStatistics().isFeasible());

//        model.setM(Optional.empty());
//        res = model.solve(solver);
//        assertEquals(new Double(2), res.getObjVal().get());
    }

    @Test
    public void equality() {
        Model model = new Model();

        IntVar f1 = model.newIntVar("f1");

        Constraint cstr1 = model.newConstraint("cstr1");
        cstr1.setExpr(eq(param(3), f1));


        Objective obj = model.newObjective(true);
        obj.setExpr(sum(f1));

        Result res = model.solve(solver);

        assertEquals(new Double(3), res.getObjVal().get());
        assertTrue(res.getStatistics().isFeasible());
    }

    @Test
    public void equality2() {
        Model model = new Model();


        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");

        Constraint cstr1 = model.newConstraint("cstr1");
        cstr1.setExpr(eq(f2, f1));
        Constraint cstr2 = model.newConstraint("cstr2");
        cstr2.setExpr(leq(f2, param(3)));


        Objective obj = model.newObjective(true);
        obj.setExpr(sum(f1));

        Result res = model.solve(solver);

        assertEquals(new Double(3), res.getObjVal().get());
        assertTrue(res.getStatistics().isFeasible());
    }

    @Test
    public void equality3() {
        Model model = new Model();


        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");

        Constraint cstr1 = model.newConstraint("cstr1");
        cstr1.setExpr(and(eq(f2, f1), leq(f2, param(3))));


        Objective obj = model.newObjective(true);
        obj.setExpr(sum(f1));

        Result res = model.solve(solver);

        System.out.println("Unbounded? " + res.getStatistics().isUnbounded());

        assertEquals(new Double(3), res.getObjVal().get());
        assertTrue(res.getStatistics().isFeasible());
    }

    @Test
    public void equality4() {
        Model model = new Model();

        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");

        Constraint cstr1 = model.newConstraint("cstr1");
        cstr1.setExpr(and(eq(param(3), sum(f1, f2)), eq(f2, param(1))));


        Objective obj = model.newObjective(true);
        obj.setExpr(sum(f1));

        Result res = model.solve(solver);

        assertEquals(new Double(2), res.getObjVal().get());
        assertTrue(res.getStatistics().isFeasible());
        assertFalse(res.getStatistics().isUnbounded());
    }

    @Test
    public void implication1() {
        Model model = new Model();

        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");

        Constraint cstr1 = model.newConstraint("cstr1");
        cstr1.setExpr(impl(eq(param(3), f1), eq(f2, param(11))));
        Constraint cstr2 = model.newConstraint("cstr2");
        cstr2.setExpr(eq(param(3), f1));

        Objective obj = model.newObjective(true);
        obj.setExpr(sum(f2));

        Result res = model.solve(solver);

        assertEquals(new Double(11), res.getObjVal().get());
        assertTrue(res.getStatistics().isFeasible());
        assertFalse(res.getStatistics().isUnbounded());
    }

    @Test
    public void implication2() throws NoSuchFieldException, IllegalAccessException {


        Model model = new Model();

        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");
        IntVar f3 = model.newIntVar("f3");

        Constraint cstr1 = model.newConstraint("cstr1");
        cstr1.setExpr(impl(and(eq(param(3), sum(f1, f2))),
                eq(param(11), f3)));
        Constraint cstr2 = model.newConstraint("cstr2");
        cstr2.setExpr(eq(param(2), f1));
        Constraint cstr3 = model.newConstraint("cstr2");
        cstr3.setExpr(eq(param(1), f2));

        Objective obj = model.newObjective(true);
        obj.setExpr(sum(f3));

        Result res = model.solve(solver);

        assertEquals(new Double(11), res.getObjVal().get());
        assertTrue(res.getStatistics().isFeasible());
        assertFalse(res.getStatistics().isUnbounded());
    }


    @Test
    public void implication3() {
        Model model = new Model();

        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");
        IntVar f3 = model.newIntVar("f3");

        Constraint cstr1 = model.newConstraint("cstr1");
        cstr1.setExpr(impl(and(eq(param(2), f1), eq(param(1), f2)),
                eq(param(11), f3)));
        Constraint cstr2 = model.newConstraint("cstr2");
        cstr2.setExpr(eq(param(2), f1));
        Constraint cstr3 = model.newConstraint("cstr2");
        cstr3.setExpr(eq(param(1), f2));

        Objective obj = model.newObjective(true);
        obj.setExpr(sum(f3));

        Result res = model.solve(solver);

        assertEquals(new Double(11), res.getObjVal().get());
        assertTrue(res.getStatistics().isFeasible());
        assertFalse(res.getStatistics().isUnbounded());
    }

    @Test
    public void implication() {
        Model model = new Model();

        IntVar f1 = model.newIntVar("f1");
        IntVar f2 = model.newIntVar("f2");
        IntVar f3 = model.newIntVar("f3");

        Constraint cstr1 = model.newConstraint("cstr1");
        cstr1.setExpr(
                impl(and(eq(param(3), sum(f1, f2)), eq(f1, param(1))),
                        eq(param(3), f3)));

        Constraint cstr2 = model.newConstraint("cstr2");
        cstr2.setExpr(eq(f1, param(1)));
        Constraint cstr3 = model.newConstraint("cstr3");
        cstr3.setExpr(eq(f2, param(2)));

        Objective obj = model.newObjective(true);
        obj.setExpr(sum(f3));

        Result res = model.solve(solver);

        assertEquals(new Double(3), res.getObjVal().get());
        assertTrue(res.getStatistics().isFeasible());
    }

}
