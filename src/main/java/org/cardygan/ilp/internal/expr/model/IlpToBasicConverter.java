package org.cardygan.ilp.internal.expr.model;

import org.cardygan.ilp.api.model.*;
import org.cardygan.ilp.api.model.bool.*;
import org.cardygan.ilp.internal.expr.ArithExprSimplifier;
import org.cardygan.ilp.internal.expr.BoolExprVisitor;
import org.cardygan.ilp.internal.expr.BoolLiteralToConstraintProcessor;
import org.cardygan.ilp.internal.expr.Coefficient;
import org.cardygan.ilp.internal.expr.cnf.CnfClause;
import org.cardygan.ilp.internal.expr.cnf.TseytinTransformer;
import org.cardygan.ilp.internal.util.Pair;
import org.cardygan.ilp.internal.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.cardygan.ilp.api.util.ExprDsl.*;

public class IlpToBasicConverter {

    public static BasicModel convert(Model model) {
        // copy model and transform equals-to expressions to inequalities
        Model copiedModel = model.copy(
                model.getConstraints().stream()
                        .map(c ->
                        {
                            if (c.getName().isPresent())
                                return new Constraint(c.getName().get(), transformEq(c.getExpr()));
                            else
                                return new Constraint(transformEq(c.getExpr()));
                        })
                        .collect(Collectors.toList())
        );


        long countBoolCstr = copiedModel.getConstraints().stream()
                .filter(c -> !(c.getExpr() instanceof RelOp)).count();

        // transform boolean constraints to inequalities
        new ArrayList<>(copiedModel.getConstraints()).stream()
                .filter(c -> !(c.getExpr() instanceof RelOp))
                .forEach(c -> preProcessBoolExpr(c.getExpr(), copiedModel));

        // Assume that we did not add further boolean constraints in last transformation step
        Util.assertTrue(countBoolCstr == copiedModel.getConstraints().stream()
                .filter(c -> !(c.getExpr() instanceof RelOp)).count());

        // transform remaining inequalities to basic ILP model
        List<NormalizedArithExpr> normalizedArithConstraints = new ArrayList<>();
        copiedModel.getConstraints().stream()
                .filter(c -> (c.getExpr() instanceof RelOp))
                .forEach(c ->
                        normalizedArithConstraints.add(preProcessArithExpr((RelOp) c.getExpr(), c))
                );

        // process objective
        final BasicObjective basicObjective;
        if (copiedModel.getObjective().isPresent())
            basicObjective = processObjective(copiedModel.getObjective().get());
        else
            basicObjective = null;

        if (copiedModel.getM().isPresent())
            return new BasicModel(normalizedArithConstraints,
                    basicObjective,
                    copiedModel.getM().get(),
                    copiedModel.getVars(),
                    copiedModel.getBounds(),
                    copiedModel.getSos1()
            );
        else return new BasicModel(normalizedArithConstraints,
                basicObjective,
                copiedModel.getVars(),
                copiedModel.getBounds(),
                copiedModel.getSos1()
        );
    }

    private static BasicObjective processObjective(Objective objective) {

        ArithExprSimplifier simplifier = new ArithExprSimplifier(objective.getExpr());
        List<Coefficient> coefficients = simplifier.getSummands().stream().map(p -> Util.coef(p(p.getFirst()), p.getSecond())).collect(Collectors.toList());
        Double constant = simplifier.getConstant();

        return new BasicObjective(objective.isMax(), constant, coefficients);
    }


    private static NormalizedArithExpr preProcessArithExpr(RelOp expr, Constraint c) {
        return NormalizedArithExprCreator.createArithExpr(expr, c);
    }

    private static void preProcessBoolExpr(BoolExpr expr,
                                           Model model) {
        Pair<List<CnfClause>, Map<BinaryVar, RelOp>> transResult = computeCnf(expr, model);
        List<CnfClause> cnfClauses = transResult.getFirst();

        Map<BinaryVar, RelOp> varMapping = transResult.getSecond();

        BoolLiteralToConstraintProcessor processor = new BoolLiteralToConstraintProcessor(model);

        for (CnfClause clause : cnfClauses) {
            // create model c_i + ... + c_n >= 1 for each clause
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

            model.newConstraint(geq(sum(summands), param(1 - rhs[0])));

            // 3. visit boolean variables of clause and transform to ILP
            for (Map.Entry<BinaryVar, RelOp> entry : varMapping.entrySet()) {
                processor.process(entry.getKey(), entry.getValue());
            }
        }
    }

    private static Pair<List<CnfClause>, Map<BinaryVar, RelOp>> computeCnf(BoolExpr expr, Model model) {
        TseytinTransformer trans = new TseytinTransformer(model, expr);
        trans.transform();

        return new Pair<>(trans.getClauses(), trans.getVarMapping());
    }

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

}
