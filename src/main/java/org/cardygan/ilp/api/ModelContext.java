package org.cardygan.ilp.api;

import org.cardygan.ilp.api.expr.ArithExpr;
import org.cardygan.ilp.api.expr.Neg;
import org.cardygan.ilp.api.expr.Var;
import org.cardygan.ilp.api.expr.bool.*;
import org.cardygan.ilp.internal.Coefficient;
import org.cardygan.ilp.internal.Pair;
import org.cardygan.ilp.internal.expr.BoolExprVisitor;
import org.cardygan.ilp.internal.expr.BoolLiteralToConstraintProcessor;
import org.cardygan.ilp.internal.expr.NormalizedArithExpr;
import org.cardygan.ilp.internal.expr.NormalizedArithExprCreator;
import org.cardygan.ilp.internal.expr.cnf.CnfClause;
import org.cardygan.ilp.internal.expr.cnf.TseytinTransformer;
import org.cardygan.ilp.internal.util.RandomString;
import org.cardygan.ilp.internal.util.Util;

import java.util.*;
import java.util.stream.Collectors;

import static org.cardygan.ilp.api.util.ExprDsl.*;

/**
 * Created by markus on 20.06.17.
 */
public class ModelContext {

    private final static String VARIABLE_PREFIX = "tmp";
    private final Model model;
    private List<NormalizedArithExpr> normalizedArithConstraints = new ArrayList<>();
    private Map<Constraint, Boolean> procConstraints = new HashMap<>();
    private List<Var> tmpVars = new ArrayList<>();
    private int counter = 0;
    private List<Set<Var>> sos1 = new ArrayList<>();

    public ModelContext(Model model) {
        this.model = model;
    }

    public Constraint newTmpConstraint(String name) {
        Constraint cstr = new Constraint(name);
        procConstraints.put(cstr, false);
        return cstr;
    }

    List<NormalizedArithExpr> getNormalizedConstraints() {
        return Collections.unmodifiableList(normalizedArithConstraints);
    }

    public void preProcessConstraints() {
        // init with constraints from model
        model.getConstraints().forEach(cstr -> procConstraints.put(cstr, false));

        // preprocess constraints
        while (procConstraints.keySet().stream().anyMatch(c -> !procConstraints.get(c))) {
            List<Constraint> tmpCstrs = new ArrayList<>(procConstraints.keySet().stream()
                    .filter(c -> !procConstraints.get(c))
                    .collect(Collectors.toList()));

            for (Constraint cstr : tmpCstrs) {
                procConstraints.put(cstr, true);
                if (cstr.getExpr() instanceof RelOp) {
                    normalizedArithConstraints.add(preProcessArithExpr(cstr.getName(), (RelOp) cstr.getExpr()));
                } else if (cstr.getExpr() instanceof BoolExpr) {
                    preProcessBoolExpr(cstr.getExpr());
                } else {
                    throw new IllegalStateException("Unknown expression type.");
                }
            }
        }
    }

    private NormalizedArithExpr preProcessArithExpr(String name, RelOp expr) {
        return NormalizedArithExprCreator.createArithExpr(name, expr);
    }

    private void preProcessBoolExpr(BoolExpr expr) {
        Pair<List<CnfClause>, Map<BinaryVar, RelOp>> transResult = computeCnf(transformExpr(expr));
        List<CnfClause> cnfClauses = transResult.getFirst();

        Map<BinaryVar, RelOp> varMapping = transResult.getSecond();

        BoolLiteralToConstraintProcessor processor = new BoolLiteralToConstraintProcessor(this);

        for (CnfClause clause : cnfClauses) {
            // create expr c_i + ... + c_n >= 1 for each clause
            int[] rhs = new int[]{0};
            List<ArithExpr> summands = clause.getVars().entrySet().stream()
                    .map( // sum 1-var to sum if negated
                            e -> {
                                BinaryVar var = e.getKey();

                                if (e.getValue()) {
                                    rhs[0]++;
                                    return new Neg(var);
                                } else {
                                    return var;
                                }
                            })
                    .collect(Collectors.toList());

            RelOp cstr = geq(sum(summands), param(1 - rhs[0]));

            normalizedArithConstraints.add(preProcessArithExpr(new RandomString(5).nextString(), cstr));


            // 3. visit boolean variables of clause and transform to ILP
            for (Map.Entry<BinaryVar, RelOp> entry : varMapping.entrySet()) {
                processor.process(entry.getKey(), entry.getValue());
            }
        }
    }

