package org.cardygan.ilp.internal.solver.milp;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.cardygan.ilp.api.Result;
import org.cardygan.ilp.api.Result.SolverStatus;
import org.cardygan.ilp.api.model.Constraint;
import org.cardygan.ilp.api.model.Var;
import org.cardygan.ilp.internal.solver.milp.LinearConstr.Type;
import org.cardygan.ilp.internal.util.LibraryUtil;
import org.cardygan.ilp.internal.util.ModelException;

import gurobi.GRB;
import ilog.concert.IloAddable;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloLinearNumExprIterator;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.concert.IloSOS1;
import ilog.concert.IloSOS2;
import ilog.cplex.CpxNumVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;

/**
 * This class represents the implementation of the interface MILPSolver for IBMs CPLEX solver.
 *
 * @author Maximilian Kratz <maximilian.kratz@stud.tu-darmstadt.de>
 */
public class CplexSolver extends MILPSolver {
    /*
     * Variables
     */
    private IloCplex solver;
    private boolean disposed;

    /**
     * Constructor for initializing an object.
     */
    public CplexSolver() {
        try {
            this.init();
        } catch (IloException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not initialize CPLEX backend.");
        }
    }

	/**
	 * Constructor for initializing an object.
	 *
	 * @param gen A MILPConstrGenerator that gets set up in super constructor.
	 * @param logging True if logging to console should be enabled.
	 * @param presolve True if presolving should be enabled.
	 * @param timeout Time limit in timeoutUnit.
	 * @param timeoutUnit Time unit for time out.
	 * @param seed Random seed for solver.
	 * @param libPath Path to CPLEX library (as String).
	 */
    public CplexSolver(MILPConstrGenerator gen, boolean logging, boolean presolve, long timeout, TimeUnit timeoutUnit,
                       int seed, String libPath) {
        super(gen);
        init(presolve, logging, timeout, timeoutUnit, seed, libPath);
    }

