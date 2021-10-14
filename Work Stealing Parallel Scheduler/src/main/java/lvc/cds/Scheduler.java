package lvc.cds;

import java.util.HashMap;
import java.util.Random;


public class Scheduler {
    private ActiveObject[] workers;
    private Random random = new Random();
    
    // Array of HashMaps, wherein at index 0, for example, the hyperobject has a key with its corresponding value. This represents the total for the hyperobject over all workers.
    private HashMap<String, Integer>[] hyperObjects;

    /**
     * Constructor for the initial Scheduler. The number to pass in based on the number of hardware thread that are a part of your computer (I used 8 but could be 4, 16, etc.)
     * Creates an araay of ActiveObject workers that each have their own worker queue. Since Scheduler has access to all of the ActiveObjects, the get, set, and create hyperobjects
     * methods are located inside this class. The management of the hyperobjects was described in class as being best put here. As described in class as well, there is an array of HashMaps
     * meaning that there can be more than one type of hyperobject (in our case, we coudld be computing the dot product, average, some other computation, etc.). Each index will contain
     * a hashmap with a String key and an Integer value. So, if there is really only one hyperobject, there will be one key and one value that is shared across all of the workers.
     */
    public Scheduler(int num) {
        workers = new ActiveObject[num];
        for (int i=0; i<num; ++i) {
            workers[i] = new ActiveObject(this, i);
        }
        this.hyperObjects = new HashMap[num];
        for(int i = 0; i < num; i++) {
            this.hyperObjects[i] = new HashMap<String, Integer>();
        }

        // Fixes the potential of an ActiveObject attempting to steal work from non-existent worker. It initializes all workers and then starts all of them at once.
        for(ActiveObject ao: workers) {
            ao.start();
        }
    }

    /**
     * Gets each individual portion of the hyperobject and combines it to get the total hyperValue. Essentially, you create a new Integer array that will store each and every hyperobject
     * value for a given key. I initially had a function being passed in for this method, but I instead decided to make things simpler and simply pass in the key name.
     * The hyperValue will be returned, which is the total value from all hyperObject pieces. Remember that we are splitting up our work across multiple workers and one worker does not
     * have the entire value from a computation. 
     */
    public Integer getForEachWorker(String key) {
        // Stores all of the hyperobject values 
        Integer[] workingArray = new Integer[hyperObjects.length];

        // Gets the value of each hyperobject for each worker based on the key.
        for(int i = 0; i < workingArray.length; i++) {
            workingArray[i] = hyperObjects[i].get(key);
        }

        Integer completeHyperValue = 0;
        for(int i = 0; i < workingArray.length; i++) {
            completeHyperValue += workingArray[i];
        }
        return completeHyperValue;
    }

    /**
     * Implemented in class. Instead of the round-robin approach, we are randomly assigning a task to a worker. We are passing in the Task. We don't explicitly pass in a Task, but instead
     * pass in an instance of the inner class of App, which extends the abstract Task class.
     */
    public Future schedule(Task task) {
        int randomWorker = random.nextInt(workers.length);
        return workers[randomWorker].enqueue(task);
    }

    /**
     * This will attempt to steal work from another worker. As described in class, we do not want our current worker to be attempting to steal from its own queue, so we can set
     * up a while loop that checks this. So, the index of the current worker is passed in and a random index is chosen for a worker to steal from. If the current worker is equal to the
     * randomly chosen worker, than a new worker will be chosen. Once a unique worker is chosen, it will be returned back to the ActiveObject, and it will steal from that workers
     * queue.
     */
    public ActiveObject steal(int i) {
        int workerToStealFrom = random.nextInt(workers.length);
        while(workers[workerToStealFrom].equals(workers[i])) {
            workerToStealFrom = random.nextInt(workers.length);
        }
        return workers[workerToStealFrom];
    }

    /**
     * Utilized in the steal method.
     */
    public ActiveObject getCurrentWorker(int i) {
        return workers[i];
    }

    /**
     * Called from within the inner class of App. Will simply create a hyperobject for each ActiveObject that there is. Will put the designated key and value in the hyperObjects
     * hash map that is implemented in Scheduler. Hashmap is implemented in scheduler because it has access to all of the ActiveObjects. The initial value will be 0 for each.
     */
    public void createHyperObject(String key, Integer value) {
        for(int i = 0; i < workers.length; i++) {
            hyperObjects[i].put(key, value);
        }
    }

    /**
     * Extension from the Task class. Simply get the value of the hyperobject for the current ActiveObject/worker. In this case, we are returning an integer because we are assuming
     * that all of our hyperobjects our integers. This is clearly seen within the setHyperObject method.
     */
    public Integer getHyperObject(Thread t, String key) {
        for(int i = 0; i < workers.length; i++) {
            if(t == workers[i]) {
                return hyperObjects[i].get(key);
            }
        }
        return 0;
    }

    /**
     * Extension from the Task class. Simply, you are assigning a hyperobject with the provided key and value to the current worker. The intial value should be 0.
     */
    public void setHyperObject(Thread t, String key, Integer value) {
        for(int i = 0; i < workers.length; i++) {
            if(t == workers[i]) {
                hyperObjects[i].replace(key, value);
            }
        }
    }

    /**
     * Simple termination method that pushes a null AOTask onto each worker. Won't worry about stealing logic with poison pills in this project.
     */
    public void terminate() {
        for (ActiveObject ao : workers)
            ao.terminate();
    }

    // I have a feeling that these are causing my the illegal state exceptions, but I can not tell.
    boolean isAWorker(Thread th) {
        for (var w : workers) {
            if (th == w) {
                return true;
            }
        }
        return false;
    }

    /**
     * Runs the current task until it is completed on the current thread/worker.
     */
    void workUntilCompleted(Thread th, Future f) {
        for (var w : workers) {
            if (th == w) {
                w.runUntilCompleted(f);
                return;
            }
        }
    }

}
