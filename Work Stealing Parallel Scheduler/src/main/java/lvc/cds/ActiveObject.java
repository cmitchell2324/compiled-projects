package lvc.cds;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ActiveObject extends Thread {
    
    private Scheduler scheduler;
    private ConcurrentLinkedDeque<AOTask> jobs;
    private int stealingActiveObjectNumber;
    private Random random = new Random();

    private static class AOTask {
        Task task;
        Future future;

        // Pushes an empty AOTask onto the jobs queue to signify termination.  Acts as a poison pill.
        AOTask() {
            this.task = null;
            this.future = null;
        }

        // Creates a new AOTask with the provided Task and Future
        AOTask(Task task, Future future) {
            this.task = task;
            this.future = future;
        }

        // Runs the AOTask
        public void runAndComplete() {
            assert task != null;
            task.compute();
            if(future != null) {
                future.complete();
            }
        }
    }

    /**
     * Constructor that initializes a new ActiveObject with provided scheduler and the worker that is doing the stealing. stealingActiveObjectNumber is simply a number that is assigned
     * to each ActiveObject to make it unique.
     */
    public ActiveObject(Scheduler s, int stealingActiveObjectNumber) {
        this.scheduler = s;
        jobs = new ConcurrentLinkedDeque<>();
        this.stealingActiveObjectNumber = stealingActiveObjectNumber;
    }

    /**
     * enqueue a task. This can execute any code
     */

   public Future enqueue(Task task) {
        Future future = new Future(this);
        task.setFuture(future);
        jobs.push(new AOTask(task, future));
        return future;
    }

    /**
     * Simply pushes an empty AOTask onto the queue. Acts as a "poison pill"
     */
    public void terminate() {
        jobs.push(new AOTask());
    }


    /**
     * A run method for the current ActiveObject which manages stealing logic. Initially polls a job and checks to see if it is null.
     * If it is null, then it will attempt to steal work from another ActiveObject. If it is unable to steal work, it will go to sleep
     * for some time (in this case, it will sleep for 5 ms plus a random integer on the bound of 4 ms). So, the current worker could be sleeping
     * for 5, 6, 7, 8, or 9 milliseconds.
     */
    public void run() {
        while (true) {
            AOTask r = jobs.pollFirst();
            if(r == null) {
                r = stealingWork();
            }

            if(r == null) {
                try {
                    Thread.sleep(5 + random.nextInt(4));
                } catch (InterruptedException e) {}
            }

            if(r != null) {
                r.runAndComplete();
            }
        }
    }

    /**
     * No get method built into ConcurrentLinkedDeque class, so this method will simply return the worker queue for the active worker. This becomes useful in the stealingWork() method,
     * wherein an AOTask is attempted to be stolen by the current worker.
     */
    public ConcurrentLinkedDeque<AOTask> getConcurrentLinkedDeque() {
        return jobs;
    }

    /**
     * Since the scheduler has access to all of the ActiveObjects, a steal() method is implemented there. Essentially, you are using the steal method from the Scheduler class
     * to retrieve an ActiveObject to steal from. The stealingActiveObjectNumber variable that was declared above is meant to check that the worker is not stealing from itself.
     * Then, when the ActiveObject is retrieved, its ConcurrentLinkedDeque is retrieved and a job is pulled from the bottom of the queue. This is the AOTask that will be worked on by
     * our current worker. 
     */
    public AOTask stealingWork() {
        ActiveObject aoToStealFrom = scheduler.steal(stealingActiveObjectNumber);
        ConcurrentLinkedDeque<AOTask> dequeToStealFrom = aoToStealFrom.getConcurrentLinkedDeque();
        AOTask stolenJob = dequeToStealFrom.pollLast();
        return stolenJob;
    }

    // similar to worker(). This is designed to be called from
    // future.get() -- we keep executing tasks on the queue until the
    // future is completed.
    public void runUntilCompleted(Future future) {
        while (true) {
            AOTask r = jobs.pollFirst();
            if(r == null) {
                r = stealingWork();
            }

            if(r == null) {
                try {
                    Thread.sleep(5 + random.nextInt(4));
                } catch (InterruptedException e) {}
            }

            if(r != null) {
                r.runAndComplete();
            }
        }
	}

    Scheduler getScheduler() {
        return scheduler;
    }
}
