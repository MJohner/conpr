package as.semaphore;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class SemaphoreImpl implements Semaphore {
    private int value;
    private final Lock lock = new ReentrantLock();
    private final List<Condition> queue = new LinkedList<>();

    public SemaphoreImpl(int initial) {
        if (initial < 0) throw new IllegalArgumentException();
        value = initial;
    }

    @Override
    public int available() {
        lock.lock();
        try {
            return value;
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void acquire() {
        lock.lock();
        try {
            Condition curr = lock.newCondition();
            queue.add(curr);
            while (available() <= 0 || queue.get(0)!=curr){
                try {
                    curr.await();
                }catch (InterruptedException e ) {}
            }
            value--;
            queue.remove(0);
            if(!queue.isEmpty()) queue.get(0).signal();
        }finally {
            lock.unlock();
        }

    }

    @Override
    public void release() {
        lock.lock();
        try {
            value++;
            if(!queue.isEmpty()) queue.get(0).signal();
        }finally {
            lock.unlock();
        }

    }
}
