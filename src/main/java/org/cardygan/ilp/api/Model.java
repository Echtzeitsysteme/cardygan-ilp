package org.cardygan.ilp.api;

import org.cardygan.ilp.api.expr.Var;
import org.cardygan.ilp.api.expr.bool.RelOp;
import org.cardygan.ilp.internal.Coefficient;
import org.cardygan.ilp.internal.util.Util;

import java.util.*;

public class Model {

    private final static String VARIABLE_PREFIX = "v";
    private List<Constraint> constraints = new ArrayList<>();

    private Objective objective;

    private Optional<Integer> m = Optional.empty();
    private int counter = 0;
    private Map<String, Var> vars = new HashMap<>();
    private List<Set<Var>> sos1 = new ArrayList<>();


    public List<Var> getVars() {
        return Collections.unmodifiableList(new ArrayList<>(vars.values()));
    }

    public Objective getObjective() {
        return objective;
    }

    public List<Set<Var>> getSos1() {
        return Collections.unmodifiableList(sos1);
    }


    public List<Constraint> getConstraints() {
        return Collections.unmodifiableList(new ArrayList<>(constraints));
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer();
        constraints.forEach(c -> ret.append(c + "\n"));
        return ret.toString();
    }

    public Constraint newConstraint(String name) {
        Constraint cstr = new Constraint(name);
        constraints.add(cstr);
        return cstr;
    }

    /**
     * Creates new decision variable with given name.
     *
     * @param name
     * @return
     */
    public IntVar newIntVar(String name) {
        if (vars.containsKey(name)) {
            throw new IllegalStateException("Variable with name " + name + " already defined.");
        }
        IntVar var = new IntVar(name);
        vars.put(name, var);
        return var;
    }

    /**
     * Creates new variable of given type with an unique name id.
     *
     * @return
     */
    public IntVar newIntVar() {
        while (vars.containsKey(VARIABLE_PREFIX + counter)) {
            counter++;
        }

        return newIntVar(VARIABLE_PREFIX + counter);
    }

    public void addSos1(Set<Var> vars) {
        sos1.add(vars);
        List<Coefficient> coefficients = new ArrayList<>();
        for (Var var : vars) {
            coefficients.add(Util.coef(1, var));
        }
        newConstraint("sos").setExpr(Util.geq(coefficients, 1));
    }


    /**
     * Creates new decision variable with given name.
     *
     * @param name
     * @return
     */
    public BinaryVar newBinaryVar(String name) {
        if (vars.containsKey(name)) {
            throw new IllegalStateException("Variable with name " + name + " already defined.");
        }
        BinaryVar var = new BinaryVar(name);
        vars.put(name, var);
        return var;
    }

    public void removeVar(Var var) {
        vars.remove(var.getName());
    }

    /**
     * Creates new variable of given type with an unique name id.
     *
     * @return
     */
    public BinaryVar newBinaryVar() {
        while (vars.containsKey(VARIABLE_PREFIX + counter)) {
            counter++;
        }

        return newBinaryVar(VARIABLE_PREFIX + counter);
    }

    public Objective newObjective(boolean maximize) {
        objective = new Objective(maximize);
        return objective;
    }

    public Result solve(Solver solver) {
        // reset previous transformed constraints
//        normalizedArithConstraints.clear();
//        procConstraints.clear();
//        constraints.forEach(cstr -> procCons  traints.put(cstr, false));
        ModelContext ctx = new ModelContext(this);

        ProxyResolver resolver = new ProxyResolver(this, solver);
        resolver.resolve();

        ctx.preProcessConstraints();

        return solver.solve(ctx);
    }


    public Optional<Integer> getM(RelOp expr) {
        //TODO implement relOp specific BigM retrieval
        return getM();
    }

    public Optional<Integer> getM() {
        return m;
    }

    public void setM(Optional<Integer> m) {
        this.m = m;
    }

    public void setM(int m) {
        this.m = Optional.of(m);
    }


}
