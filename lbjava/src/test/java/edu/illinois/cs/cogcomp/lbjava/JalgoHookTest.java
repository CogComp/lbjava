package edu.illinois.cs.cogcomp.lbjava;

import edu.illinois.cs.cogcomp.lbjava.infer.OJalgoHook;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JalgoHookTest {
    @Test
    public void testProgram1() throws Exception {
        OJalgoHook ojaHook = new OJalgoHook();
        int[] varInds = new int[2];

        int i = 0;
        while (i< 2) {
            int x = ojaHook.addBooleanVariable(-1.0);
            varInds[i] = x;
            i++;
        }

        double[] coefs = { 1, 2 };
        ojaHook.addGreaterThanConstraint(varInds, coefs, -3);
        ojaHook.addLessThanConstraint(varInds, coefs, 4);

        ojaHook.setMaximize(false);

        try {
            ojaHook.solve();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(ojaHook.objectiveValue() == -2.0);
        assertTrue(ojaHook.getBooleanValue(0));
        assertTrue(ojaHook.getBooleanValue(1));
    }

    @Test
    public void testProgram2() throws Exception {
        OJalgoHook ojaHook = new OJalgoHook();
        int[] varInds = new int[2];

        int i = 0;
        while (i< 2) {
            int x = ojaHook.addBooleanVariable(-1.0);
            varInds[i] = x;
            i++;
        }

        double[] coefs = { 1, 2 };
        ojaHook.addGreaterThanConstraint(varInds, coefs, -3);
        ojaHook.addLessThanConstraint(varInds, coefs, 4);

        ojaHook.setMaximize(true);

        try {
            ojaHook.solve();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ojaHook.printModelInfo();

        assertTrue(ojaHook.objectiveValue() == 0); // fails
        assertTrue(!ojaHook.getBooleanValue(0)); // fails
        assertTrue(!ojaHook.getBooleanValue(1)); // fails
    }


    @Test
    public void testProgram3() throws Exception {
        OJalgoHook ojaHook = new OJalgoHook();
        int[] varInds = new int[2];

        int i = 0;
        while (i< 2) {
            int x = ojaHook.addBooleanVariable(1.5);
            varInds[i] = x;
            i++;
        }

        double[] coefs = { 1, 2 };
        ojaHook.addGreaterThanConstraint(varInds, coefs, -3);
        ojaHook.addLessThanConstraint(varInds, coefs, 4);

        ojaHook.setMaximize(true);

        try {
            ojaHook.solve();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ojaHook.printModelInfo();

        assertTrue(ojaHook.objectiveValue() == 3); // fails
        assertTrue(ojaHook.getBooleanValue(0)); // fails
        assertTrue(ojaHook.getBooleanValue(1)); // fails
    }

    @Test
    public void testProgram4() throws Exception {
        OJalgoHook ojaHook = new OJalgoHook();
        int[] varInds = new int[2];

        int i = 0;
        while (i< 2) {
            int x = ojaHook.addBooleanVariable(1.5);
            varInds[i] = x;
            i++;
        }

        double[] coefs = { 1, 2 };
        ojaHook.addGreaterThanConstraint(varInds, coefs, -3);
        ojaHook.addLessThanConstraint(varInds, coefs, 4);

        ojaHook.setMaximize(false);

        try {
            ojaHook.solve();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ojaHook.printModelInfo();

        assertTrue(ojaHook.objectiveValue() == 0); // fails
        assertTrue(!ojaHook.getBooleanValue(0)); // fails
        assertTrue(!ojaHook.getBooleanValue(1)); // fails
    }

    @Test
    public void testProgram5() throws Exception {
        OJalgoHook ojaHook = new OJalgoHook();
        int[] varInds = new int[2];

        int i = 0;
        while (i< 2) {
            int x = ojaHook.addBooleanVariable(1.5);
            varInds[i] = x;
            i++;
        }

        double[] coefs = { 1, 2 };
        ojaHook.addGreaterThanConstraint(varInds, coefs, 1);
        ojaHook.addLessThanConstraint(varInds, coefs, 4);

        ojaHook.setMaximize(true);

        try {
            ojaHook.solve();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ojaHook.printModelInfo();

        assertTrue(ojaHook.objectiveValue() == 3);
        assertTrue(ojaHook.getBooleanValue(0));
        assertTrue(ojaHook.getBooleanValue(1));
    }

    @Test
    public void testProgram6() throws Exception {
        OJalgoHook ojaHook = new OJalgoHook();
        int[] varInds = new int[2];

        int i = 0;
        while (i< 2) {
            int x = ojaHook.addBooleanVariable(1.5);
            varInds[i] = x;
            i++;
        }

        double[] coefs = { 1, 2 };
        ojaHook.addGreaterThanConstraint(varInds, coefs, 1);
        ojaHook.addLessThanConstraint(varInds, coefs, 2);

        ojaHook.setMaximize(false);

        try {
            ojaHook.solve();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ojaHook.printModelInfo();

        assertTrue(ojaHook.objectiveValue() == 1); // fails: should be 1
        assertTrue(ojaHook.getBooleanValue(0)); // fails; should be true
        assertTrue(!ojaHook.getBooleanValue(1)); // fails; should be false
    }

    @Test
    public void testProgram7() throws Exception {
        OJalgoHook ojaHook = new OJalgoHook();
        int[] varInds = new int[2];

        int i = 0;
        while (i< 2) {
            int x = ojaHook.addBooleanVariable(1.5);
            varInds[i] = x;
            i++;
        }

        double[] coefs = { 1, 2 };
        ojaHook.addGreaterThanConstraint(varInds, coefs, 1);
        ojaHook.addLessThanConstraint(varInds, coefs, 2);

        ojaHook.setMaximize(true);

        try {
            ojaHook.solve();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ojaHook.printModelInfo();

        assertTrue(ojaHook.objectiveValue() == 2); // fails
        assertTrue(!ojaHook.getBooleanValue(0)); // fails
        assertTrue(ojaHook.getBooleanValue(1)); // fails
    }
}
