package com.gnicks.designs.pc.utils;

import com.gnicks.designs.pc.impl.beans.Product;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DataQueue {
    private static final int DEFAULT_CAPACITY = 10;

    private static BlockingQueue<List<Product>> queue;

    private static volatile DataQueue dataQueue;

    private boolean signal = true;

    public boolean getSignal() {
        return signal;
    }

    public void setSignal(boolean signal) {
        this.signal = signal;
    }

    private void init() {
    }

    private DataQueue(int capacity) {
        queue = capacity > DEFAULT_CAPACITY
            ? new LinkedBlockingQueue<List<Product>>(capacity)
            : new LinkedBlockingQueue<List<Product>>(DEFAULT_CAPACITY);
    }

    public static DataQueue getInstance(int capacity) {
        if (dataQueue == null) {
            synchronized (DataQueue.class) {
                if (dataQueue == null) {
                    dataQueue = new DataQueue(capacity);
                }
            }
        }
        return dataQueue;
    }

    public BlockingQueue<List<Product>> getQueue() {
        return dataQueue.queue;
    }
}
