package org.cardygan.ilp.api;

import org.cardygan.ilp.api.expr.Var;
import org.cardygan.ilp.internal.Coefficient;
import org.cardygan.ilp.internal.expr.NormalizedArithExpr;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ProxyResolver {

    private final Model ilp;
    private final Solver solver;
    private Set<Var> visited;
    private Stack<ProxyParam> toBeSolved;

    public ProxyResolver(Model ilp, Solver solver) {
        this.ilp = ilp;
        this.solver = solver;
    }

    public void resolve() {
        visited = new HashSet<Var>();
        toBeSolved = new Stack<ProxyParam>();

        ilp.getConstraints().forEach(this::resolve);

        ilp.getObjective().getCoefficients().forEach(this::resolve);

        while (!toBeSolved.isEmpty()) {
            ProxyParam p = toBeSolved.pop();
            p.solve(solver);
        }

    }

    private void resolve(NormalizedArithExpr cstr) {
        cstr.getCoefficients().forEach(this::resolve);

        if (cstr.getRhs() instanceof ProxyParam) {
            resolve((ProxyParam) cstr.getRhs());
        }
    }


    private void resolve(Coefficient coef) {
        if (!(coef.getParam() instanceof ProxyParam)) {
            return;
        }

        ProxyParam param = (ProxyParam) coef.getParam();
        resolve(param);
    }

    private void resolve(ProxyParam param) {
        if (visited.contains(param.getTargetIlpVar())) {
            throw new IllegalStateException("Cycle detected in Proxy Params. Cannot solve.");
        }

        visited.add(param.getTargetIlpVar());
        toBeSolved.push(param);

        for (NormalizedArithExpr cstr : param.getIlpModel().getConstraints()) {
            cstr.getCoefficients().forEach(this::resolve);
        }

        param.getIlpModel().getObjective().getCoefficients().forEach(this::resolve);
    }

}
