package org.cardygan.ilp.internal.solver.milp;

import org.cardygan.ilp.api.model.ArithExpr;
import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.Neg;
import org.cardygan.ilp.api.model.bool.*;
import org.cardygan.ilp.internal.expr.BoolExprVisitor;
import org.cardygan.ilp.internal.expr.cnf.CnfClause;
import org.cardygan.ilp.internal.expr.cnf.CnfTransformer;
import org.cardygan.ilp.internal.expr.cnf.TseytinTransformer;
import org.cardygan.ilp.internal.expr.ExprSimplifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.cardygan.ilp.api.util.ExprDsl.*;

public abstract class AbstractCstrGenerator implements MILPConstrGenerator {

    //TODO make configurable
    protected final static double EPSILON = 0.0001;

    private final CnfTransformer cnfTransformer = new TseytinTransformer();

    @Override
    public LinearConstr[] transform(final BoolExpr boolExpr, final Model ilpModel) {
        final List<LinearConstr> ret = new ArrayList<>();

        // transform a = b to a<= b && a >= b
        final BoolExpr transExpr = transformEq(boolExpr);

        // compute CNF
        cnfTransformer.computeCnf(transExpr, ilpModel);


        List<CnfClause> cnfClauses = cnfTransformer.getClauses();

        Map<BinaryVar, RelOp> varMapping = cnfTransformer.getMapping();

        for (CnfClause clause : cnfClauses) {
            // create model c_i + ... + c_n >= 1 for each clause
            AtomicInteger rhs = new AtomicInteger(0);

            List<ArithExpr> summands = clause.getVars().entrySet().stream()
                    .map( // sum 1-var to sum if negated
                            e -> {
                                BinaryVar var = e.getKey();

                                if (e.getValue()) {
                                    rhs.incrementAndGet();
                                    return new Neg(var);
                                } else {
                                    return var;
                                }
                            })
                    .collect(Collectors.toList());

            RelOp tmpRelop = geq(sum(summands), param(1 - rhs.get()));
            Optional<LinearConstr> tmpCstr = ExprSimplifier.normalizeCstr(tmpRelop);
            if (tmpCstr.isPresent())
                ret.add(tmpCstr.get());
            else
                throw new IllegalStateException("This should never happen.");
        }

        // visit boolean variables of clause and transform to ILP
        for (Map.Entry<BinaryVar, RelOp> entry : varMapping.entrySet()) {

            RelOp relOp = entry.getValue();
            BinaryVar var = entry.getKey();

            ExprSimplifier.SimplifiedArithExpr expr = ExprSimplifier.simplify(sum(relOp.getLhs(), neg(relOp.getRhs())));

//            ExprSimplifier.SimplifiedArithExpr expr = ExprSimplifier.simplify(relOp);
            double rhs = -expr.getConstant();


            if (relOp instanceof Geq && rhs >= 0)
                ret.addAll(addGeqCstr(var, expr, false, ilpModel));
            else if (relOp instanceof Geq)
                ret.addAll(addLeqCstr(var, expr, true, ilpModel));
            else if (relOp instanceof Leq && rhs >= 0)
                ret.addAll(addLeqCstr(var, expr, false, ilpModel));
            else if (relOp instanceof Leq)
                ret.addAll(addGeqCstr(var, expr, true, ilpModel));
            else
                throw new IllegalStateException("Unknown BoolRel subtype.");
        }

        return ret.toArray(new LinearConstr[0]);
    }

    protected abstract List<LinearConstr> addLeqCstr(BinaryVar var, ExprSimplifier.SimplifiedArithExpr expr,
                                                     boolean isBothSidesNegated, final Model ilpModel);

    protected abstract List<LinearConstr> addGeqCstr(BinaryVar var, ExprSimplifier.SimplifiedArithExpr expr,
                                                     boolean isBothSidesNegated, final Model ilpModel);


    /**
     * Transforms an equals-to-expression to the conjunction of leq and geq
     *
     * @param expr
     * @return the transformed boolean expression
     */
    private static BoolExpr transformEq(BoolExpr expr) {
        return expr.accept(new BoolExprVisitor<BoolExpr>() {

            @Override
            public BoolExpr visit(And expr) {
                return new And(expr.getLhs().accept(this), expr.getRhs().accept(this));
            }

            @Override
            public BoolExpr visit(Or expr) {
                return new Or(expr.getLhs().accept(this), expr.getRhs().accept(this));
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

}
