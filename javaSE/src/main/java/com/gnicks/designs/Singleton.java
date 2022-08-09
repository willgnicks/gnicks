package com.gnicks.designs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;

public class Singleton {
    /**
     * 禁止指令重排序
     *
     */
    private volatile static Singleton singleton;

    private Singleton() {
    }

    public static Singleton getInstance(){
        // DCL检查，线程安全，锁中双重判断防止非单例出现
        if (singleton == null){
            synchronized (Singleton.class){
                if (singleton == null){
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }

}
