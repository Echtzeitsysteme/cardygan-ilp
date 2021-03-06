package org.cardygan.ilp.internal.solver.milp;

import ilog.concert.*;
import ilog.cplex.CpxNumVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;
import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.Result.SolverStatus;
import org.cardygan.ilp.api.model.Constraint;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.internal.solver.milp.LinearConstr.Type;
import org.cardygan.ilp.internal.util.LibraryUtil;
import org.cardygan.ilp.internal.util.ModelException;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * This class represents the implementation of the interface MILPSolver for IBMs CPLEX solver.
 *
 * @author Maximilian Kratz <maximilian.kratz@stud.tu-darmstadt.de>
 */
public class CplexSolver extends MILPSolver {

    private IloCplex solver;
    private boolean disposed;


    public CplexSolver() {
        this(false, true, -1, null, -1, null);
    }

    /**
     * Constructor for initializing an object.
     *
     * @param logging     True if logging to console should be enabled.
     * @param presolve    True if presolving should be enabled.
     * @param timeout     Time limit in timeoutUnit.
     * @param timeoutUnit Time unit for time out.
     * @param seed        Random seed for solver.
     * @param libPath     Path to CPLEX library (as String).
     */
    private CplexSolver(boolean logging, boolean presolve, long timeout, TimeUnit timeoutUnit,
                        int seed, String libPath) {
        init(presolve, logging, timeout, timeoutUnit, seed, libPath);
    }

    /**
     * Constructor for initializing an object.
     *
     * @param gen         A MILPConstrGenerator that gets set up in super constructor.
     * @param logging     True if logging to console should be enabled.
     * @param presolve    True if presolving should be enabled.
     * @param timeout     Time limit in timeoutUnit.
     * @param timeoutUnit Time unit for time out.
     * @param seed        Random seed for solver.
     * @param libPath     Path to CPLEX library (as String).
     */
    private CplexSolver(MILPConstrGenerator gen, boolean logging, boolean presolve, long timeout, TimeUnit timeoutUnit,
                        int seed, String libPath) {
        super(gen);
        init(presolve, logging, timeout, timeoutUnit, seed, libPath);
    }

    /**
     * Initializes the CPLEX solver backend.
     *
     * @param presolve    True if CPLEX should use presolving.
     * @param logging     True if CPLEX should log to console.
     * @param timeout     Time limit in timeoutUnit.
     * @param timeoutUnit Time out unit.
     * @param seed        Random seed value.
     * @param libPath     Library path for CPLEX (as String).
     */
    private void init(boolean presolve, boolean logging, long timeout, TimeUnit timeoutUnit, int seed, String libPath) {
        if (libPath != null && libPath.length() != 0) {
            LibraryUtil.loadLibraryFromPath(libPath);
        }

        try {
            solver = new IloCplex();

            // Setup logging
            setLogging(logging);

            // Timeout
            if (timeout >= 0)
                // Set parameter for timeout in seconds
                solver.setParam(IloCplex.DoubleParam.TiLim, timeoutUnit.toSeconds(timeout));


            // Random Seed
            if (seed != -1)
                solver.setParam(IloCplex.IntParam.RandomSeed, seed);

            setPresolve(presolve);


        } catch (IloException ex) {
            ex.printStackTrace();
            throw new IllegalStateException("Could not initialize CPLEX backend.");
        }
    }

    public void setPresolve(boolean presolve) {
        try {
            solver.setParam(IloCplex.BooleanParam.PreInd, presolve);
        } catch (IloException e) {
            throw new IllegalStateException("Could not initialize CPLEX backend.");
        }
    }


