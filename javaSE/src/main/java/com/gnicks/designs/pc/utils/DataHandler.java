package com.gnicks.designs.pc.utils;

import com.gnicks.designs.pc.impl.beans.Product;
import com.gnicks.designs.pc.impl.houses.IOJobStorehouse;

import java.util.List;

public class DataHandler {

//    private static volatile PhoneStorehouse.Handler handler;
//
//    private Handler() {
//
//    }
//
//    public static PhoneStorehouse.Handler getInstance() {
//        if (handler == null) {
//            synchronized (PhoneStorehouse.Handler.class) {
//                if (handler == null) {
//                    handler = new PhoneStorehouse.Handler();
//                }
//            }
//        }
//        return handler;
//    }

    private com.gnicks.designs.pc.impl.houses.IOJobStorehouse IOJobStorehouse;

    public DataHandler(){}

    public DataHandler(IOJobStorehouse pst){
        this.IOJobStorehouse = pst;
    }

    // 省略中间数据加工过程，直接到最后数据处理情况
    public void handleData(List<Product> list) {
        IOJobStorehouse.produce(list, true);
    }


}
