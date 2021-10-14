package lvc.cds;

public abstract class Task {
    private Future future; 

    // This implementation would separate the base class from the drive class. The base class does
    // not need to know how compute works and the drive class doesn't need to know how the fork/join works.
    
    protected Task() {}

    /**
     * Implemented in class. Simply gets the current ActiveObject and then enqueues the current Task onto it.
     */
    public void fork() {
        var ao = getAO();
        ao.enqueue(this);
    }

    /**
     * Instead of passing in a Future when creating a new Task, I instead decided to set the future in a separate method. 
     * This is called from within the enqueue method in ActiveObject. The ActiveObject has access to create a new Future on the
     * current ActiveObject, so it can be set here. 
     */
    public void setFuture(Future fut) {
        this.future = fut;
    }

    /**
     * Also implemented in class. If the future is not null, it will attempt to get the value. Otherwise, an exception will be thrown
     * stating that you can only join from the worker/active thread.
     */
    public void join() {
        if(future != null) {
            future.join();
        } else {
            throw new IllegalStateException("Can only join from worker thread");
        }
    }

    /**
     * Implemented in class as well. Starts off by getting the current thread and then checks to see if the thread is an instance of the ActiveObject class.
     * If it is, then the thread is an ActiveObject and should return the ActiveObject.
     */
    private ActiveObject getAO() {
        var th = Thread.currentThread();
        if(th instanceof ActiveObject) {
            return (ActiveObject) th;
        } else {
            throw new IllegalStateException("Not running on an AO thread");
        }
    }

    /**
     * Initially implemented in class, but expanded on further. You start off by getting the ActiveObject and its Scheduler. You then get the current thread, which
     * will be utilized to determine if the current worker is the same as the current thread. Essentially, this method is taking in the key (the value that indicates which
     * hyperobject we are looking at. In our example, we are just doing the computeMath key) and then retrieving the hyperobject of the running ActiveObject with that specific
     * key. I utilized a similar idea to what getAO is doing.
     */
    public Integer getHyper(String key) {
        var th = Thread.currentThread();
        Integer hyper = 0;
        if(th instanceof ActiveObject) {
            var ao = (ActiveObject) th;
            Scheduler scheduler = ao.getScheduler();
            hyper = scheduler.getHyperObject(th, key);
        }
        return hyper;
    }

    /**
     * Sets the value of the hyperobject with a given key and value. For us, we are initially setting some key value and initializing it to 0, since we are only dealing with 
     * integer hyperobjects. This setHyper method is called from within the inner class of App. The scheduler method of setHyperObject will set the value of the currently running
     * worker with the passed in argument. I utilized a similar idea to what is occurring in getHyper.
     */
    public void setHyper(String key, Integer value) {
        var th = Thread.currentThread();
        if(th instanceof ActiveObject) {
            var ao = (ActiveObject) th;
            Scheduler scheduler = ao.getScheduler();
            scheduler.setHyperObject(th, key, value);
        }
    }

    /**
     * This abstract method is implemented within the inner class of App. Essentialy, Task acts as a RecursiveAction. So, the compute method will act nearly identical to the 
     * previous fork/join implementation we had. Instead, we have our above fork/join method that we are utilizing. 
     */
    public abstract void compute();
}