    @Override
    public void addCstr(String name, LinearConstr cstr) {
        if (name == null || name.length() == 0) {
            throw new NullPointerException("Provided parameter 'name' was invalid!");
        }

        if (cstr == null) {
            throw new NullPointerException("Provided parameter 'cstr' was invalid!");
        }

        if (hasConstraint(name)) {
            throw new ModelException("Constraint with given name already exists in model!");
        }


        //Get parameters from constraint
        final LinearConstr.Type type = cstr.getType();
        final double[] params = cstr.getParams();
        final Var[] vars = cstr.getVars();

        try {
            // If type is SOS (special ordered set)
            if (type == Type.SOS) {
                final IloNumVar[] numVars = new IloNumVar[vars.length];

                for (int i = 0; i < vars.length; i++) {
                    String varName = vars[i].getName();

                    numVars[i] = retrieveVar(varName);
                }

                IloAddable ret = solver.addSOS1(numVars, params, name);

                if (ret == null) {
                    throw new IloException("SOS1 constraint was not added to model correctly!");
                }
            } else {
                final double rhs = cstr.getRhs();
                IloLinearNumExpr lhs = solver.linearNumExpr();

                for (int i = 0; i < params.length; i++) {
                    IloNumVar var = retrieveVar(vars[i].getName());
                    lhs.addTerm(var, params[i]);
                }

                switch (type) {
                    case EQ:
                        solver.addEq(lhs, rhs, name);
                        break;
                    case GEQ:
                        solver.addGe(lhs, rhs, name);
                        break;
                    case LEQ:
                        solver.addLe(lhs, rhs, name);
                        break;
                    default:
                        throw new InternalError("Type of provided constraint is not 'EQ', 'GEQ', 'LEQ' nor 'SOS', which is not supported!");
                }
            }
        } catch (IloException ex) {
            ex.printStackTrace();
            throw new IllegalStateException("Could not add constraint to CPLEX model.");
        } catch (NullPointerException ex) {
            throw new ModelException("NullPointer in method 'addCstr()'!");
        }
    }

    @Override
    public void setObj(LinearObj obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Provided parameter 'obj' was invalid!");
        }

        checkIsDisposed();

