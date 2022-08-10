package com.gnicks.designs;


import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DesignTest extends TestCase {
    private final static Logger LOGGER = LogManager.getLogger(DesignTest.class);

    //    Logger
    public void testInstance() {
    }

    public void testSingleton() {
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                LOGGER.info(Singleton.getInstance().hashCode());
            }).start();
        }
    }

    public void testConsumer() {
        for (int i = 0; i < 50; i++) {
            LOGGER.trace("spring");
            LOGGER.debug("boot");
            LOGGER.info("is");
            LOGGER.warn("a good");
            LOGGER.error("util");
        }
    }

    public static void main(String[] args) {

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(20,
                30,
                100,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(20));
        Information information = new Information();
        for (int i = 0; i < 20; i++) {
            poolExecutor.submit(information);
        }
        poolExecutor.shutdown();

//        new Thread(() -> {
//            LOGGER.info(Thread.currentThread().getName());
//        }).start();
    }


    private static class Information implements Runnable {
        @Override
        public void run() {
            LOGGER.info(Thread.currentThread().getName());
        }
    }
}
