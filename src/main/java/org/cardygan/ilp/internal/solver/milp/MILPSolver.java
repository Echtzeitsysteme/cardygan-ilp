package org.cardygan.ilp.internal.solver.milp;

import org.cardygan.ilp.api.model.ArithExpr;
import org.cardygan.ilp.api.model.Constraint;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.api.model.bool.And;
import org.cardygan.ilp.api.model.bool.BoolExpr;
import org.cardygan.ilp.api.model.bool.RelOp;
import org.cardygan.ilp.internal.expr.ExprSimplifier;
import org.cardygan.ilp.internal.solver.CstrIdGen;
import org.cardygan.ilp.internal.solver.IdGen;
import org.cardygan.ilp.internal.solver.Solver;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Mixed Integer Linear Programming (MILP) Problem
 */
public abstract class MILPSolver implements Solver {

    private final IdGen cstrIdGen;
    private final MILPConstrGenerator constrGen;

    public MILPSolver() {
        this.cstrIdGen = new CstrIdGen(this);
        this.constrGen = new SosBasedCstrGenerator();
    }

    public MILPSolver(MILPConstrGenerator constrGenFactory) {
        this.cstrIdGen = new CstrIdGen(this);
        this.constrGen = constrGenFactory;
    }

    @Override
    public Constraint[] newConstraint(Model model, String name, BoolExpr expr) {
        if (expr instanceof RelOp) {
            return newLinearConstr(name, (RelOp) expr);
        } else if (expr instanceof And) {
            // First try to split expressions consisting only of conjunctions into a list of relops
            List<RelOp> relOps = ExprSimplifier.collectConjunctiveRelops((And) expr);

            if (!relOps.isEmpty())
                return relOps.stream()
                        .flatMap(it -> Arrays.stream(newLinearConstr(null, it)))
                        .toArray(Constraint[]::new);

        }
        return newGeneralConstr(model, name, expr);
    }

    private Constraint[] newLinearConstr(final String name, RelOp expr) {
        final String cstrName = cstrIdGen.checkOrGenNewIfNull(name);

        Optional<LinearConstr> cstr = ExprSimplifier.normalizeCstr(expr);

        if (cstr.isPresent()) {
            addCstr(cstrName, cstr.get());
            return new Constraint[]{new Constraint(cstrName)};
        } else
            return new Constraint[]{};
    }


    private Constraint[] newGeneralConstr(Model model, String name, BoolExpr expr) {
        final LinearConstr[] transCstrs = constrGen.transform(expr, model);
        final Constraint[] ret = new Constraint[transCstrs.length];

        int i = 0;
        for (LinearConstr cstr : transCstrs) {

            final String cstrName;
            if (i == 0)
                cstrName = cstrIdGen.checkOrGenNewIfNull(name);
            else
                cstrName = cstrIdGen.genNew();

            addCstr(cstrName, cstr);

            ret[i] = new Constraint(cstrName);

            i++;
        }

        return ret;
    }

    public void newObjective(boolean maximize, ArithExpr expr) {
        ExprSimplifier.SimplifiedArithExpr simplExpr = ExprSimplifier.simplify(expr);

        // one additional entry for constant
        final double[] params = new double[simplExpr.getCoeffs().size()];

        final double constant = simplExpr.getConstant();

        final Var[] vars = new Var[simplExpr.getCoeffs().size()];

        final AtomicInteger i = new AtomicInteger(0);
        simplExpr.getCoeffs().forEach(
                (var, param) ->

                {
                    params[i.get()] = param;
                    vars[i.get()] = var;
                    i.incrementAndGet();
                }
        );

        LinearObj obj = new LinearObj(maximize, vars, params, constant);
        setObj(obj);
    }

    /**
     * Adds a constraint to the backend.
     *
     * @param name name of constraint. Needs to be unique in model
     * @param cstr constraint to be added
     * @throws org.cardygan.ilp.internal.util.ModelException if one of the following conditions is met:
     *                                                       <ul>
     *                                                       <li>constraint contains at least one variable which is not part of the model.</li>
     *                                                       <li>model already contains a constraint with given name</li>
     *                                                       </ul>
     * @throws NullPointerException                          if at least one parameter is null
     */
    public abstract void addCstr(String name, LinearConstr cstr);

    /**
     * Sets the objective for the model.
     *
     * @param obj The objective to be set
     * @throws NullPointerException                          if parameter obj is null
     * @throws org.cardygan.ilp.internal.util.ModelException if objective contains variables which do not exist in the model.
     */
    public abstract void setObj(LinearObj obj);


    interface MILPSolverBuilder extends SolverBuilder {
        MILPSolver build();
    }

}
