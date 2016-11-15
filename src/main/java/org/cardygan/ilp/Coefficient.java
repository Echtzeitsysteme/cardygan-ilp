package org.cardygan.ilp;

import java.util.Objects;

/**
 * Created by markus on 24.10.16.
 */
public class Coefficient {

    private final IlpParam ilpParam;
    private final IlpVar ilpVar;

    public Coefficient(IlpParam ilpParam, IlpVar ilpVar) {
        this.ilpParam = ilpParam;
        this.ilpVar = ilpVar;
    }

    public IlpParam getIlpParam() {
        return ilpParam;
    }

    public IlpVar getIlpVar() {
        return ilpVar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coefficient that = (Coefficient) o;
        return Objects.equals(ilpParam, that.getIlpParam()) &&
                Objects.equals(ilpVar, that.getIlpVar());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ilpParam, ilpVar);
    }

    @Override
    public String toString() {
        return "("+ ilpParam.getVal()+","+ ilpVar.getName()+")";
    }
}