        try {
            final IloLinearNumExpr expr = solver.linearNumExpr();
            final double[] params = obj.getParams();
            final Var[] vars = obj.getVars();
            final double constant = obj.getConstant();

            for (int i = 0; i < params.length; i++) {
                IloNumVar var = retrieveVar(vars[i].getName());
                expr.addTerm(params[i], var);
            }

            expr.add(solver.linearNumExpr(constant));

            // Check if another objective is already set
            removeObj();

            // Check if objective is to maximize or to minimize
            if (obj.isMax()) {
                solver.addMaximize(expr);
            } else {
                solver.addMinimize(expr);
            }

        } catch (IloException ex) {
            ex.printStackTrace();
            throw new IllegalStateException("Could not add objective to CPLEX model.");
        } catch (NullPointerException ex) {
            throw new ModelException("There was a NullPointerException thrown in the CPLEX backend!");
        }
    }

    @Override
    protected void removeObj() {
        if (solver.getObjective() != null) {
            try {
                solver.remove(solver.getObjective());
            } catch (IloException e) {
                throw new ModelException("Could not remove objective.");
            }
        }
    }

    @Override
    public void removeConstr(Constraint constraint) {
        if (constraint == null) {
            throw new IllegalArgumentException("Provided parameter 'constraint' was invalid!");
        }

        checkIsDisposed();

        // Check if constraint with given name is contained in model
        if (retrieveCstr(constraint.getName()) == null) {
            throw new ModelException("Model does not contain constraint with name " + constraint.getName());
        }

        try {
            Object ret = retrieveCstr(constraint.getName());

            if (ret instanceof IloAddable) {
                solver.remove((IloAddable) ret);
            } else {
                throw new IloException("Constraint is not of type 'IloAddable'!");
            }
        } catch (IloException e) {
            e.printStackTrace();
            throw new ModelException("Could not remove constraint with name " + constraint.getName());
        }
    }

    @Override
    public Result optimize() {
        checkIsDisposed();

        try {
            final long start = System.nanoTime();
            solver.solve();
            final long end = System.nanoTime();

            // Get status from solver
            Status iloStat = solver.getStatus();
            SolverStatus status;

            if (iloStat == Status.Optimal) {
                status = SolverStatus.OPTIMAL;
            } else if (iloStat == Status.Unbounded) {
                if (solver.isPrimalFeasible())
                    status = SolverStatus.UNBOUNDED;
                else
                    status = SolverStatus.INF_OR_UNBD;
            } else if (iloStat == Status.Infeasible) {
                status = SolverStatus.INFEASIBLE;
            } else if (iloStat == Status.InfeasibleOrUnbounded) {
                status = SolverStatus.INF_OR_UNBD;
            } else if (iloStat == Status.Unknown) {
                status = SolverStatus.TIME_OUT;
            } else {
                // feasible, error
                throw new RuntimeException("Unknown internal status.");
            }

            return new Result(status, start - end);
        } catch (IloException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error in internal Run ");
        }
    }

    @Override
    public void dispose() {
        solver.end();
        disposed = true;
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public double getVal(Var var) {
        checkIsDisposed();

        IloNumVar iloVar = retrieveVar(var.getName());
        try {
            return solver.getValue(iloVar);
        } catch (IloException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not add retrieve value for variable.");
        }
    }

    @Override
    public double getObjVal() {
        checkIsDisposed();

        IloObjective obj = solver.getObjective();
        try {
            IloNumExpr expr = obj.getExpr();
            return solver.getValue(expr);
        } catch (IloException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not add retrieve value for objective.");
        }
    }

    @Override
    public Object getUnderlyingModel() {
        checkIsDisposed();

        try {
            return solver.getModel();
        } catch (IloException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not add retrieve underlying model.");
        }
    }

    @Override
    public void addVar(String name, double lb, double ub, VarType type) {
        if (name == null || name.length() == 0) {
            throw new NullPointerException("Provided parameter 'name' was invalid!");
        }

        if (type == null) {
            throw new NullPointerException("Provided parameter 'type' was invalid!");
        }

        if (hasVar(name)) {
            throw new ModelException("Variable already contained in model!");
        }

        if (lb >= 0 && ub >= 0 && lb > ub) {
            throw new ModelException("Specified lower bound needs to be larger than upper bound.");
        }

        try {
            if (type == VarType.BIN) {
                solver.add(solver.boolVar(name));
            } else if (type == VarType.INT) {
                final double lbNew = (lb < 0) ? Integer.MIN_VALUE : lb;
                final double ubNew = (ub < 0) ? Integer.MAX_VALUE : ub;

                solver.add(solver.intVar((int) lbNew, (int) ubNew, name));
            } else if (type == VarType.DBL) {
                final double lbNew = (lb < 0) ? Double.MIN_VALUE : lb;
                final double ubNew = (ub < 0) ? Double.MAX_VALUE : ub;

                solver.add(solver.numVar(lbNew, ubNew, name));
            }
        } catch (IloException ex) {
            ex.printStackTrace();
            throw new IllegalStateException("Could not add variable name to CPLEX model.");
        }
    }

    @Override
    public int getNumVars() {
        checkIsDisposed();
        int counter = 0;

        // Get iterator for model (constraints)
        Iterator<?> cplexIt = solver.rangeIterator();

        // Iterate over constraints
        while (cplexIt.hasNext()) {
            IloRange range = (IloRange) cplexIt.next();
            IloLinearNumExprIterator linearIt;
            try {
                linearIt = ((IloLinearNumExpr) range.getExpr()).linearIterator();

                // Iterate over variables in constraint
                while (linearIt.hasNext()) {
                    // Get next numerical variable (so that iterator sets pointer to next object)
                    linearIt.nextNumVar();

                    // Increment counter
                    counter++;
                }
            } catch (IloException e) {
                e.printStackTrace();
                throw new IllegalStateException("This should not occur.");
            }
        }

        return counter;
    }

    @Override
    public int getNumConstrs() {
        int counter = 0;

        // Get iterator for model (constraints)
        Iterator<?> cplexIt = solver.rangeIterator();

        // Iterate over constraints
        while (cplexIt.hasNext()) {
            // Get next constraint (so that iterator sets pointer to next object)
            cplexIt.next();

            // Increment counter
            counter++;
        }

        return counter;
    }

    @Override
    public boolean hasVar(String varName) {
        if (varName == null || varName.length() == 0) {
            throw new IllegalArgumentException("Provided parameter 'varName' was invalid!");
        }

        checkIsDisposed();

        return !(null == retrieveVar(varName));
    }

    @Override
    public boolean hasConstraint(String cstrName) {
        if (cstrName == null || cstrName.length() == 0) {
            throw new IllegalArgumentException("Provided parameter 'cstrName' was invalid!");
        }

        checkIsDisposed();

        return !(null == retrieveCstr(cstrName));
    }


    /**
     * Checks if the state is disposed. Throws a model exception if state is disposed.
     */
    private void checkIsDisposed() {
        if (disposed) {
            throw new ModelException("Could not process requested operation. The Model was already disposed.");
        }
    }

    /**
     * Returns a constraint if present in model or null.
     *
     * @param cstrName String to search for.
     * @return constraint if name is contained in model or null
     */
    private Object retrieveCstr(final String cstrName) {
        checkIsDisposed();

        /*
         * Normal constraints:
         */
        // Get iterator for model (constraints)
        Iterator<?> cplexIt = solver.rangeIterator();

        // Iterate over constraints
        while (cplexIt.hasNext()) {
            IloRange range = (IloRange) cplexIt.next();

            // Check if name is equal
            if (cstrName.equals(range.getName())) {
                return range;
            }
        }

        /*
         * SOS1 constraints:
         */
        // Get iterator for model (sos1 constraints)
        Iterator<?> cplexItSos1 = solver.SOS1iterator();

        // Iterate over constraints
        while (cplexItSos1.hasNext()) {
            Object next = cplexItSos1.next();

            if (next instanceof IloSOS1) {
                IloSOS1 cnstr = (IloSOS1) next;
                // Check if name is equal
                if (cstrName.equals(cnstr.getName())) {
                    return cnstr;
                }
            }
        }

        /*
         * SOS2 constraints:
         */
        // Get iterator for model (sos2 constraints)
        Iterator<?> cplexItSos2 = solver.SOS2iterator();

        // Iterate over constraints
        while (cplexItSos2.hasNext()) {
            Object next = cplexItSos2.next();

            if (next instanceof IloSOS2) {
                IloSOS2 cnstr = (IloSOS2) next;
                // Check if name is equal
                if (cstrName.equals(cnstr.getName())) {
                    return cnstr;
                }
            }
        }

        // if nothing is found, return null
        return null;
    }

    /**
     * Returns a variable if present in model or null.
     *
     * @param varName String to search for.
     * @return variable if name is contained in model or null
     */
    private IloNumVar retrieveVar(final String varName) {
        checkIsDisposed();

        // Get iterator for model (constraints)
        Iterator<?> cplexIt = solver.iterator();

        // Iterate over constraints
        while (cplexIt.hasNext()) {
            Object next = cplexIt.next();

            if (next instanceof CpxNumVar) {
                CpxNumVar var = (CpxNumVar) next;
                if (var.getName().equals(varName)) {
                    return var;
                }
            }
        }

        // If nothing is found, return null
        return null;
    }


    /**
     * Sets logging up.
     *
     * @param isLogging True if logging to console should be enabled.
     * @throws IloException Throws an IloException if logging could not be activated.
     */
    private void setLogging(boolean isLogging) throws IloException {
        if (!isLogging) {
            // Disable logging
            solver.setOut(null);
        } else {
            // Activate logging to console
            solver.setOut(System.out);
        }
    }

    /*
     * SolverBuilder implementation
     */

    /**
     * Implementation of the interface SolverBuilder specified for CPLEX backend.
     *
     * @author Maximilian Kratz <maximilian.kratz@stud.tu-darmstadt.de>
     */
    public static class CplexSolverBuilder implements MILPSolverBuilder {

        private boolean logging;
        private boolean presolve;
        private MILPConstrGenerator gen;
        private long timeout = -1;
        private TimeUnit timeoutUnit = TimeUnit.SECONDS;
        private String libPath = null;
        private int seed = -1;

        public CplexSolverBuilder withLogging(boolean logging) {
            this.logging = logging;
            return this;
        }

        public CplexSolverBuilder withLibPath(String path) {
            this.libPath = path;
            return this;
        }

        public CplexSolverBuilder withTimeout(long timeout, TimeUnit timeoutUnit) {
            this.timeout = timeout;
            this.timeoutUnit = timeoutUnit;
            return this;
        }

        public CplexSolverBuilder withPresolve(boolean presolve) {
            this.presolve = presolve;
            return this;
        }

        public CplexSolverBuilder withMILPConstrGenerator(MILPConstrGenerator gen) {
            this.gen = gen;
            return this;
        }

        public CplexSolverBuilder withSeed(int seed) {
            this.seed = seed;
            return this;
        }

        @Override
        public CplexSolver build() {
            return new CplexSolver(gen, logging, presolve, timeout, timeoutUnit, seed, libPath);
        }
    }
}