    private Pair<List<CnfClause>, Map<BinaryVar, RelOp>> computeCnf(BoolExpr expr) {
        TseytinTransformer trans = new TseytinTransformer(this, expr);
        trans.transform();

        return new Pair<>(trans.getClauses(), trans.getVarMapping());
    }

    private BoolExpr transformExpr(BoolExpr expr) {
        return expr.accept(new BoolExprVisitor<BoolExpr>() {

            @Override
            public BoolExpr visit(And expr) {
                return new And(expr.getElements()
                        .stream()
                        .map(e -> e.accept(this))
                        .collect(Collectors.toList()));
            }

            @Override
            public BoolExpr visit(Or expr) {
                return new Or(expr.getElements()
                        .stream()
                        .map(e -> e.accept(this))
                        .collect(Collectors.toList()));
            }

            @Override
            public BoolExpr visit(Xor expr) {
                return xor(expr.getLhs().accept(this), expr.getRhs().accept(this));
            }

            @Override
            public BoolExpr visit(Not expr) {
                return not(expr.getVal().accept(this));
            }

            @Override
            public BoolExpr visit(Impl expr) {
                return impl(expr.getLhs().accept(this), expr.getRhs().accept(this));
            }

            @Override
            public BoolExpr visit(BiImpl expr) {
                return bi_impl(expr.getLhs().accept(this), expr.getRhs().accept(this));
            }

            @Override
            public BoolExpr visit(BinaryVar expr) {
                return expr;
            }

            @Override
            public BoolExpr visit(RelOp expr) {
                // Rewrite equal equation to leq and geq equations
                if (expr instanceof Eq) {
                    Eq rel = (Eq) expr;
                    return and(leq(rel.getLhs(), rel.getRhs()), geq(rel.getLhs(), rel.getRhs()));
                }
                return expr;
            }
        });
    }

    public Optional<Integer> getM(RelOp expr) {
        return model.getM(expr);
    }

    private boolean varInList(String varName, List<Var> vars) {
        return vars.stream().filter(e -> e.getName().equals(varName)).findAny().isPresent();
    }

    public BinaryVar newBinaryVar() {
        String varName = VARIABLE_PREFIX + counter;
        while (varInList(varName, model.getVars()) || varInList(varName, tmpVars)) {
            varName = VARIABLE_PREFIX + counter++;
        }

        BinaryVar var = new BinaryVar(varName);

        tmpVars.add(var);
        return var;
    }

    public IntVar newIntVar() {
        String varName = VARIABLE_PREFIX + counter;
        while (varInList(varName, model.getVars()) || varInList(varName, tmpVars)) {
            varName = VARIABLE_PREFIX + counter++;
        }

        IntVar var = new IntVar(varName);

        tmpVars.add(var);
        return var;
    }

    public void addSos1(Set<Var> sos) {
        sos1.add(sos);
    }

    public List<Set<Var>> getSos1() {
        List<Set<Var>> ret = new ArrayList<>();
        ret.addAll(model.getSos1());
        ret.addAll(sos1);
        return Collections.unmodifiableList(ret);
    }

    public Objective getObjective() {
        return model.getObjective();
    }

    public List<Var> getVars() {
        List<Var> ret = new ArrayList<>();
        ret.addAll(model.getVars());
        ret.addAll(tmpVars);
        return ret;
    }

    public BinaryVar newBinaryVar(String name) {
        if (varInList(name, model.getVars()) || varInList(name, tmpVars)) {
            throw new IllegalStateException("Variable with name " + name + " already defined.");
        }
        BinaryVar var = new BinaryVar(name);
        tmpVars.add(var);
        return var;
    }
}
