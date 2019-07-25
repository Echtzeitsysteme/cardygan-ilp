package org.cardygan.ilp.internal.expr.cnf;

import org.cardygan.ilp.api.model.BinaryVar;
import org.cardygan.ilp.api.model.Model;
import org.cardygan.ilp.api.model.bool.BoolExpr;
import org.cardygan.ilp.api.model.bool.RelOp;

import java.util.List;
import java.util.Map;

public interface CnfTransformer {

    void computeCnf(BoolExpr expr, Model model);

    Map<BinaryVar, RelOp> getMapping();

    List<CnfClause> getClauses();

}
