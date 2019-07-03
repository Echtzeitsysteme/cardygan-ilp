package org.cardygan.ilp.api.model;

import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.model.bool.BoolExpr;
import org.cardygan.ilp.api.model.bool.RelOp;
import org.cardygan.ilp.api.solver.Solver;

import java.util.*;

public class Model {

    public final static String VARIABLE_PREFIX = "v";

    public final static double EPSILON = 0.0001;
    private final List<Constraint> constraints;
    private Objective objective;
    private Integer m;
    private int counter = 0;
    private final Map<String, Var> vars;
    private final Map<DoubleVar, DblBounds> dblBounds;
    private final Map<IntVar, IntBounds> intBounds;
    private final List<Sos1Constraint> sos1;

    private Model(Map<String, Var> vars,
                  Map<DoubleVar, DblBounds> dblBounds,
                  Map<IntVar, IntBounds> intBounds,
                  List<Sos1Constraint> sos1,
                  List<Constraint> constraints,
                  Integer m,
                  Objective objective) {
        this.vars = new HashMap<>(vars);
        this.dblBounds = new HashMap<>(dblBounds);
        this.intBounds = new HashMap<>(intBounds);
        this.constraints = new ArrayList<>(constraints);
        this.sos1 = new ArrayList<>(sos1);
        this.m = m;
        this.objective = objective;
    }

    public Model() {
        vars = new HashMap<>();
        dblBounds = new HashMap<>();
        intBounds = new HashMap<>();
        constraints = new ArrayList<>();
        sos1 = new ArrayList<>();
        m = null;
    }


    public Map<String, Var> getVars() {
        return Collections.unmodifiableMap(vars);
    }

    public Optional<Objective> getObjective() {
        return Optional.ofNullable(objective);
    }

    public List<Sos1Constraint> getSos1() {
        return Collections.unmodifiableList(sos1);
    }

    public List<Constraint> getConstraints() {
        return Collections.unmodifiableList(constraints);
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer();
        constraints.forEach(c -> ret.append(c + "\n"));
        return ret.toString();
    }

    public Constraint newConstraint(BoolExpr expr) {
        Constraint cstr = new Constraint(expr);
        constraints.add(cstr);
        return cstr;
    }

    public Constraint newConstraint(String name, BoolExpr expr) {
        Constraint cstr = new Constraint(name, expr);
        constraints.add(cstr);
        return cstr;
    }

    public DblBounds getBounds(DoubleVar var) {
        return dblBounds.get(var);
    }

    public IntBounds getBounds(IntVar var) {
        return intBounds.get(var);
    }

    public Map<DoubleVar, DblBounds> getDblBounds() {
        return Collections.unmodifiableMap(dblBounds);
    }

    public Map<IntVar, IntBounds> getIntBounds() {
        return Collections.unmodifiableMap(intBounds);
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
     * Creates new decision variable with given name and bounds.
     *
     * @param name
     * @return
     */
    public IntVar newIntVar(String name, int lb, int ub) {
        if (vars.containsKey(name)) {
            throw new IllegalStateException("Variable with name " + name + " already defined.");
        }
        IntVar var = new IntVar(name);
        vars.put(name, var);
        intBounds.put(var, new IntBounds(lb, ub));
        return var;
    }

    /**
     * Creates new decision variable with given name and lower bound.
     *
     * @param name
     * @return
     */
    public IntVar newIntVar(String name, int lb) {
        if (vars.containsKey(name)) {
            throw new IllegalStateException("Variable with name " + name + " already defined.");
        }
        IntVar var = new IntVar(name);
        vars.put(name, var);
        intBounds.put(var, new IntBounds(lb, -1));
        return var;
    }

    /**
     * Creates new decision variable with given lower bound.
     *
     * @param lb
     * @return
     */
    public IntVar newIntVar(int lb) {
        IntVar var = newIntVar();
        intBounds.put(var, new IntBounds(lb, -1));
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


    public void newSos1(List<Var> vars) {
        sos1.add(new Sos1Constraint(vars));
    }

    public void newSos1(Map<Var, Double> elements) {
        sos1.add(new Sos1Constraint(elements));
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

    /**
     * Creates new decision variable.
     *
     * @return
     */
    public DoubleVar newDoubleVar() {
        while (vars.containsKey(VARIABLE_PREFIX + counter)) {
            counter++;
        }

        return newDoubleVar(VARIABLE_PREFIX + counter);
    }

    /**
     * Creates new decision variable with given name.
     *
     * @param name
     * @return
     */
    public DoubleVar newDoubleVar(String name) {
        if (vars.containsKey(name)) {
            throw new IllegalStateException("Variable with name " + name + " already defined.");
        }
        DoubleVar var = new DoubleVar(name);
        vars.put(name, var);
        return var;
    }

    /**
     * Creates new decision variable with given name and bounds.
     *
     * @param name
     * @param lb
     * @param ub
     * @return
     */
    public DoubleVar newDoubleVar(String name, double lb, double ub) {
        if (vars.containsKey(name)) {
            throw new IllegalStateException("Variable with name " + name + " already defined.");
        }
        DoubleVar var = new DoubleVar(name);
        vars.put(name, var);
        dblBounds.put(var, new DblBounds(lb, ub));
        return var;
    }

    /**
     * Creates new decision variable with given name and lower bound.
     *
     * @param name
     * @param lb
     * @return
     */
    public DoubleVar newDoubleVar(String name, double lb) {
        if (vars.containsKey(name)) {
            throw new IllegalStateException("Variable with name " + name + " already defined.");
        }
        DoubleVar var = new DoubleVar(name);
        vars.put(name, var);
        dblBounds.put(var, new DblBounds(lb, -1));
        return var;
    }

//    public void removeVar(Var var) {
//        vars.remove(var.getName());
//    }

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

    private boolean varInList(String varName, List<Var> vars) {
        return vars.stream().filter(e -> e.getName().equals(varName)).findAny().isPresent();
    }

    public Objective newObjective(boolean maximize, ArithExpr expr) {
        objective = new Objective(maximize, expr);
        return objective;
    }

    public Result solve(Solver solver) {
//        if (objective == null) {
//            // add dummy empty objective
//            newObjective(true, new Sum());
//        }

        return solver.solve(this);
    }

    public Model copy(List<Constraint> constraints) {
        return new Model(vars,
                dblBounds,
                intBounds,
                sos1,
                constraints,
                m,
                objective);
    }

    public Model copy() {
        return new Model(vars,
                dblBounds,
                intBounds,
                sos1,
                constraints,
                m,
                objective);
    }

    public Optional<Integer> getM(RelOp expr) {
        //TODO implement relOp specific BigM retrieval
        return getM();
    }

    public Optional<Integer> getM() {
        return Optional.ofNullable(m);
    }

    public void setM(int m) {
        this.m = m;
    }


}
