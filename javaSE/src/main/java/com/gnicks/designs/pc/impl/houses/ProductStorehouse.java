package com.gnicks.designs.pc.impl.houses;

import com.gnicks.designs.pc.api.Storehouse;
import com.gnicks.designs.pc.impl.beans.Product;
import com.gnicks.designs.pc.utils.DataHandler;
import com.gnicks.designs.pc.utils.PrintUtil;

import java.util.ArrayList;
import java.util.List;

public class ProductStorehouse implements Storehouse<Product> {
    private final Object signal = new Object();

    private boolean flag = true;

    private List<Product> data = new ArrayList<>();

    private final String producer = "产品生产线程";

    private final String consumer = "产品消费线程";

    private int capacity = DEFAULT_CAPACITY;

    private int countProduce = 0;

    private IOJobStorehouse pst;

    public ProductStorehouse(int capacity, IOJobStorehouse pst) {
        if (capacity > DEFAULT_CAPACITY)
            this.capacity = capacity;
        this.pst = pst;
    }

    @Override
    public void produce(List<Product> data, boolean flag) {
        synchronized (signal) {
            try {
                long start = 0L;
                while ((this.data.size() + data.size()) > capacity) {
                    start = System.currentTimeMillis();
                    signal.wait();
                }
                if (start != 0L && (System.currentTimeMillis() > start)) {
                    PrintUtil.inform(producer, Thread.currentThread().getName(), "waited ", PrintUtil.timeCost(start), "millignicks.designsonds");
                }

                this.data.addAll(data);
                if (this.flag){
                    this.flag = flag;
                }
                PrintUtil.inform(producer, Thread.currentThread().getName(), "current signal", this.flag,"insert flag",flag);

                PrintUtil.inform(producer, Thread.currentThread().getName(), "phone store已生产数据量", data.size(), "当前仓库数量", this.data.size());
                signal.notifyAll();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void consume() {
        List<Product> tmp = new ArrayList<>();
        DataHandler dataHandler = new DataHandler(this.pst);
        int count = 0;
        while (true) {
            synchronized (signal) {
                try {
                    while (this.data.isEmpty()) {
                        signal.wait();
                    }
                    PrintUtil.inform(consumer, Thread.currentThread().getName(), "信号量",flag,"数据量",data.size());
                    PrintUtil.inform(consumer, Thread.currentThread().getName(), "product store consuming", "data size", this.data.size());
                    count += this.data.size();
                    tmp.addAll(this.data);
                    synchronizePrintFlag(flag);
                    this.data.clear();
                    signal.notifyAll();
                    if (flag == false && this.data.isEmpty()){
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            dataHandler.handleData(tmp);
            tmp.clear();
        }

        PrintUtil.inform(consumer, Thread.currentThread().getName(), "phone 仓库 ==> 消费结束", "consume amount = ", count);
    }

    private void synchronizePrintFlag(boolean flag){
        PrintUtil.inform(consumer, Thread.currentThread().getName(), "仓库 ==> 同步条件", "实参标识 = ", flag,"仓库当前标识",pst.getFlag());
        if (pst.getFlag()== false){
            return;
        }
        pst.setFlag(flag);
    }
}

