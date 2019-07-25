package org.cardygan.ilp.internal.expr.cnf;

import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.bool.*;
import org.cardygan.ilp.internal.expr.BoolExprVisitor;
import org.cardygan.ilp.internal.expr.ExprSimplifier;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Transforms a arbitrary propositional logic expression in its CNF
 * representation using
 * {@link <a href="https://en.wikipedia.org/wiki/Tseytin_transformation">Tseytin
 * transformation</a>}.
 *
 * @author markus
 */
public class TseytinTransformer implements CnfTransformer {

    private Map<BoolExpr, BinaryVar> vars;
    private Map<BinaryVar, RelOp> varMapping;
    private List<CnfClause> clauses;
    private BoolVarGen varGen;


    @Override
    public void computeCnf(BoolExpr expr, Model model) {
        varGen = new BoolVarGen(model);
        varMapping = new HashMap<>();
        vars = new HashMap<>();

        clauses = new ArrayList<>();

        if (isInCnf(expr)) {
            if (expr instanceof And) {
                addCnfAnd((And) expr);
            }

            if (expr instanceof Or) {
                addCnfOr((Or) expr);
            }

        } else {
            expr.accept(new TreeVisitor());
        }
    }

    private boolean isInCnf(BoolExpr expr) {
        if (expr instanceof And) {
            List<BoolExpr> ret = ExprSimplifier.toNAryAnd((And) expr);
            return ret.stream().allMatch(it -> it instanceof BoolLiteral);
        }
        if (expr instanceof Or) {
            List<BoolExpr> ret = ExprSimplifier.toNAryOr((Or) expr);
            return ret.stream().allMatch(it -> it instanceof BoolLiteral);
        }
        return false;
    }

    private void addCnfAnd(And expr) {

        for (BoolLiteral lit : ExprSimplifier.toNAryAnd(expr).stream()
                .map(it -> (BoolLiteral) it).collect(Collectors.toList())) {
            CnfClause clause = new CnfClause();
            if (lit instanceof BinaryVar) {
                clause.add((BinaryVar) lit);
                BinaryVar var = (BinaryVar) lit;
                vars.put(var, var);
            } else if (lit instanceof RelOp) {
                BinaryVar var = varGen.newVar();
                clause.add(var);
                vars.put(lit, var);
                varMapping.put(var, (RelOp) lit);
            } else {
                throw new IllegalStateException("Unknown literal type.");
            }
            clauses.add(clause);
        }

    }

    private void addCnfOr(Or expr) {
        CnfClause clause = new CnfClause();

        for (BoolLiteral lit : ExprSimplifier.toNAryOr(expr).stream()
                .map(it -> (BoolLiteral) it).collect(Collectors.toList())) {

            if (lit instanceof BinaryVar) {
                clause.add((BinaryVar) lit);
                BinaryVar var = (BinaryVar) lit;
                vars.put(var, var);
            } else if (lit instanceof RelOp) {
                BinaryVar var = varGen.newVar();
                clause.add(var);
                vars.put(lit, var);
                varMapping.put(var, (RelOp) lit);
            } else {
                throw new IllegalStateException("Unknown literal type.");
            }

        }
        clauses.add(clause);
    }


    @Override
    public Map<BinaryVar, RelOp> getMapping() {
        return varMapping;
    }

    @Override
    public List<CnfClause> getClauses() {
        return clauses;
    }

    public Map<BoolExpr, BinaryVar> getVars() {
        return vars;
    }

    public Map<BinaryVar, RelOp> getVarMapping() {
        return varMapping;
    }

    private BinaryVar newVar() {
        if (varGen.isFirst()) {
            BinaryVar x = varGen.newVar();
            CnfClause clause = new CnfClause();
            clause.add(x);
            clauses.add(clause);
            return x;
        } else {
            return varGen.newVar();
        }
    }


    private class TreeVisitor implements BoolExprVisitor<BinaryVar> {


        @Override
        public BinaryVar visit(Not expr) {
            BinaryVar x = newVar();
            vars.put(expr, x);

            BinaryVar children = vars.get(expr.getVal());
            if (children == null) {
                children = expr.getVal().accept(this);
            }

            CnfClause clause = new CnfClause();
            clause.add(x);
            clause.add(children);
            clauses.add(clause);
            clause = new CnfClause();
            clause.add(x, true);
            clause.add(children, true);
            clauses.add(clause);

            return x;
        }

        @Override
        public BinaryVar visit(And expr) {
            BinaryVar x = newVar();
            vars.put(expr, x);

            List<BinaryVar> x_c = new LinkedList<>();
            for (BoolExpr e : ExprSimplifier.toNAryAnd(expr)) {
                BinaryVar y = vars.get(e);
                if (y == null) {
                    y = e.accept(this);
                }
                x_c.add(y);
            }

            CnfClause clause = new CnfClause();
            clause.add(x);
            for (BinaryVar y : x_c) {
                clause.add(y, true);
            }
            clauses.add(clause);

            for (BinaryVar y : x_c) {
                clause = new CnfClause();
                clause.add(x, true);
                clause.add(y);
                clauses.add(clause);
            }

            return x;
        }

