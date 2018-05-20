package org.cardygan.ilp.api.model;

import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.solver.Solver;
import org.cardygan.ilp.internal.expr.ArithExprVisitor;

import java.util.Optional;
import java.util.function.Function;

public class ProxyParam extends Param {

    private final Model ilpModel;
    private final Var targetIlpVar;
    private final Optional<Function<Double, Double>> evalRule;
    private Param ilpParam;

    /**
     * Take value of target variable as value of ilpParam
     *
     * @param ilpModel
     * @param targetIlpVar
     */
    ProxyParam(Model ilpModel, Var targetIlpVar) {
        this(ilpModel, targetIlpVar, null);
    }

    ProxyParam(Model ilpModel, Var targetIlpVar, Function<Double, Double> evalRule) {
        this.ilpModel = ilpModel;
        this.targetIlpVar = targetIlpVar;
        this.evalRule = Optional.ofNullable(evalRule);
    }

    public Model getIlpModel() {
        return ilpModel;
    }

    public Var getTargetIlpVar() {
        return targetIlpVar;
    }


    public void solve(Solver solver) {
        if (ilpParam == null) {
            Result result = ilpModel.solve(solver);
            double res = result.getSolutions().get(targetIlpVar);
            ilpParam = new DoubleParam(res);
        }
    }

    @Override
    public double getVal() {
        if (ilpParam == null) {
            throw new IllegalStateException("Proxy param was not resolved.");
        }

        if (evalRule.isPresent()) {
            return evalRule.get().apply(ilpParam.getVal());
        }
        return ilpParam.getVal();
    }

    @Override
    public <T> T accept(ArithExprVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
