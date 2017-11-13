package org.cardygan.ilp.api;

import org.cardygan.ilp.api.expr.Var;
import org.cardygan.ilp.internal.Coefficient;
import org.cardygan.ilp.internal.expr.EqNormalizedArithExpr;
import org.cardygan.ilp.internal.expr.GeqNormalizedArithExpr;
import org.cardygan.ilp.internal.expr.LeqNormalizedArithExpr;
import org.cardygan.ilp.internal.expr.NormalizedArithExpr;
import org.cardygan.ilp.internal.util.IlpUtil;
import org.gnu.glpk.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class GlpkSolver implements Solver {

    private static final String LOG_FILE = "log-" + System.currentTimeMillis() + ".log";
    private static final String LOG_PATH = "." + File.separator + "log" + File.separator + "glpk" + File.separator;
    private ModelContext model;
    private Map<Var, Integer> vars;
    private Map<Var, Double> solutions;

    @Override
    public Result solve(ModelContext ilpModel) {
        this.model = ilpModel;
        vars = new HashMap<>();

        glp_iocp iocp;

        try {
//            GLPK.glp_free_env();

            glp_prob model = GLPK.glp_create_prob();
            GLPK.glp_set_prob_name(model, "model");

            GLPK.glp_add_cols(model, this.model.getVars().size());

            int variableCount = 1;

            for (Var var : this.model.getVars()) {
                GLPK.glp_set_col_name(model, variableCount, var.getName());

                if (IlpUtil.isBinaryVar(var)) {
                    GLPK.glp_set_col_kind(model, variableCount, GLPKConstants.GLP_BV);
                    GLPK.glp_set_col_bnds(model, variableCount, GLPKConstants.GLP_DB, 0, 1);
                } else if (IlpUtil.isIntVar(var)) {
                    GLPK.glp_set_col_kind(model, variableCount, GLPKConstants.GLP_IV);
                    GLPK.glp_set_col_bnds(model, variableCount, GLPKConstants.GLP_FR, 0, 0);
                } else {
                    throw new IllegalStateException("Not supported variable type.");
                }
                vars.put(var, variableCount);
                variableCount++;
            }

            // Create constraints
            constructConstraints(model, this.model.getNormalizedConstraints());

            GLPK.glp_set_obj_name(model, "objective");
            if (this.model.getObjective().isMax()) {
                GLPK.glp_set_obj_dir(model, GLPKConstants.GLP_MAX);
            } else {
                GLPK.glp_set_obj_dir(model, GLPKConstants.GLP_MIN);
            }

            createObjectiveTerms(model, this.model.getObjective());

            iocp = new glp_iocp();
            GLPK.glp_init_iocp(iocp);
            iocp.setPresolve(GLPK.GLP_ON);

            // if (!loggingOn) {
            // GLPK.glp_print_sol(model, "");
            // }
            // else {
            // final File log = new File(LOG_PATH);
            // if (log.exists() || log.mkdirs()) {
            // GLPK.glp_print_sol(model, LOG_PATH + LOG_FILE);
            // }
            // }
            // System.out.println(LOG_PATH);
            // final File log = new File(LOG_PATH);
            // if (log.exists() || log.mkdirs()) {
            // GLPK.glp_print_sol(model, LOG_PATH + LOG_FILE);
            //
            // }

            final long start = System.currentTimeMillis();
            int ret = GLPK.glp_intopt(model, iocp);
            final long end = System.currentTimeMillis();

            final boolean isUnbounded = GLPK.glp_get_status(model) == GLPK.GLP_UNBND;
            final boolean isFeasible = ret == 0;

            solutions = new HashMap<>();
            for (Entry<Var, Integer> entry : vars.entrySet()) {
                solutions.put(entry.getKey(), GLPK.glp_mip_col_val(model, entry.getValue()));
            }

            final Optional<Double> objectiveVal = (isFeasible) ? Optional.of(GLPK.glp_mip_obj_val(model))
                    : Optional.empty();

            Result res = new Result(ilpModel.getModel(), new Result.Statistics(isFeasible, isUnbounded, end - start), solutions, objectiveVal);

            GLPK.glp_delete_prob(model);
//            GLPK.glp_free_env();
            return res;
        } catch (final GlpkException ex) {
            ex.printStackTrace();
//            GLPK.glp_free_env();
//            System.gc();

        }
        throw new RuntimeException("Error in solver Run ");
    }


    private void createObjectiveTerms(glp_prob model, Objective obj) {
        GLPK.glp_set_obj_coef(model, 0, obj.getConstant());

        for (Coefficient coef : obj.getCoefficients()) {
            double paramVal = coef.getParam().getVal();
            GLPK.glp_set_obj_coef(model, vars.get(coef.getVar()), paramVal);
        }
    }

    private void constructConstraints(glp_prob model, List<NormalizedArithExpr> ilpNormalizedArithExprs) {
        int constraintRowCounter = 1;

        GLPK.glp_add_rows(model, ilpNormalizedArithExprs.size());

        for (final NormalizedArithExpr ilpNormalizedArithExpr : ilpNormalizedArithExprs) {


//            try {
            final SWIGTYPE_p_int ind = GLPK.new_intArray(ilpNormalizedArithExpr.getCoefficients().size());
            final SWIGTYPE_p_double val = GLPK.new_doubleArray(ilpNormalizedArithExpr.getCoefficients().size());

            GLPK.glp_set_row_name(model, constraintRowCounter, "cstr_" + constraintRowCounter);

            for (Coefficient coef : ilpNormalizedArithExpr.getCoefficients()) {
                final int variable = vars.get(coef.getVar());

                int index = ilpNormalizedArithExpr.getCoefficients().indexOf(coef);

//                System.out.println("VARIABLE: " + variable + " VAL: " + coef.getParam().getVal());
                GLPK.intArray_setitem(ind, index + 1, variable);
                GLPK.doubleArray_setitem(val, index + 1, coef.getParam().getVal());
            }

            double right = ilpNormalizedArithExpr.getRhs().getVal();

            if (ilpNormalizedArithExpr instanceof LeqNormalizedArithExpr) {
                GLPK.glp_set_row_bnds(model, constraintRowCounter, GLPKConstants.GLP_UP, 0, right);
            }
            if (ilpNormalizedArithExpr instanceof GeqNormalizedArithExpr) {
                GLPK.glp_set_row_bnds(model, constraintRowCounter, GLPKConstants.GLP_LO, right, 0);
            }
            if (ilpNormalizedArithExpr instanceof EqNormalizedArithExpr) {
                GLPK.glp_set_row_bnds(model, constraintRowCounter, GLPKConstants.GLP_FX, right, right);
            }

            GLPK.glp_set_mat_row(model, constraintRowCounter, ilpNormalizedArithExpr.getCoefficients().size(), ind, val);
            constraintRowCounter++;

//            GLPK.delete_intArray(ind);
//            GLPK.delete_doubleArray(val);

        }

    }

}
