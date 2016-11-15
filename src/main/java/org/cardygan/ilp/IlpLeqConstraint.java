package org.cardygan.ilp;

import java.util.List;

/**
 * Created by markus on 24.10.16.
 */
public class IlpLeqConstraint extends IlpConstraint {

    public IlpLeqConstraint(String name, List<Coefficient> coefficients, IlpParam rhs) {
        super( name, coefficients, rhs);
    }
}
