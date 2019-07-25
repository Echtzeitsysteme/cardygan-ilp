package org.cardygan.ilp.internal.solver;

import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.ArithExpr;
import org.cardygan.ilp.api.model.Constraint;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.api.model.bool.BoolExpr;

/**
 * A Problem represents a class of problem instances that can be solved by similar internal backends
 * (e.g., Mixed Integer Linear Programming, Constraint Programming).
 */
public interface Solver {

    /**
     * Creates no, one or multiple new constraints representing the given {@link BoolExpr}
     * and adds them to the given {@link Model}.
     * The method may return an empty array if given <code>expr</code> is determined to represent
     * a condition that always holds.
     *
     * @param model the model to which the newly created constraint(s) is added
     * @param name  the unique name of the constraint
     * @param expr  the boolean expression from which the constraint(s) are created
     * @return the newly created constraints which were added to the model or an empty array when <code>expr</code> is determined to always hold
     * @throws org.cardygan.ilp.internal.util.ModelException if <code>model</code> already contains a constraint with the given <code>name</code>
     */
    Constraint[] newConstraint(Model model, String name, BoolExpr expr);

    /**
     * Creates an objective from the given {@link ArithExpr} and adds it to the model.
     * An already existing objective is replaced.
     *
     * @param maximize <code>true</code> if objective should be maximized otherwise <code>false</code>
     * @param expr     the expression which is set as an objective for the model
     */
    void newObjective(boolean maximize, ArithExpr expr);

    /**
     * Removes the given constraint from the model.
     *
     * @param constraint the constraint to be removed from the model
     * @throws org.cardygan.ilp.internal.util.ModelException if given constraint does not exist in the model
     */
    void removeConstr(Constraint constraint);

    /**
     * Optimizes the underlying model and returns a
     * result object.
     * <p>
     * This method needs to be executed before
     * getVal and getObjVal can be used.
     *
     * @return the result of the optimization run
     */
    Result optimize();

    /**
     * Releases the resources associated with the model.
     */
    void dispose();

    /**
     * Check if the model has been disposed.
     *
     * @return true if model has been disposed otherwise false
     */
    boolean isDisposed();

    /**
     * Gets the value of a variable for the last solution.
     *
     * @param var a variable from the model
     * @return the value of the last calculated solution for the given variable.
     * @throws org.cardygan.ilp.internal.util.ModelException optimize has not been called before or result is not OPTIMAL or variable does not exist in model
     */
    double getVal(Var var);

    /**
     * Get the objective value for the last calculated solution.
     *
     * @return the objective value of the last calculated solution
     * @throws org.cardygan.ilp.internal.util.ModelException optimize has not been called before or result is not OPTIMAL
     */
    double getObjVal();

    /**
     * Gets the underlying internal specific model object.
     * <p>
     * The underlying model object can be used to tune
     * internal-specific configuration options. Changes made to the
     * underlying model object are not synchronized back to the
     * Solver. Thus, changes to the model
     * (e.g., adding or removal of constraints or variables)
     * should be always made through the internal backend.
     *
     * @return the internal specific underlying model
     */
    Object getUnderlyingModel();

    /**
     * Adds a variable with the given name, lower and upper bound,
     * and variable type.
     *
     * @param name the unique name of the variable
     * @param lb   the lower bound. If the variable should be unbounded at the lower bound lb < 0 must hold.
     * @param ub   the upper bound. If the variable should be unbounded at the upper bound ub < 0 must hold, other wise lb<= ub must hold.
     * @param type the type of the variable
     * @throws org.cardygan.ilp.internal.util.ModelException if bounds were not properly specified or variable with given name already exists.
     */
    void addVar(String name, double lb, double ub, VarType type);

    /**
     * Returns the number of variables that are contained in the model.
     *
     * @return the number of variables
     */
    int getNumVars();

    /**
     * Returns the number of constraints that are contained in the model.
     *
     * @return the number of constraints
     */
    int getNumConstrs();

    /**
     * This enum represents variable types.
     */
    enum VarType {
        /**
         * Discrete variable of type integer
         */
        INT,

        /**
         * Discrete variable of type binary
         */
        BIN,

        /**
         * Continuous variable of type double
         */
        DBL
    }

    /**
     * Returns <code>true</code> if variable with the name <code>varName</code>
     * is already provided otherwise <code>false</code>.
     *
     * @param varName the name of the variable
     * @return <code>true</code> if variable is provided otherwise <code>false</code>
     */
    boolean hasVar(String varName);

    /**
     * Returns <code>true</code> if constraint with the name <code>cstrName</code>
     * is already provided otherwise <code>false</code>.
     *
     * @param cstrName the name of the constraint
     * @return <code>true</code> if the constraint is provided otherwise <code>false</code>
     */
    boolean hasConstraint(String cstrName);

    interface SolverBuilder {
        Solver build();
    }

}
