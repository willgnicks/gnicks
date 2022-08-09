package com.gnicks.designs;


import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DesignTest extends TestCase {
    private final static Logger LOGGER = LogManager.getLogger(DesignTest.class);

    public void testInstance(){}

    public void testSingleton(){
        for (int i = 0; i < 100; i++) {
            new Thread(()->{LOGGER.debug(Singleton.getInstance().hashCode());}).start();
        }
    }
    public void testConsumer(){

    }
}
