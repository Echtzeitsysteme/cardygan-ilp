package org.cardygan.ilp;

import java.util.*;

import static org.cardygan.ilp.IlpDsl.coef;

public abstract class IlpConstraint {

    protected final IlpParam rhs;
    private final String name;
    private final List<Coefficient> coefficients;
    private final List<Coefficient> coefficientsConsolidated;


    public IlpConstraint(String name, List<Coefficient> coefficients, IlpParam rhs) {
        this.rhs = rhs;
        this.name = name;
        this.coefficients = Collections.unmodifiableList(coefficients);
        this.coefficientsConsolidated = Collections.unmodifiableList(consolidate(coefficients));
    }

    private List<Coefficient> consolidate(List<Coefficient> coefficients) {
        Map<IlpVar, Coefficient> ret = new HashMap<>();
        for (Coefficient coef : coefficients) {
            if (ret.containsKey(coef.getIlpVar())) {
                double curVal = ret.get(coef.getIlpVar()).getIlpParam().getVal();
                double newVal = curVal + coef.getIlpParam().getVal();
                if (newVal == 0){
                    ret.remove(coef.getIlpVar());
                } else {
                    ret.put(coef.getIlpVar(), coef(newVal, coef.getIlpVar()));
                }
            } else {
                ret.put(coef.getIlpVar(), coef);
            }
        }

        return new ArrayList<>(ret.values());
    }

    public String getName() {
        return name;
    }

    public IlpParam getRhs() {
        return rhs;
    }

    public List<Coefficient> getCoefficients() {
        return coefficients;
    }

    public List<Coefficient> getCoefficientsConsolidated() {
        return coefficientsConsolidated;
    }
}
