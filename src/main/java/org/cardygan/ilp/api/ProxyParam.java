package org.cardygan.ilp.api;

import org.cardygan.ilp.api.expr.DoubleParam;
import org.cardygan.ilp.api.expr.Param;
import org.cardygan.ilp.api.expr.Var;
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
    public ProxyParam(Model ilpModel, Var targetIlpVar) {
        this(ilpModel, targetIlpVar, null);
    }

    public ProxyParam(Model ilpModel, Var targetIlpVar, Function<Double, Double> evalRule) {
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
            ilpModel.solve(solver);
            double res = solver.getVal(targetIlpVar);
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
