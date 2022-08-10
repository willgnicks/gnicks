package com.gnicks.designs.pc;

import com.gnicks.designs.pc.api.Storehouse;
import com.gnicks.designs.pc.impl.beans.Product;
import com.gnicks.designs.pc.impl.houses.IOJobStorehouse;
import com.gnicks.designs.pc.impl.houses.ProductStorehouse;
import com.gnicks.designs.pc.impl.pcs.DataQueueProducer;
import com.gnicks.designs.pc.impl.pcs.IOJobConsumer;
import com.gnicks.designs.pc.impl.pcs.ProductConsumer;
import com.gnicks.designs.pc.impl.pcs.ProductProducer;
import com.gnicks.designs.pc.utils.DataQueue;
import com.gnicks.designs.pc.utils.PrintUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final int DATA_SIZE = 1000000;

    private static final int BATCH_SIZE = 5000;

    private static final int PRODUCT_STOREHOUSE_CAPACITY = 50000;

    private static  final int DATA_QUEUE_CAPACITY = 10;

    public static void main(String[] args) {


        ExecutorService pool = Executors.newFixedThreadPool(50, new CustomThreadFactory("pool"));

        // 仓库资源和阻塞队列资源
        DataQueue dataQueue = DataQueue.getInstance(DATA_QUEUE_CAPACITY);

        IOJobStorehouse ioJobStorehouse = new IOJobStorehouse();
        Storehouse<Product> storehouse = new ProductStorehouse(PRODUCT_STOREHOUSE_CAPACITY, ioJobStorehouse);

        // 原始数据生产 类似 processor 往队列里边扔数据
        DataQueueProducer dataQueueProducer = new DataQueueProducer(dataQueue, DATA_SIZE, BATCH_SIZE);

        // 将队列中的数据生产，添加到第一个仓库
        ProductProducer productProducer = new ProductProducer(dataQueue, storehouse);

        // 第一个仓库的消费线程将仓库数据进行处理，处理完成的数据将放入作为生产者放入第二个仓库中
        ProductConsumer productConsumer = new ProductConsumer(storehouse);
        // 模拟第二个仓库的消费者，将处理好的数据进行磁盘IO，写入数据库
        IOJobConsumer IOJobConsumer = new IOJobConsumer(ioJobStorehouse);

        long start = System.currentTimeMillis();

        pool.execute(dataQueueProducer);

        addThreadToPool(5, pool, productProducer);
        addThreadToPool(6, pool, productConsumer);
        addThreadToPool(7, pool, IOJobConsumer);

        pool.shutdown();

        try {
            pool.awaitTermination(2000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            PrintUtil.inform("主程序",Thread.currentThread().getName(),"耗时", PrintUtil.timeCost(start),"毫秒");
        }

    }

    private static void addThreadToPool(int number, ExecutorService pool, Runnable t){
        for (int i = 0; i < number; i++) {
            pool.submit(t);
        }
    }

    static class CustomThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final String namePrefix;

        CustomThreadFactory(String poolName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = poolName + "-" + poolNumber + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}


