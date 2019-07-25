package org.cardygan.ilp.api.model;

import org.cardygan.ilp.internal.expr.ArithExprVisitor;
import org.cardygan.ilp.internal.util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Sum extends ArithExpr {

    private final ArithExpr lhs;
    private final ArithExpr rhs;


    public Sum(ArithExpr lhs, ArithExpr rhs) {
        Util.assertNotNull(lhs, rhs);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    /**
     * Creates sum of summands [s1, s2, s3, s4].
     * <p>
     * Parameters lhs and rhs are initialized the following way:
     * <p>
     * [s1, s2, s3, s4] = ((s1 + s2) + s3) + s4
     * lhs = ((s1 + s2) + s3)
     * rhs = s4
     *
     * @param array of at least two summands
     */
    public Sum(ArithExpr... summands) {
        Util.assertNotNull((Object[]) summands);

        if (summands.length < 2)
            throw new IllegalStateException("A sum must have at least two summands: lhs and rhs.");

        else if (summands.length == 2) {
            this.lhs = summands[0];
            this.rhs = summands[1];
        } else {
            this.rhs = summands[summands.length - 1];

            this.lhs = Arrays.asList(summands).subList(0, summands.length - 1).stream().reduce(Sum::new).get();
        }
    }

    /**
     * Creates sum of summands [s1, s2, s3, s4].
     * <p>
     * Parameters lhs and rhs are initialized the following way:
     * <p>
     * [s1, s2, s3, s4] = ((s1 + s2) + s3) + s4
     * lhs = ((s1 + s2) + s3)
     * rhs = s4
     *
     * @param exprs of at least two summands
     */
    public Sum(List<ArithExpr> exprs) {
        Util.assertNotNull(exprs);

        if (exprs.size() < 2)
            throw new IllegalStateException("A conjunction must have at least two elements: lhs and rhs.");

        else if (exprs.size() == 2) {
            this.lhs = exprs.get(0);
            this.rhs = exprs.get(1);
        } else {
            this.rhs = exprs.get(exprs.size() - 1);

            this.lhs = exprs.subList(0, exprs.size() - 1).stream().reduce(Sum::new).get();
        }
    }

    public ArithExpr getLhs() {
        return lhs;
    }

    public ArithExpr getRhs() {
        return rhs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sum sum = (Sum) o;
        return lhs.equals(sum.lhs) &&
                rhs.equals(sum.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lhs, rhs);
    }

    @Override
    public <T> T accept(ArithExprVisitor<T> visitor) {
        return visitor.visit(this);
    }


}
