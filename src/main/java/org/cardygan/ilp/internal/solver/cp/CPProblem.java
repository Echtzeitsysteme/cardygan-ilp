//package org.cardygan.ilp.internal.solver.cp;
//
//import org.cardygan.ilp.api.model.ArithExpr;
//import org.cardygan.ilp.api.model.Constraint;
//import org.cardygan.ilp.api.model.Model;
//import org.cardygan.ilp.api.model.bool.BoolExpr;
//import org.cardygan.ilp.internal.solver.CstrIdGen;
//import org.cardygan.ilp.internal.solver.IdGen;
//import org.cardygan.ilp.internal.solver.Problem;
//import org.cardygan.ilp.internal.solver.Solver;
//
//public class CPProblem implements Problem {
//
//    private final IdGen cstrIdGen;
//    private final CPSolver backend;
//
//    public CPProblem(CPSolver backend) {
//        this.cstrIdGen = new CstrIdGen(backend);
//        this.backend = backend;
//    }
//
//    @Override
//    public Constraint[] newConstraint(Model model, String name, BoolExpr expr) {
//        return backend.addCstr(name, expr);
//    }
//
//    @Override
//    public void newObjective(boolean maximize, ArithExpr expr) {
//        backend.setObjective(maximize, expr);
//    }
//
//    @Override
//    public Solver getBackend() {
//        return backend;
//    }
//}
