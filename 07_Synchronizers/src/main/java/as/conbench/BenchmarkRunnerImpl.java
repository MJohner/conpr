package as.conbench;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class BenchmarkRunnerImpl implements BenchmarkRunner {
    private shouldRun run = new shouldRun();
    private CyclicBarrier barrier;

    @Override
    public void runBenchmark(BenchmarkDescriptor desc) {
        int numberOfRuns = desc.testMethods.get(0).nThreads.length;


        System.out.println("Starting benchmark for Class: " + desc.testClass.getName());
        for (int i = 0; i < numberOfRuns; i++) {
            AtomicReference<Long> end = new AtomicReference<>(0L);
            run = new shouldRun();

            AtomicInteger nrOfThreads = new AtomicInteger();

            int finalI1 = i;
            desc.testMethods.forEach(tm -> nrOfThreads.addAndGet(tm.nThreads[finalI1]));

            barrier = new CyclicBarrier(nrOfThreads.get(), () -> end.set(System.nanoTime()));
            benchMarkRun(desc, i);
            System.out.println("Run " + i + " start ");
            Long start = System.nanoTime();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            run.yes();

            while (end.get() == 0L) {

            }

            final StringBuilder builder = new StringBuilder();
            builder.append("- Run[" + i + "] ");

            int finalI = i;
            desc.testMethods.forEach(tm -> {
                builder.append(tm.method.getName() + "[" + tm.nThreads[finalI] + "]" + ", ");
            });
            builder.append("Duration: ");
            if((end.get() - start) / 1_000_000 / desc.nTimes == 0L){
                builder.append((end.get() - start) / desc.nTimes + "ns");
            }else{
                builder.append((end.get() - start) / 1_000_000 / desc.nTimes + "ms");
            }
            System.out.println(builder.toString());
            System.out.println();
        }


    }

    private void benchMarkRun(BenchmarkDescriptor desc, int runNumber) {
        System.out.println("Run " + runNumber + " setup");
        desc.testMethods.forEach(tm -> {
            for (int i = 0; i < tm.nThreads[runNumber]; i++) {
                new Thread(() -> {
                    System.out.println("Ready: "+Thread.currentThread().getName());
                    while (!run.isShouldRun()) {

                    }
                    System.out.println("Go: "+Thread.currentThread().getName());
                    try {
                        tm.method.invoke(desc.testClass.newInstance(), desc.nTimes, 1);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        });
    }
}

class shouldRun {
    private volatile boolean shouldRun = false;

    public void yes() {
        shouldRun = true;
    }

    public boolean isShouldRun() {
        return shouldRun;
    }
}
