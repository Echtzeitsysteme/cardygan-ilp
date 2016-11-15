package org.cardygan.ilp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class IlpDsl {

    public static Objective obj(List<Coefficient> coefficients, boolean max) {
        return new Objective(coefficients, max);
    }

    public static Coefficient coef(IlpParam ilpParam, IlpVar ilpVar) {
        return new Coefficient(ilpParam, ilpVar);
    }

    public static Coefficient coef(int param, IlpVar ilpVar) {
        return new Coefficient(p(param), ilpVar);
    }

    public static IlpDoubleParam p(double val){
        return new IlpDoubleParam(val);
    }

    public static IlpBinaryVar b_var(String name){
        return new IlpBinaryVar(name);
    }

    public static IlpIntVar i_var(String name){
        return new IlpIntVar(name);
    }

    public static IlpDoubleVar d_var(String name){
        return new IlpDoubleVar(name);
    }

    public static List<Coefficient> neg(List<Coefficient> coefficients){
        return coefficients.stream().map(c -> neg(c)).collect(Collectors.toList());
    }

    public static Coefficient neg(Coefficient coefficient){
        return new Coefficient(p(-coefficient.getIlpParam().getVal()), coefficient.getIlpVar());
    }

    public static Coefficient coef(double param, IlpVar ilpVar) {
        return new Coefficient(p(param), ilpVar);
    }

    public static CstrCoef cstr(String name) {
        return new Cstr(name);
    }

    public interface CstrCoef {

        public CstrRhs sum(IlpVar... ilpVars);

        public CstrRhs sum(Coefficient... coefficients);

        public CstrRhs sum(List<Coefficient> coefficients);

        public CstrRhs sum(List<Coefficient>... coefficients);
    }

    public interface CstrRhs {
        IlpConstraint geq(int rhs);

        IlpConstraint geq(double rhs);

        IlpConstraint geq(IlpParam rhs);

        IlpConstraint leq(int rhs);

        IlpConstraint leq(double rhs);

        IlpConstraint leq(IlpParam rhs);

        IlpConstraint eq(int rhs);

        IlpConstraint eq(double rhs);

        IlpConstraint eq(IlpParam rhs);


    }

    public static class Cstr implements CstrRhs, CstrCoef {

        private final String name;
        private List<Coefficient> coefficients;

        public Cstr(String name) {
            this.name = name;
        }


        @Override
        public CstrRhs sum(IlpVar... ilpVars) {
            this.coefficients = Stream.of(ilpVars).map(v -> coef(1, v)).collect(Collectors.toList());
            return this;
        }

        @Override
        public CstrRhs sum(Coefficient... coefficients) {
            this.coefficients = Arrays.asList(coefficients);
            return this;
        }

        @Override
        public CstrRhs sum(List<Coefficient> coefficients) {
            this.coefficients = coefficients;
            return this;
        }

        @Override
        public CstrRhs sum(List<Coefficient>... coefficients) {
            this.coefficients = new ArrayList<>();
            for (List<Coefficient> c : coefficients) {
                this.coefficients.addAll(c);
            }
            return this;
        }


        @Override
        public IlpConstraint geq(int rhs) {
            return new IlpGeqConstraint(name, coefficients, p(rhs));
        }

        @Override
        public IlpConstraint geq(double rhs) {
            return new IlpGeqConstraint(name,coefficients,p(rhs));
        }

        @Override
        public IlpConstraint geq(IlpParam rhs) {
            return new IlpGeqConstraint(name, coefficients, rhs);
        }

        @Override
        public IlpConstraint leq(int rhs) {
            return new IlpLeqConstraint(name, coefficients, p(rhs));
        }

        @Override
        public IlpConstraint leq(double rhs) {
            return new IlpLeqConstraint(name,coefficients,p(rhs));
        }

        @Override
        public IlpConstraint leq(IlpParam rhs) {
            return new IlpLeqConstraint(name, coefficients, rhs);
        }

        @Override
        public IlpConstraint eq(int rhs) {
            return new IlpEqConstraint(name,coefficients,p(rhs));
        }

        @Override
        public IlpConstraint eq(double rhs) {
            return new IlpEqConstraint(name,coefficients,p(rhs));
        }

        @Override
        public IlpConstraint eq(IlpParam rhs) {
            return new IlpEqConstraint(name,coefficients,rhs);
        }
    }

}
