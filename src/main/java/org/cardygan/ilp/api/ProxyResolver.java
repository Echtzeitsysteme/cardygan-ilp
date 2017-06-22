package org.cardygan.ilp.api;

import org.cardygan.ilp.api.expr.*;
import org.cardygan.ilp.api.expr.bool.*;
import org.cardygan.ilp.internal.expr.ArithExprVisitor;
import org.cardygan.ilp.internal.expr.BoolExprVisitor;

import java.util.*;

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

        List<ProxyParam> searchList = new ArrayList<>();

        for (Constraint cstr : ilp.getConstraints()) {
            searchList.addAll(resolve(cstr));
        }

        searchList.addAll(resolve(ilp.getObjective()));

        for (ProxyParam p : searchList) {
            p.solve(solver);
        }


    }

    private List<ProxyParam> resolve(Objective obj) {

        List<ProxyParam> searchList = new ArrayList<>();

        obj.getExpr().accept(new ProxyParamSearcher(searchList));

        return searchList;
    }

    private List<ProxyParam> resolve(Constraint cstr) {

        List<ProxyParam> searchList = new ArrayList<>();

        cstr.getExpr().accept(new ProxyParamSearcher(searchList));

        return searchList;
    }


//    private void resolve(ProxyParam param) {
//        if (visited.contains(param.getTargetIlpVar())) {
//            throw new IllegalStateException("Cycle detected in Proxy Params. Cannot solve.");
//        }
//
//        visited.add(param.getTargetIlpVar());
//        toBeSolved.push(param);
//
////        for (NormalizedArithExpr cstr : param.getIlpModel().getNormalizedConstraints()) {
////            cstr.getCoefficients().forEach(this::resolve);
////        }
////
////        param.getIlpModel().getObjective().getCoefficients().forEach(this::resolve);
//    }

    private class ProxyParamSearcher implements BoolExprVisitor<Void>, ArithExprVisitor<Void> {

        private final List<ProxyParam> searchList;

        public ProxyParamSearcher(List<ProxyParam> searchList) {
            this.searchList = searchList;
        }

        @Override
        public Void visit(Sum expr) {
            expr.getSummands().forEach(e -> e.accept(this));
            return null;
        }

        @Override
        public Void visit(Mult expr) {
            expr.getLeft().accept(this);
            expr.getRight().accept(this);
            return null;
        }

        @Override
        public Void visit(Param expr) {
            if (expr instanceof ProxyParam) {
                searchList.add((ProxyParam) expr);
            }
            return null;
        }

        @Override
        public Void visit(Neg expr) {
            expr.getNeg().accept(this);
            return null;
        }

        @Override
        public Void visit(Var var) {
            return null;
        }

        @Override
        public Void visit(And expr) {
            expr.getElements().forEach(e -> e.accept(this));
            return null;
        }

        @Override
        public Void visit(Or expr) {
            expr.getElements().forEach(e -> e.accept(this));
            return null;
        }

        @Override
        public Void visit(Xor expr) {
            expr.getLhs().accept(this);
            expr.getRhs().accept(this);
            return null;
        }

        @Override
        public Void visit(Not expr) {
            expr.getVal().accept(this);
            return null;
        }

        @Override
        public Void visit(Impl expr) {
            expr.getLhs().accept(this);
            expr.getRhs().accept(this);
            return null;
        }

        @Override
        public Void visit(BiImpl expr) {
            expr.getLhs().accept(this);
            expr.getRhs().accept(this);
            return null;
        }

        @Override
        public Void visit(BinaryVar expr) {
            return null;
        }

        @Override
        public Void visit(RelOp expr) {
            expr.getLhs().accept(this);
            expr.getRhs().accept(this);
            return null;
        }
    }

}
