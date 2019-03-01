import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.internal.util.IlpUtil;
import org.junit.Test;

import static org.cardygan.ilp.api.util.ExprDsl.*;

import static org.cardygan.ilp.api.util.ExprDsl.and;

public class IlpUtilTest {

    @Test
    public void testAnd() {
        Model model = new Model();
        BinaryVar v1 = model.newBinaryVar();
        BinaryVar v2 = model.newBinaryVar();

        model.newConstraint("cstr1", and(v1, v2));

        test(model);
    }

    private void test(Model model) {
//        String json = IlpUtil.persistToJson(model);
//
//        System.out.println(json);
//
//        Model readModel = IlpUtil.readFromJson(json);

    }

}
