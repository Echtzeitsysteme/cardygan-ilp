package org.cardygan.ilp.api.model;

import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.bool.BoolExpr;
import org.cardygan.ilp.internal.solver.IdGen;
import org.cardygan.ilp.internal.solver.Solver;
import org.cardygan.ilp.internal.solver.VarIdGen;
import org.cardygan.ilp.internal.util.Util;

import static org.cardygan.ilp.internal.solver.Solver.VarType.*;


public class Model {

    private final IdGen varIdGen;
    private final Solver solver;

    public Model(Solver solver) {
        this.solver = solver;
        this.varIdGen = new VarIdGen(solver);

    }

    public void dispose() {
        solver.dispose();
    }

    public boolean isDiposed() {
        return solver.isDisposed();
    }

    public Constraint[] newConstraint(BoolExpr expr) {
        Util.assertNotNull(expr);

        return solver.newConstraint(this, null, expr);
    }

    public Constraint[] newConstraint(String name, BoolExpr expr) {
        Util.assertNotNull(name, expr);

        return solver.newConstraint(this, name, expr);
    }


    private IntVar newIntVarInternal(String name, int lb, int ub) {
        final String varName = varIdGen.checkOrGenNewIfNull(name);


        solver.addVar(varName, lb, ub, INT);


        // TODO add caching mechanism
        IntVar var = new IntVar(varName);
        return var;
    }


    /**
     * Creates new decision variable with given name.
     *
     * @param name
     * @return
     */
    public IntVar newIntVar(String name) {
        return newIntVarInternal(name, -1, -1);
    }

    /**
     * Creates new decision variable with given name and bounds.
     *
     * @param name
     * @return
     */
    public IntVar newIntVar(String name, int lb, int ub) {
        Util.assertTrue(lb >= 0);
        return newIntVarInternal(name, lb, ub);
    }

    /**
     * Creates new decision variable with given name and lower bound.
     *
     * @param name
     * @return
     */
    public IntVar newIntVar(String name, int lb) {
        Util.assertTrue(lb >= 0);
        return newIntVarInternal(name, lb, -1);
    }

    /**
     * Creates new decision variable with given lower bound.
     *
     * @param lb
     * @return
     */
    public IntVar newIntVar(int lb) {
        Util.assertTrue(lb >= 0);
        return newIntVarInternal(null, lb, -1);
    }

    /**
     * Creates new variable of given val with an unique name id.
     *
     * @return
     */
    public IntVar newIntVar() {
        return newIntVarInternal(null, -1, -1);
    }


    /**
     * Creates new decision variable with given name.
     *
     * @param name
     * @return
     */
    public BinaryVar newBinaryVar(final String name) {
        Util.assertNotNull(name);

        final String varName = varIdGen.checkOrGenNewIfNull(name);

        solver.addVar(varName, -1, -1, BIN);

        return new BinaryVar(varName);
    }

    /**
     * Creates new variable of given val with an unique name id.
     *
     * @return
     */
    public BinaryVar newBinaryVar() {
        final String varName = varIdGen.genNew();

        return newBinaryVar(varName);
    }

    /**
     * Creates new decision variable.
     *
     * @return
     */
    public DoubleVar newDoubleVar() {
        return newDoublVarInternal(null, -1, -1);
    }

    /**
     * Creates new decision variable with given name.
     *
     * @param name
     * @return
     */
    public DoubleVar newDoubleVar(String name) {
        Util.assertNotNull(name);
        return newDoublVarInternal(name, -1, -1);
    }

    /**
     * Creates new decision variable with given name and bounds.
     *
     * @param name
     * @param lb
     * @param ub
     * @return
     */
    public DoubleVar newDoubleVar(String name, double lb, double ub) {
        Util.assertNotNull(name);
        Util.assertTrue(lb >= 0);
        return newDoublVarInternal(name, lb, ub);
    }

    /**
     * Creates new decision variable with given name and lower bound.
     *
     * @param name
     * @param lb
     * @return
     */
    public DoubleVar newDoubleVar(String name, double lb) {
        return newDoubleVar(name, lb, -1);
    }

    private DoubleVar newDoublVarInternal(String name, double lb, double ub) {
        final String varName = varIdGen.checkOrGenNewIfNull(name);

        solver.addVar(varName, lb, ub, DBL);

        // TODO add caching mechanism
        return new DoubleVar(varName);
    }

    public boolean hasVar(String name) {
        return solver.hasVar(name);
    }


    public double getVal(Var var) {
        // TODO check malfunction
        return solver.getVal(var);
    }

    public double getObjVal() {
        // TODO check if not yet optimized
        return solver.getObjVal();
    }

    /**
     * Creates an objective from the given {@link ArithExpr} and adds it to the model.
     * An already existing objective is replaced.
     *
     * @param maximize <code>true</code> if objective should be maximized otherwise <code>false</code>
     * @param expr     the expression which is set as an objective for the model. Objective is removed from the model
     *                 if parameter is null.
     */
    public void newObjective(boolean maximize, ArithExpr expr) {
        solver.newObjective(maximize, expr);
    }

    /**
     * Removes the given constraint from the model.
     *
     * @param cstr the constraint to be removed from the model
     * @throws org.cardygan.ilp.internal.util.ModelException if given constraint does not exist in the model
     */
    public void removeConstraint(Constraint cstr) {
        solver.removeConstr(cstr);
    }

    public Result optimize() {
        return solver.optimize();
    }

    /**
     * Returns the number of variables that are contained in the model.
     *
     * @return the number of variables
     */
    public int getNumVars() {
        return solver.getNumVars();
    }

    /**
     * Returns the number of constraints that are contained in the model.
     *
     * @return the number of constraints
     */
    public int getNumConstrs() {
        return solver.getNumConstrs();
    }

    public Solver getSolver() {
        return solver;
    }
}
