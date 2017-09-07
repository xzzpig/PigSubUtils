package com.github.xzzpig.pigutils.thread;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadLock {

    private Vector<Thread> threads;

    private AtomicBoolean locked;

    public ThreadLock() {
        locked = new AtomicBoolean(false);
        threads = new Vector<>();
    }

    public boolean isLocked() {
        return locked.get();
    }

    public boolean isPrepared() {
        return threads.contains(Thread.currentThread());
    }

    public void lock() {
        threads.clear();
        synchronized (threads) {
            threads.notifyAll();
        }
        locked.set(true);
        synchronized (locked) {
            locked.notifyAll();
        }
    }

    public void prepare() {
        waitUnlock();
        waitLock();
        threads.add(Thread.currentThread());
        waitUnlock();
    }

    public void prepared() {
        threads.remove(Thread.currentThread());
        synchronized (threads) {
            threads.notifyAll();
        }
    }

    public void unlock() {
        locked.set(false);
        synchronized (locked) {
            locked.notifyAll();
        }
        while (threads.size() != 0)
            synchronized (threads) {
                try {
                    threads.wait();
                } catch (InterruptedException e) {
                }
            }
    }

    public void waitLock() {
        while (!locked.get())
            synchronized (locked) {
                try {
                    locked.wait();
                } catch (InterruptedException e) {
                }
            }
    }

    public void waitUnlock() {
        while (locked.get())
            synchronized (locked) {
                try {
                    locked.wait();
                } catch (InterruptedException e) {
                }
            }
    }
}