        @Override
        public BinaryVar visit(Or expr) {
            BinaryVar x = newVar();
            vars.put(expr, x);

            List<BinaryVar> x_c = new LinkedList<>();
            for (BoolExpr e : ExprSimplifier.toNAryOr(expr)) {
                BinaryVar y = vars.get(e);
                if (y == null) {
                    y = e.accept(this);
                }
                x_c.add(y);
            }

            CnfClause clause = new CnfClause();
            clause.add(x, true);
            clause.addAll(x_c);
            clauses.add(clause);

            for (BinaryVar y : x_c) {
                clause = new CnfClause();
                clause.add(x);
                clause.add(y, true);
                clauses.add(clause);
            }

            return x;
        }

        @Override
        public BinaryVar visit(Impl expr) {
            BinaryVar x = newVar();
            vars.put(expr, x);

            BinaryVar a = vars.get(expr.getLhs());
            if (a == null) {
                a = expr.getLhs().accept(this);
            }

            BinaryVar b = vars.get(expr.getRhs());
            if (b == null) {
                b = expr.getRhs().accept(this);
            }

            CnfClause clause = new CnfClause();
            clause.add(x, true);
            clause.add(a, true);
            clause.add(b);
            clauses.add(clause);

            clause = new CnfClause();
            clause.add(x);
            clause.add(a);
            clauses.add(clause);

            clause = new CnfClause();
            clause.add(x);
            clause.add(b, true);
            clauses.add(clause);

            return x;
        }

        @Override
        public BinaryVar visit(BiImpl expr) {
            BinaryVar x = newVar();
            vars.put(expr, x);

            BinaryVar a = vars.get(expr.getLhs());
            if (a == null) {
                a = expr.getLhs().accept(this);
            }

            BinaryVar b = vars.get(expr.getRhs());
            if (b == null) {
                b = expr.getRhs().accept(this);
            }

            CnfClause clause = new CnfClause();
            clause.add(x, true);
            clause.add(a, true);
            clause.add(b);
            clauses.add(clause);

            clause = new CnfClause();
            clause.add(x, true);
            clause.add(a);
            clause.add(b, true);
            clauses.add(clause);

            clause = new CnfClause();
            clause.add(x);
            clause.add(a, true);
            clause.add(b, true);
            clauses.add(clause);

            clause = new CnfClause();
            clause.add(x);
            clause.add(a);
            clause.add(b);
            clauses.add(clause);

            return x;
        }


        @Override
        public BinaryVar visit(Xor expr) {
            BinaryVar x = newVar();
            vars.put(expr, x);

            BinaryVar a = vars.get(expr.getLhs());
            if (a == null) {
                a = expr.getLhs().accept(this);
            }

            BinaryVar b = vars.get(expr.getRhs());
            if (b == null) {
                b = expr.getRhs().accept(this);
            }

            CnfClause clause = new CnfClause();
            clause.add(x, true);
            clause.add(a, true);
            clause.add(b, true);
            clauses.add(clause);

            clause = new CnfClause();
            clause.add(x, true);
            clause.add(a);
            clause.add(b);
            clauses.add(clause);

            clause = new CnfClause();
            clause.add(x);
            clause.add(a, true);
            clause.add(b);
            clauses.add(clause);

            clause = new CnfClause();
            clause.add(x);
            clause.add(a);
            clause.add(b, true);
            clauses.add(clause);

            return x;
        }


        @Override
        public BinaryVar visit(BinaryVar expr) {

            // if no variable was created before, passed BinaryVar is root node of AST.
            // In this case CNF only consists of one variable
            if (varGen.isFirst()) {
                CnfClause clause = new CnfClause();
                clause.add(expr);
                clauses.add(clause);
            }

            vars.put(expr, expr);

            return expr;
        }

        @Override
        public BinaryVar visit(RelOp expr) {
            BinaryVar x = varGen.newVar();

            varMapping.put(x, expr);

            // if no variable was created before, passed BinaryVar is root node of AST.
            // In this case CNF only consists of one variable
            if (varGen.isFirst()) {
                CnfClause clause = new CnfClause();
                clause.add(x);
                clauses.add(clause);
            }
            vars.put(expr, x);

            return x;
        }

    }

    private class BoolVarGen {

        private final Model model;
        private int counter = 0;

        BoolVarGen(Model model) {
            this.model = model;
        }

        BinaryVar newVar() {
            counter++;
            return model.newBinaryVar();
        }

        boolean isFirst() {
            return counter == 0;
        }

    }


}
