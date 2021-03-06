package org.cardygan.ilp.internal.solver.milp;

import gurobi.GRBModel;
import ilog.concert.IloModel;
import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.*;
import org.cardygan.ilp.internal.solver.Solver;
import org.cardygan.ilp.internal.util.ModelException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class MILPSolverTest {

    private MILPSolver sut;

    @Parameterized.Parameters
    public static List<MILPSolver.MILPSolverBuilder> data() {
        return Arrays.asList(
                new GurobiSolver.GurobiSolverBuilder(),
                new CplexSolver.CplexSolverBuilder()
        );
    }


    public MILPSolverTest(MILPSolver.MILPSolverBuilder builder) {
        this.sut = builder.build();

    }


    @Test
    public void optimize() {
        Model model = mock(Model.class);
        Var v1 = mock(IntVar.class);
        Var v2 = mock(IntVar.class);

        // add and remove valid constraint
        given(model.hasVar("v1")).willReturn(true);
        given(model.hasVar("v2")).willReturn(true);
        given(v1.getName()).willReturn("v1");
        given(v2.getName()).willReturn("v2");

        sut.addVar("v1", -1, -1, Solver.VarType.INT);
        sut.addVar("v2", 0, 1, Solver.VarType.INT);

        // check optimal
        sut.addCstr("test", new LinearConstr(new Var[]{v1, v2}, new double[]{1d, -1d}, 0, LinearConstr.Type.LEQ));
        sut.setObj(new LinearObj(true, new Var[]{v1}, new double[]{1d}, 41d));
        Result res = sut.optimize();
        assertSame(Result.SolverStatus.OPTIMAL, res.getStatus());
        assertEquals(1d, sut.getVal(v1), 0);
        assertEquals(42d, sut.getObjVal(), 0);

        // check infeasible or unbounded
        sut.addCstr("test2", new LinearConstr(new Var[]{v1}, new double[]{1d}, 0, LinearConstr.Type.EQ));
        sut.addCstr("test3", new LinearConstr(new Var[]{v1}, new double[]{1d}, 1, LinearConstr.Type.EQ));

        // check infeasible
        sut.setObj(new LinearObj(true, new Var[]{}, new double[]{}, 0));
        Result res3 = sut.optimize();
        assertSame(Result.SolverStatus.INFEASIBLE, res3.getStatus());

        // check unbounded
        sut.setObj(new LinearObj(true, new Var[]{v1}, new double[]{1d}, 1d));
        sut.removeConstr(new Constraint("test2"));
        sut.removeConstr(new Constraint("test3"));
        sut.removeConstr(new Constraint("test"));
        Result res4 = sut.optimize();
        assertSame(Result.SolverStatus.UNBOUNDED, res4.getStatus());

        sut.dispose();
    }

    @Test
    public void optimizeInfeasibleOrUnbounded() {

//        Min -x1 - x2
//        s.t.
//                x1 - x2 = 5
//        x1 - x2 - x3= -5
//        x3 = 0
//        x1, x2, x3 >= 0
        Model model = mock(Model.class);
        Var x1 = mock(DoubleVar.class);
        Var x2 = mock(DoubleVar.class);
        Var x3 = mock(DoubleVar.class);

        // add and remove valid constraint
        given(model.hasVar("x1")).willReturn(true);
        given(model.hasVar("x2")).willReturn(true);
        given(model.hasVar("x3")).willReturn(true);
        given(x1.getName()).willReturn("x1");
        given(x2.getName()).willReturn("x2");
        given(x3.getName()).willReturn("x3");

        sut.addVar("x1", 0, -1, Solver.VarType.DBL);
        sut.addVar("x2", 0, -1, Solver.VarType.DBL);
        sut.addVar("x3", 0, -1, Solver.VarType.DBL);

        // check infeasible or unbounded
        sut.addCstr("test1", new LinearConstr(new Var[]{x1, x2}, new double[]{1d, -1d}, 5, LinearConstr.Type.EQ));
        sut.addCstr("test2", new LinearConstr(new Var[]{x1, x2, x3}, new double[]{1d, -1d, -1d}, -5, LinearConstr.Type.EQ));
        sut.addCstr("test3", new LinearConstr(new Var[]{x3}, new double[]{1d}, 0, LinearConstr.Type.EQ));

        sut.setObj(new LinearObj(false, new Var[]{x1, x2}, new double[]{-1d, -1d}, 0));

        Result res = sut.optimize();
        assertSame(Result.SolverStatus.INF_OR_UNBD, res.getStatus());

        sut.dispose();
    }


    @Test
    public void dispose() {
        assertFalse(sut.isDisposed());
        sut.dispose();
        assertTrue(sut.isDisposed());
    }

    @Test
    public void getUnderlyingModel() {
        if (sut instanceof GurobiSolver)
            assertTrue(sut.getUnderlyingModel() instanceof GRBModel);

        if (sut instanceof CplexSolver)
            assertTrue(sut.getUnderlyingModel() instanceof IloModel);
    }

    @Test
    public void addAndRemoveLinearConstr() {
        Model model = mock(Model.class);
        Var v1 = mock(IntVar.class);
        Var v2 = mock(IntVar.class);

        // add and remove valid constraint
        given(model.hasVar("v1")).willReturn(true);
        given(model.hasVar("v2")).willReturn(true);
        given(v1.getName()).willReturn("v1");
        given(v2.getName()).willReturn("v2");

        sut.addVar("v1", 0, 1, Solver.VarType.INT);
        sut.addVar("v2", 0, 1, Solver.VarType.INT);

        sut.addCstr("test", new LinearConstr(new Var[]{v1, v2}, new double[]{1d, 1d}, 2, LinearConstr.Type.LEQ));

        assertTrue(sut.hasConstraint("test"));

        sut.removeConstr(new Constraint("test"));
        assertFalse(sut.hasConstraint("test"));

        sut.addCstr("test2", new LinearConstr(new Var[]{v1, v2}, new double[]{1d, 1d}, 2, LinearConstr.Type.GEQ));
        assertTrue(sut.hasConstraint("test2"));

        sut.addCstr("test3", new LinearConstr(new Var[]{v1, v2}, new double[]{1d, 1d}, 2, LinearConstr.Type.EQ));
        assertTrue(sut.hasConstraint("test3"));

        sut.removeConstr(new Constraint("test3"));
        assertFalse(sut.hasConstraint("test3"));

        // missing vars should lead to an exception
        given(model.hasVar("v3")).willReturn(false);
        Var v3 = mock(IntVar.class);
        given(v3.getName()).willReturn("v3");

        try {
            sut.addCstr("test4", new LinearConstr(new Var[]{v1, v3}, new double[]{1d, 1d}, 2, LinearConstr.Type.EQ));
            fail();
        } catch (ModelException ignored) {

        }

        // already existing name
        try {
            sut.addCstr("test2", new LinearConstr(new Var[]{v1, v2}, new double[]{1d, 1d}, 2, LinearConstr.Type.GEQ));
            fail();
        } catch (ModelException ignored) {

        }

        // null
        try {
            sut.addCstr(null, new LinearConstr(new Var[]{v1, v2}, new double[]{1d, 1d}, 2, LinearConstr.Type.GEQ));
            fail();
        } catch (NullPointerException ignored) {

        }

        // add valid SOS constraint
        sut.addCstr("testSos1", new LinearConstr(new Var[]{v1, v2}, new double[]{1d, 2d}, 0, LinearConstr.Type.SOS));
        assertTrue(sut.hasConstraint("testSos1"));

        // remove SOS
        sut.removeConstr(new Constraint("testSos1"));
        assertFalse(sut.hasConstraint("testSos1"));

        // add invalid SOS constraint
        try {
            sut.addCstr("testSos2", new LinearConstr(new Var[]{v1, v3}, new double[]{1d, 2d}, 0, LinearConstr.Type.SOS));
            fail();
        } catch (ModelException ignored) {

        }

        // remove constraint which does not exist in model
        try {
            sut.removeConstr(new Constraint("doesNotExist"));
            fail();
        } catch (ModelException ignored) {

        }

        sut.dispose();
    }

    @Test
    public void setObj() {
        Model model = mock(Model.class);
        Var v1 = mock(IntVar.class);
        Var v2 = mock(IntVar.class);

        // ok
        given(model.hasVar("v1")).willReturn(true);
        given(model.hasVar("v2")).willReturn(true);
        given(v1.getName()).willReturn("v1");
        given(v2.getName()).willReturn("v2");
        sut.addVar("v1", 0, 1, Solver.VarType.INT);
        sut.addVar("v2", 0, 1, Solver.VarType.INT);

        sut.setObj(new LinearObj(true, new Var[]{v1, v2}, new double[]{1d, 1d}, 2));

        // missing vars should lead to an exception
        given(model.hasVar("v3")).willReturn(false);
        Var v3 = mock(IntVar.class);
        given(v3.getName()).willReturn("v3");

        try {
            sut.setObj(new LinearObj(true, new Var[]{v1, v3}, new double[]{1d, 1d}, 2));
            fail();
        } catch (ModelException ignored) {

        }

        // null
        try {
            sut.addCstr(null, new LinearConstr(new Var[]{v1, v2}, new double[]{1d, 1d}, 2, LinearConstr.Type.GEQ));
            fail();
        } catch (NullPointerException ignored) {

        }

        sut.dispose();
    }

    @Test
    public void addVar() {
        // ok
        sut.addVar("test", 0, 3, Solver.VarType.INT);
        assertTrue(sut.hasVar("test"));

        sut.addVar("b1", 0, 0, Solver.VarType.BIN);
        assertTrue(sut.hasVar("b1"));

        sut.addVar("d1", 13, 30.4, Solver.VarType.DBL);
        assertTrue(sut.hasVar("d1"));


        // variable with same name leads to exception
        try {
            sut.addVar("test", 0, 3, Solver.VarType.INT);
            fail();
        } catch (ModelException ignored) {

        }

        // invalid lower and upper bounds should lead to exception
        try {
            sut.addVar("test2", 3, 0, Solver.VarType.INT);
            fail();
        } catch (ModelException ignored) {

        }

        // Passing null as parameter should lead to an exception
        try {
            sut.addVar(null, 0, 3, Solver.VarType.INT);
            fail();
        } catch (NullPointerException ignored) {

        }

        // If model is already disposed an exception should be thrown
        try {
            sut.dispose();
            sut.addVar("test3", 0, 3, Solver.VarType.INT);
            fail();
        } catch (ModelException ignored) {

        }
    }
}