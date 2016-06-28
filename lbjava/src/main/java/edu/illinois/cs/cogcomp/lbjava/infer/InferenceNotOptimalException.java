/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.infer;


import edu.illinois.cs.cogcomp.infer.ilp.ILPSolver;

/**
 * Exceptions of this type are thrown by the {@link ILPInference} class when the selected
 * {@link ILPSolver} did not successfully find the optimal solution to the inference problem.
 * Instances of this class contain a reference to the {@link ILPSolver} instance so that the user
 * can, for instance, call the {@link ILPSolver#write(java.lang.StringBuffer)} method.
 *
 * @author Nick Rizzolo
 **/
public class InferenceNotOptimalException extends Exception {
    /** The ILP algorithm and problem representation that failed. */
    private ILPSolver solver;
    /** The head object of the inference problem. */
    private Object head;


    /**
     * Initializing constructor.
     *
     * @param solver The ILP algorithm and problem representation that failed.
     * @param head The head object of the inference problem.
     **/
    public InferenceNotOptimalException(ILPSolver solver, Object head) {
        super("Failed to solve inference problem with the following head object: " + head);
        this.solver = solver;
        this.head = head;
    }


    /** Retrieves the ILP problem instance, {@link #solver}. */
    public ILPSolver getSolver() {
        return solver;
    }


    /** Retrieves the head object, {@link #head}. */
    public Object getHead() {
        return head;
    }
}
