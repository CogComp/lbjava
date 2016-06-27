package edu.illinois.cs.cogcomp.infer.ilp;

import gurobi.*;
import org.junit.Test;

import static org.junit.Assert.*;


public class GurobiHookTest {

    @Test
    public void testGurobi() throws GRBException {
        GRBEnv env = new GRBEnv();
        GRBModel model = new GRBModel(env);

        // Create variables
        GRBVar x = model.addVar(0.0, 1.0, -1.0, GRB.BINARY, "x");
        GRBVar y = model.addVar(0.0, 1.0, -1.0, GRB.BINARY, "y");
        GRBVar z = model.addVar(0.0, 1.0, -2.0, GRB.BINARY, "z");

        // Integrate new variables

        model.update();

        // Add constraint: x + 2 y + 3 z <= 4
        GRBLinExpr expr = new GRBLinExpr();
        expr.addTerm(1.0, x);
        expr.addTerm(2.0, y);
        expr.addTerm(3, z);
        model.addConstr(expr, GRB.LESS_EQUAL, 4.0, "c0");

        // Add constraint: x + y >= 1
        expr = new GRBLinExpr();
        expr.addTerm(1.0, x);
        expr.addTerm(1.0, y);
        model.addConstr(expr, GRB.GREATER_EQUAL, 1.0, "c1");

        // Optimize model
        model.optimize();

        assertEquals("x", x.get(GRB.StringAttr.VarName));
        assertEquals(1.0, x.get(GRB.DoubleAttr.X), 0.0);
        assertEquals(0.0, y.get(GRB.DoubleAttr.X), 0.0);
        assertEquals(1.0, z.get(GRB.DoubleAttr.X), 0.0);
        assertEquals(-3.0, model.get(GRB.DoubleAttr.ObjVal), 0.0);
    }
}