package lvc.cds;

import java.util.Random;

public class App {
    static Random r = new Random();

    private double[] x;
    private double[] y;
    private int start;
    private int end;

    /**
     * Inner class that manages the forking and joining of tasks. This fork/join is nearly identical to how we have been doing fork/join in class. The InnerApp is extending from Task,
     * which is essentially a hand-crafted version of RecursiveAction that works with work stealing and hyperobjects. If the the length of the data to look at is less than the threshold
     * value, then the hyperObjectWork() method will be called. It is called from within the inner class so it is possible to access the getHyper and setHyper methods from Task.
     */
    private class InnerApp extends Task {
        private int start;
        private int end;
        private String key;
        private final int THRESHOLD = 1000;

        public InnerApp(int start, int end, String key) {
            this.start = start;
            this.end = end;
            this.key = key;
        }
        @Override
        public void compute() {
            if(end - start < THRESHOLD) {
                hyperObjectWork();
                return;
            }

            int mid = start + (end - start) / 2;
            var left = new InnerApp(start, mid, key);
            left.fork();
            var right = new InnerApp(mid, end, key);
            right.compute();
            left.join();
        }

        public void hyperObjectWork() {
            int hyper = getHyper(key);
            for(int i = start; i < end; i++) {
                hyper += dotSeg();
            }
            setHyper(key, hyper);
        }
        
    }

    public App(double[] x, double[] y) {
        this.x = x;
        this.y = y;
    }

    private double dotSeg() {
        double res = 0.0;
        for (int i = start; i < end; ++i) {
            res += Math.sin(Math.sqrt(Math.abs(x[i] * y[i])));
            res += x[i] * y[i];
        }
        return res;
    }

    public static double dotSegSerial(double[] x, double[] y) {
        double res = 0.0;
        for (int i = 0; i < x.length; ++i) {
            res += Math.sin(Math.sqrt(Math.abs(x[i] * y[i])));
            res += x[i] * y[i];
        }
        return res;
    }

    private void dotAsync2() {
        Scheduler scheduler = new Scheduler(8);
        String hyperObjectKeyName = "computeMath";
        scheduler.createHyperObject(hyperObjectKeyName, 0);
        InnerApp innerApp = new InnerApp(0, x.length, hyperObjectKeyName);
        var fut = scheduler.schedule(innerApp);
        fut.join();
        // Gets the hyperobject val for each worker and combines them
        int hyper = scheduler.getForEachWorker(hyperObjectKeyName);
        System.out.println(hyper);
    }

    public static void testScheduler() {
        final int N = 10_000_000;
        double[] x = new double[N];
        double[] y = new double[N];

        for (int i = 0; i < N; ++i) {
            x[i] = r.nextDouble() * 100_000;
            y[i] = r.nextDouble() * 100_000;
        }

        long start = System.currentTimeMillis();
        dotSegSerial(x, y);
        long end = System.currentTimeMillis() - start;
        System.out.println("Serial time: " + end + " ms");

        App app = new App(x, y);
        app.dotAsync2();
    }


    public static void main(String[] args) {
        testScheduler();
    }
}
