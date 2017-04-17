package org.cardygan.ilp.api;

import org.cardygan.ilp.api.expr.ArithExpr;
import org.cardygan.ilp.api.expr.Neg;
import org.cardygan.ilp.api.expr.bool.*;
import org.cardygan.ilp.internal.Pair;
import org.cardygan.ilp.internal.expr.*;
import org.cardygan.ilp.internal.expr.cnf.CnfClause;
import org.cardygan.ilp.internal.expr.cnf.TseytinTransformer;
import org.cardygan.ilp.internal.util.RandomString;

import java.util.*;
import java.util.stream.Collectors;

import static org.cardygan.ilp.api.util.ExprDsl.*;

public class Model {

    private List<Constraint> constraints = new ArrayList<>();
    private Objective objective;

    private List<NormalizedArithExpr> normalizedArithConstraints = new ArrayList<>();
    private Optional<Integer> m;

    private int counter = 0;
    private String VARIABLE_PREFIX = "v";

    private Map<String, org.cardygan.ilp.api.expr.Var> vars = new HashMap<>();


    public List<org.cardygan.ilp.api.expr.Var> getVars() {
        return Collections.unmodifiableList(new ArrayList<>(vars.values()));
    }

    public Objective getObjective() {
        return objective;
    }


    public List<NormalizedArithExpr> getConstraints() {
        return Collections.unmodifiableList(normalizedArithConstraints);
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer();
        normalizedArithConstraints.forEach(c -> ret.append(c + "\n"));
        return ret.toString();
    }

    public Constraint newConstraint(String name) {
        Constraint cstr = new Constraint(name);
        constraints.add(cstr);
        return cstr;
    }

    /**
     * Creates new decision variable with given name.
     *
     * @param name
     * @return
     */
    public IntVar newIntVar(String name) {
        if (vars.containsKey(name)) {
            throw new IllegalStateException("Variable with name " + name + " already defined.");
        }
        IntVar var = new IntVar(name);
        vars.put(name, var);
        return var;
    }

    /**
     * Creates new variable of given type with an unique name id.
     *
     * @return
     */
    public IntVar newIntVar() {
        while (vars.containsKey(VARIABLE_PREFIX + counter)) {
            counter++;
        }

        return newIntVar(VARIABLE_PREFIX + counter);
    }


    /**
     * Creates new decision variable with given name.
     *
     * @param name
     * @return
     */
    public BinaryVar newBinaryVar(String name) {
        if (vars.containsKey(name)) {
            throw new IllegalStateException("Variable with name " + name + " already defined.");
        }
        BinaryVar var = new BinaryVar(name);
        vars.put(name, var);
        return var;
    }

    /**
     * Creates new variable of given type with an unique name id.
     *
     * @return
     */
    public BinaryVar newBinaryVar() {
        while (vars.containsKey(VARIABLE_PREFIX + counter)) {
            counter++;
        }

        return newBinaryVar(VARIABLE_PREFIX + counter);
    }

    public Objective newObjective(boolean maximize) {
        objective = new Objective(maximize);
        return objective;
    }

    public Result solve(Solver solver) {
        preProcessConstraints();
        return solver.solveProblem(this);
    }

    private void preProcessConstraints() {
        // preprocess constraints
        for (Constraint cstr : constraints) {
            if (cstr.getExpr() instanceof RelOp) {
                normalizedArithConstraints.add(preProcessArithExpr(cstr.getName(), (RelOp) cstr.getExpr()));
            } else if (cstr.getExpr() instanceof BoolExpr) {
                preProcessBoolExpr(cstr.getExpr());
            } else {
                throw new IllegalStateException("Unknown expression type.");
            }

        }
    }

    private NormalizedArithExpr preProcessArithExpr(String name, RelOp expr) {
        if (expr instanceof Leq) {
            return new LeqNormalizedArithExpr(name, expr);
        } else if (expr instanceof Geq) {
            return new GeqNormalizedArithExpr(name, expr);
        } else if (expr instanceof Eq) {
            return new EqNormalizedArithExpr(name, expr);
        } else {
            throw new IllegalStateException("Unknown RelOp type.");
        }
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

    public int getM(RelOp expr) {
        //TODO implement relOp specific BigM retrieval
        return getM();
    }

    public int getM() {
        if (!m.isPresent()) {
            throw new IllegalStateException("Big M was not set.");
        }
        return m.get();
    }

    public void setM(int m) {
        this.m = Optional.of(m);
    }
}
