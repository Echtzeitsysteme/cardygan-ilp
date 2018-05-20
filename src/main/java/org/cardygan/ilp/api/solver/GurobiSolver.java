//package org.cardygan.ilp.api.solver;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import gurobi.GRB;
//import gurobi.GRBConstr;
//import gurobi.GRBEnv;
//import gurobi.GRBException;
//import gurobi.GRBLinExpr;
//import gurobi.GRBModel;
//import gurobi.GRBVar;
//import org.cardygan.ilp.api.Result;
//import org.cardygan.ilp.api.model.Constraint;
//import org.cardygan.ilp.api.model.Var;
//
//
//public class GurobiSolver implements Solver {
//
//    private final static String ENV_VAR_CPLEX_LIB_PATH = "CPLEX_LIB_PATH";
//    private final Optional<String> modelOutputFilePath;
//    private final Optional<Integer> seed;
//    private final boolean logging;
//    private final boolean preSolve;
//    private final int threadCount;
//    private final int parallelMode;
//    private Map<Var, GRBVar> vars;
//    private Map<Var, Double> solutions;
//    private final Optional<Long> timeout;
//
//    private static final String LOG_FILE = "log-" + System.currentTimeMillis() + ".log";
//    private static final String LOG_PATH = "." + File.separator + "log" + File.separator + "gurobi" + File.separator;
//    private int M = -1;
//    private boolean loggingOn = false;
//
//    public GurobiSolver(boolean loggingOn) {
//        this.loggingOn = loggingOn;
//    }
//
//    @Override
//    public Result solve(ILP ilp) {
//
//        if (ilp.getBigM() == -1) {
//            throw new RuntimeException("M must either be set or initialized with a non-negative value!");
//        }
//
//        M = ilp.getBigM();
//
//        typeVariableMap = new HashMap<FeatureTypeVariable, GRBVar>();
//        instanceVariableMap = new HashMap<FeatureInstanceVariable, GRBVar>();
//        crossTreeVariableMap = new HashMap<CrossTreeConstraintVariable, GRBVar>();
//
//        try {
//            final GRBEnv environment = new GRBEnv();
//            if (!loggingOn) {
//                environment.set(GRB.IntParam.LogToConsole, 0);
//                environment.set(GRB.StringParam.LogFile, "");
//            } else {
//                final File log = new File(LOG_PATH);
//                if (log.exists() || log.mkdirs()) {
//                    environment.set(GRB.StringParam.LogFile, LOG_PATH + LOG_FILE);
//                }
//            }
//
//            final GRBModel model = new GRBModel(environment);
//
//            for (final FeatureTypeVariable var : ilp.getFeatureTypeVars()) {
//                final GRBVar grbVar = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "t" + var.getIndex());
//                typeVariableMap.put(var, grbVar);
//            }
//
//            for (final FeatureInstanceVariable var : ilp.getFeatureInstanceVars()) {
//                final GRBVar grbVar = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.INTEGER, "f" + var.getIndex());
//                instanceVariableMap.put(var, grbVar);
//            }
//
//            for (final CrossTreeConstraintVariable var : ilp.getCrossTreeConstraintVars()) {
//                final GRBVar grbVar = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "r" + var.getIndex());
//                crossTreeVariableMap.put(var, grbVar);
//            }
//
//            model.update();
//
//            for (final Constraint con : ilp.getConstraints()) {
//                constructConstraint(model, con);
//            }
//
//            final GRBLinExpr obj = new GRBLinExpr();
//            createExpressionTerms(obj, ilp.getObjectiveFunction().getExpr());
//
//            if (ilp.getObjectiveFunction().isMaximize()) {
//                model.setObjective(obj, GRB.MAXIMIZE);
//            } else {
//                model.setObjective(obj, GRB.MINIMIZE);
//            }
//
//            final long start = System.currentTimeMillis();
//            model.optimize();
//            final long end = System.currentTimeMillis();
//
//            final boolean isFeasible = model.get(GRB.IntAttr.Status) == GRB.OPTIMAL;
//            final boolean isUnbounded = model.get(GRB.IntAttr.Status) == GRB.UNBOUNDED;
//            final Optional<Map<DecisionVariable, Double>> values = (isFeasible) ? Optional.of(getVarVals())
//                    : Optional.empty();
//            final Optional<Double> objVal = (isFeasible) ? Optional.of(obj.getValue()) : Optional.empty();
//
//            final Result res = new Result(new Statistics(isFeasible, isUnbounded, end - start), values, objVal);
//
//            model.dispose();
//            environment.dispose();
//
//            return res;
//        } catch (final GRBException e) {
//            e.printStackTrace();
//        }
//
//        throw new RuntimeException("Error in solver Run ");
//    }
//
//    private Map<DecisionVariable, Double> getVarVals() throws GRBException {
//        final Map<DecisionVariable, Double> varVals = new HashMap<>();
//
//        for (final DecisionVariable var : typeVariableMap.keySet()) {
//            varVals.put(var, typeVariableMap.get(var).get(GRB.DoubleAttr.X));
//        }
//
//        for (final DecisionVariable var : instanceVariableMap.keySet()) {
//            varVals.put(var, instanceVariableMap.get(var).get(GRB.DoubleAttr.X));
//        }
//
//        for (final DecisionVariable var : crossTreeVariableMap.keySet()) {
//            varVals.put(var, crossTreeVariableMap.get(var).get(GRB.DoubleAttr.X));
//        }
//
//        return varVals;
//    }
//
//    private GRBConstr constructConstraint(GRBModel model, Constraint con) {
//        final GRBLinExpr expr = new GRBLinExpr();
//
//        model.addGen
//
//        if (M == -1) {
//            throw new RuntimeException("M must either be set or initialized with a non-negative value!");
//        }
//
//        createExpressionTerms(expr, con.getLeft());
//
//        double right;
//        if (con.getRight().getVal() instanceof Symbol) {
//            right = M;
//        } else {
//            right = Double.parseDouble(con.getRight().getVal().getValue());
//        }
//
//        if (con.getRight().isNegative()) {
//            right = -right;
//        }
//
//        try {
//
//            if (con instanceof Leq) {
//                return model.addConstr(expr, GRB.LESS_EQUAL, right, con.getName());
//            }
//            if (con instanceof Geq) {
//                return model.addConstr(expr, GRB.GREATER_EQUAL, right, con.getName());
//            }
//            if (con instanceof Eq) {
//                return model.addConstr(expr, GRB.EQUAL, right, con.getName());
//            }
//            throw new RuntimeException("Constraint type was not recognized.");
//
//        } catch (final GRBException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    private void createExpressionTerms(GRBLinExpr expr, List<Term> terms) {
//        if (M == -1) {
//            throw new RuntimeException("M must either be set or initialized with a non-negative value!");
//        }
//
//        for (final Term term : terms) {
//            if (term instanceof BinaryTerm) {
//                double param;
//                if (((BinaryTerm) term).getParam() instanceof Symbol) {
//                    param = M;
//                } else {
//                    param = Double.parseDouble(((BinaryTerm) term).getParam().getValue());
//                }
//
//                if (((BinaryTerm) term).isNegative()) {
//                    param = -param;
//                }
//
//                final DecisionVariable var = ((BinaryTerm) term).getVar();
//                if (!createTerm(expr, param, var)) {
//                    throw new RuntimeException("No matching class found.");
//                }
//            }
//            if (term instanceof Var) {
//                double param;
//                if (((Var) term).isNegative()) {
//                    param = -1.0;
//                } else {
//                    param = 1.0;
//                }
//
//                final DecisionVariable var = ((Var) term).getVar();
//                if (!createTerm(expr, param, var)) {
//                    throw new RuntimeException("No matching class found.");
//                }
//            }
//        }
//    }
//
//    private boolean createTerm(GRBLinExpr expr, double param, DecisionVariable var) {
//        if (var instanceof FeatureInstanceVariable) {
//            expr.addTerm(param, instanceVariableMap.get(var));
//            return true;
//        }
//        if (var instanceof FeatureTypeVariable) {
//            expr.addTerm(param, typeVariableMap.get(var));
//            return true;
//        }
//        if (var instanceof CrossTreeConstraintVariable) {
//            expr.addTerm(param, crossTreeVariableMap.get(var));
//            return true;
//        }
//
//        return false;
//    }
//
//}
