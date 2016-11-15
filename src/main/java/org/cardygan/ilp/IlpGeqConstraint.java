package org.cardygan.ilp;

import java.util.List;

/**
 * Created by markus on 24.10.16.
 */
public class IlpGeqConstraint extends IlpConstraint {

    public IlpGeqConstraint(String name, List<Coefficient> coefficients, IlpParam rhs) {
        super(name, coefficients, rhs);
    }
}
