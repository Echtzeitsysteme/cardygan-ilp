import org.cardygan.ilp.api.*;
import org.cardygan.ilp.api.BinaryVar;
import org.junit.Test;

import static org.cardygan.ilp.api.util.ExprDsl.*;

/**
 * Created by markus on 18.02.17.
 */
public class SampleApiUsage {

    @Test
    public void simpleModel1() {
        Model model = new Model();

        Constraint cstr1 = model.newConstraint("name");
        BinaryVar v1 = model.newBinaryVar("v1");
        BinaryVar v2 = model.newBinaryVar("v2");
        cstr1.setExpr(leq(sum(v1, v2), sum(v1, param(2))));

        Constraint cstr2 = model.newConstraint("name2");
        BinaryVar v3 = model.newBinaryVar();
        BinaryVar v4 = model.newBinaryVar();
        cstr2.setExpr(and(leq(sum(v1, v2), sum(v1, param(2))), geq(v3, v4)));

        Objective obj = model.newObjective(false);
        obj.setTerm(sum(v1));

        Result res = model.solve(new CplexSolver());

    }

    @Test
    public void simpleModel2() {
        Model model = new Model();

        Constraint cstr = model.newConstraint("name2");
        BinaryVar v1 = model.newBinaryVar("var1");
        BinaryVar v2 = model.newBinaryVar("var2");
        cstr.setExpr(and(v1, v2));

        Objective obj = model.newObjective(false);
        obj.setTerm(v1);

        Result res = model.solve(new CplexSolver());

    }
}
