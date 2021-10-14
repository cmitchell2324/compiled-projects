package lvc.cds;

import java.util.concurrent.atomic.AtomicInteger;

public class Future {
    private static int rootId = 0;
    private int id;
    private static final boolean DEBUG = false;
    private static final int NEW = 0;
    private static final int COMPLETED = 1;
    private Integer result;

    private AtomicInteger state;
    // the active object that our task is currently scheduled on
    private ActiveObject ao;
    private Scheduler scheduler;

    /**
     * Simply a constructor for a Future that will pass in the ActiveObject to assign the Future to. 
     */
    public Future(ActiveObject ao) {
        this.id = rootId++;
        this.ao = ao;
        this.scheduler = ao.getScheduler();
        this.state = new AtomicInteger(NEW);
        this.result = null;
    }

    /**
     * Implemented quite some time ago, but utilized the AtomicInteger and compares it to the COMPLETED integer. If they are the same, then the Future is completed.
     */
    public boolean isComplete() {
        return state.get() == COMPLETED;
    }

    /**
     * Similar to complete method from our previous code, but we no longer need a continuation. 
     */
    void complete() {
        diag("complete", "");
        if(state.compareAndSet(NEW, COMPLETED)) {
            diag("complete", "Transition from NEW to COMPLETED");
        } else {
            throw new IllegalStateException("transition from NEW to COMPLETED");
        }
    }

    /**
     * Implemented in class. 
     */
    void join() {
        diag("join", "");
        var th = Thread.currentThread();

        if(!(th instanceof ActiveObject)) {
            throw new IllegalStateException("Can only join from worker thread");
        }

        if(state.get() < COMPLETED) {
            diag("join", "waiting in a scheduler thread");
            scheduler.workUntilCompleted(th, this);
        }
    }

     // diagnostics -- a cheezy logging tool. Change DEBUG to true to enable output
     private void diag(String mName, String msg) {
        if (DEBUG) {
            System.out.println(id + ": future::" + mName + ", state=" + state.get());

            if (msg != null && !msg.equals("")) {
                System.out.println("\t\t-->" + msg);
            }
        }
    }
}