    @Override
    public void addCstr(String name, LinearConstr cstr) {
        if (name == null || name.length() == 0) {
            throw new NullPointerException("Provided parameter 'name' was invalid!");
        }

        if (cstr == null) {
            throw new NullPointerException("Provided parameter 'cstr' was invalid!");
        }

        if (this.hasConstraint(name)) {
            throw new ModelException("Constraint with given name already exists in model!");
        }

        /*
         * Get parameters from constraint
         */
        final LinearConstr.Type type = cstr.getType();
        final double[] params = cstr.getParams();
        final Var[] vars = cstr.getVars();

        try {
            // If type is SOS (special one!)
            if (type == Type.SOS) {
                final IloNumVar[] numVars = new IloNumVar[vars.length];

                for (int i = 0; i < vars.length; i++) {
                    String varName = vars[i].getName();

                    numVars[i] = this.retrieveVar(varName);
                }

                IloAddable ret = this.solver.addSOS1(numVars, params, name);

                if (ret == null) {
                    throw new IloException("SOS1 constraint was not added to model correctly!");
                }
            } else {
                final double rhs = cstr.getRhs();
                IloLinearNumExpr lhs = this.solver.linearNumExpr();

                for (int i = 0; i < params.length; i++) {
                    IloNumVar var = this.retrieveVar(vars[i].getName());
                    lhs.addTerm(var, params[i]);
                }

                switch (type) {
                    case EQ:
                        this.solver.addEq(lhs, rhs, name);
                        break;
                    case GEQ:
                        this.solver.addGe(lhs, rhs, name);
                        break;
                    case LEQ:
                        this.solver.addLe(lhs, rhs, name);
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
    	if(obj == null) {
    		throw new IllegalArgumentException("Provided parameter 'obj' was invalid!");
    	}
    	
    	checkIsDisposed();
    	
    	try {
	    	final IloLinearNumExpr expr = this.solver.linearNumExpr();
	    	final double[] params = obj.getParams();
	    	final Var[] vars = obj.getVars();
	    	final double constant = obj.getConstant();
	    	
	    	for(int i = 0; i < params.length; i++) {
	    		IloNumVar var = this.retrieveVar(vars[i].getName());
	    		expr.addTerm(params[i], var);
	    	}
	    	
	    	expr.add(this.solver.linearNumExpr(constant));
	    	
	    	// Check if another objective is already set
	    	if(solver.getObjective() != null) {
	    		// Remove it before setting new one
	    		solver.remove(solver.getObjective());
	    	}
	    	
	    	// Check if objective is to maximize or to minimize
	    	if(obj.isMax()) {
	    		this.solver.addMaximize(expr);
	    	} else {
	    		this.solver.addMinimize(expr);
	    	}
	    	
    	} catch (IloException ex) {
    		ex.printStackTrace();
            throw new IllegalStateException("Could not add objective to CPLEX model.");
    	} catch (NullPointerException ex) {
    		throw new ModelException("There was a NullPointerException thrown in the CPLEX backend!");
    	}
    }

    @Override
    public void removeConstr(Constraint constraint) {
        if (constraint == null) {
            throw new IllegalArgumentException("Provided parameter 'constraint' was invalid!");
        }

        checkIsDisposed();

        // Check if constraint with given name is contained in model
        if (this.retrieveCstr(constraint.getName()) == null) {
            throw new ModelException("Model does not contain constraint with name " + constraint.getName());
        }

        try {
            Object ret = this.retrieveCstr(constraint.getName());

            if (ret instanceof IloAddable) {
                this.solver.remove((IloAddable) ret);
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
            solver.exportModel("/home/maxkratz/Schreibtisch/test_cplex.lp");
        } catch (IloException e) {
            e.printStackTrace();
        }

        try {
            final long start = System.nanoTime();
            this.solver.solve();
            final long end = System.nanoTime();

            // Get status from solver
            Status iloStat = this.solver.getStatus();
            SolverStatus status;

            /*
             * UNBOUNDED
             * INF_OR_UNBD
             * INFEASIBLE
             * OPTIMAL
             * TIME_OUT
             */

            if (iloStat == Status.Optimal) {
                status = SolverStatus.OPTIMAL;
            } else if (iloStat == Status.Unbounded) {
                status = SolverStatus.UNBOUNDED;
            } else if (iloStat == Status.Infeasible) {
                status = SolverStatus.INFEASIBLE;
            } else if (iloStat == Status.InfeasibleOrUnbounded) {
                status = SolverStatus.INF_OR_UNBD;
            } else {
                throw new RuntimeException("Unknown internal status.");
            }

            /*
             * TODO:
             * There are more than these four states possible!
             */

            return new Result(status, start - end);
        } catch (IloException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error in internal Run ");
        }
    }

    @Override
    public void dispose() {
        this.solver.end();
        this.disposed = true;
    }

    @Override
    public boolean isDisposed() {
        return this.disposed;
    }

    @Override
    public double getVal(Var var) {
        checkIsDisposed();

        IloNumVar iloVar = this.retrieveVar(var.getName());
        try {
            return this.solver.getValue(iloVar);
        } catch (IloException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not add retrieve value for variable.");
        }
    }

    @Override
    public double getObjVal() {
        checkIsDisposed();

        IloObjective obj = this.solver.getObjective();
        try {
            IloNumExpr expr = obj.getExpr();
            return this.solver.getValue(expr);
        } catch (IloException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not add retrieve value for objective.");
        }
    }

    @Override
    public Object getUnderlyingModel() {
        checkIsDisposed();

        try {
            return this.solver.getModel();
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
        
        if (this.hasVar(name)) {
        	throw new ModelException("Variable already contained in model!");
        }
        
        try {
	        if(type == VarType.BIN) {
	        	solver.add(solver.boolVar(name));
	        } else if(type == VarType.INT) {
	        	if(lb > ub) {
	        		// TODO
	        		throw new ModelException("");
	        	}
	        	
	            final double lbNew = (lb < 0) ? Integer.MIN_VALUE : lb;
	            final double ubNew = (ub < 0) ? Integer.MAX_VALUE : ub;
	        	
	        	solver.add(solver.intVar((int) lbNew, (int) ubNew, name));
	        } else if(type == VarType.DBL) {
	        	if(lb > ub) {
	        		// TODO
	        		throw new ModelException("");
	        	}
	        	
	            final double lbNew = (lb < 0) ? Double.MIN_VALUE : lb;
	            final double ubNew = (ub < 0) ? Double.MAX_VALUE : ub;
	            
	        	solver.add(solver.numVar(lbNew, ubNew, name));
	        }
        } catch (IloException ex) {
        	// TODO
        	ex.printStackTrace();
        }
    }

    @Override
    public int getNumVars() {
        checkIsDisposed();
        int counter = 0;

        // Get iterator for model (constraints)
        Iterator<?> cplexIt = this.solver.rangeIterator();

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
        Iterator<?> cplexIt = this.solver.rangeIterator();

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

        return !(null == this.retrieveVar(varName));
    }

    @Override
    public boolean hasConstraint(String cstrName) {
        if (cstrName == null || cstrName.length() == 0) {
            throw new IllegalArgumentException("Provided parameter 'cstrName' was invalid!");
        }

        checkIsDisposed();

        return !(null == this.retrieveCstr(cstrName));
    }

    /*
     * Utility methods
     */

    /**
     * Initializes all variables.
     *
     * @throws IloException If something goes wrong.
     */
    private void init() throws IloException {
        this.solver = new IloCplex();
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
        Iterator<?> cplexIt = this.solver.rangeIterator();

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
        Iterator<?> cplexItSos1 = this.solver.SOS1iterator();

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
        Iterator<?> cplexItSos2 = this.solver.SOS2iterator();

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
        Iterator<?> cplexIt = this.solver.iterator();

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
            this.solver = new IloCplex();

            // Setup logging
            this.setLogging(logging);

            // Timeout
            if (timeout != 0)
                // Set parameter for timeout in seconds
                this.solver.setParam(IloCplex.DoubleParam.TiLim, timeoutUnit.toSeconds(timeout));


            // Random Seed
            if (seed != -1)
                this.solver.setParam(IloCplex.IntParam.RandomSeed, seed);


            // Presolve switch
            if (!presolve) {
                this.solver.setParam(IloCplex.BooleanParam.PreInd, false);
            }
        } catch (IloException ex) {
            ex.printStackTrace();
            throw new IllegalStateException("Could not initialize CPLEX backend.");
        }
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
            this.solver.setOut(null);
        } else {
            // Activate logging to console
            this.solver.setOut(System.out);
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
    public static class CplexSolverBuilder implements SolverBuilder {

        private boolean logging;
        private boolean presolve;
        private MILPConstrGenerator gen;
        private long timeout = 0;
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
