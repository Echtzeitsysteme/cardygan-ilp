package org.cardygan.ilp.api.model.bool;


import org.cardygan.ilp.internal.expr.BoolExprVisitor;
import org.cardygan.ilp.internal.util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Or implements BoolExpr {

    private final BoolExpr lhs;
    private final BoolExpr rhs;

    public Or(BoolExpr lhs, BoolExpr rhs) {
        Util.assertNotNull(lhs, rhs);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    /**
     * Creates conjunction of exprs [s1, s2, s3, s4].
     * <p>
     * Parameters lhs and rhs are initialized the following way:
     * <p>
     * [s1, s2, s3, s4] = ((s1 || s2) || s3) || s4
     * lhs = ((s1 || s2) || s3)
     * rhs = s4
     *
     * @param Array of at least two exprs
     */
    public Or(BoolExpr... exprs) {
        this(Arrays.asList(exprs));
    }

    /**
     * Creates conjunction of exprs [s1, s2, s3, s4].
     * <p>
     * Parameters lhs and rhs are initialized the following way:
     * <p>
     * [s1, s2, s3, s4] = ((s1 || s2) || s3) || s4
     * lhs = ((s1 || s2) || s3)
     * rhs = s4
     *
     * @param List of at least two exprs
     */
    public Or(List<BoolExpr> exprs) {
        Util.assertNotNull(exprs);

        if (exprs.size() < 2)
            throw new IllegalStateException("A conjuction must have at least two elements: lhs and rhs.");

        else if (exprs.size() == 2) {
            this.lhs = exprs.get(0);
            this.rhs = exprs.get(1);
        } else {
            this.rhs = exprs.get(exprs.size() - 1);

            this.lhs = exprs.subList(0, exprs.size() - 1).stream().reduce(And::new).get();
        }
    }

    public BoolExpr getLhs() {
        return lhs;
    }

    public BoolExpr getRhs() {
        return rhs;
    }

    @Override
    public <T> T accept(BoolExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Or or = (Or) o;
        return lhs.equals(or.lhs) &&
                rhs.equals(or.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lhs, rhs);
    }
}
