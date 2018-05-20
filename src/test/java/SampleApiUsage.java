import org.cardygan.ilp.api.*;
import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.solver.CplexSolver;
import org.junit.Test;

import static org.cardygan.ilp.api.util.ExprDsl.*;

/**
 * Created by markus on 18.02.17.
 */
public class SampleApiUsage {

    @Test
    public void simpleModel1() {
        Model model = new Model();

        BinaryVar v1 = model.newBinaryVar("v1");
        BinaryVar v2 = model.newBinaryVar("v2");
        model.newConstraint("name", leq(sum(v1, v2), sum(v1, param(2))));

        BinaryVar v3 = model.newBinaryVar();
        BinaryVar v4 = model.newBinaryVar();
        model.newConstraint("name2", and(leq(sum(v1, v2), sum(v1, param(2))), geq(v3, v4)));

        model.newObjective(false, sum(v1));

        Result res = model.solve(new CplexSolver());

    }

    @Test
    public void simpleModel2() {
        Model model = new Model();

        BinaryVar v1 = model.newBinaryVar("var1");
        BinaryVar v2 = model.newBinaryVar("var2");
        model.newConstraint("name2", and(v1, v2));

        model.newObjective(false, v1);

        Result res = model.solve(new CplexSolver());

    }
}
