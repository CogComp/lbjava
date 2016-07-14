/**
 * 
 */
package edu.illinois.cs.cogcomp.lbjava.neuralnet;

import edu.illinois.cs.cogcomp.lbjava.neuralnet.ThreadedNNTrainer.Range;

/**
 * Threads will operate on a range, this superclass contains that
 * range and handles atomic synchronized access.
 * @author redman
 */
public class PushThread extends Thread {

    /** the range to operate on. */
    protected Range range = null;
    
    /** set when this thread is waiting for input. */
    private boolean idle = false;
    /**
     * the push thread takes the name ofthe thread, to pass to 
     * the super.
     * @param name the name of the thread.
     */
    PushThread(String name) {
        super(name);
    }
    
    /**
     * set the range of things to operate on.
     * @param range
     */
    synchronized void setRange(Range range) {
        this.range = range;
        this.notifyAll();
    }
    
    /**
     * call this when we are done.
     */
    synchronized void done() {
        this.range = null;
        this.interrupt();
    }
    
    /**
     * wait for the thread to complete it's run, it will set
     * poised and block till it gets data.
     */
    final synchronized public void waitIdle() {
        while(!idle || range != null)
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
    }
    
    /**
     * wait for the next range. 
     * @return the range.
     */
    final synchronized protected Range getRange() {
        while (range == null)
            try {
                this.idle = true;
                this.notify(); // somebody waiting for completion?
                this.wait();
            } catch (InterruptedException e) {
                if (this.isInterrupted()) {
                    System.out.println("Interrupted error.");
                    return null;
                }
            }
        Range r = range;
        range = null;
        this.idle = false;
        return r;
    }

}
