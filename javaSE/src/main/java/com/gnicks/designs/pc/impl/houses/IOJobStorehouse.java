package com.gnicks.designs.pc.impl.houses;

import com.gnicks.designs.pc.api.Storehouse;
import com.gnicks.designs.pc.impl.beans.Product;
import com.gnicks.designs.pc.utils.PrintUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class IOJobStorehouse implements Storehouse<Product> {

    private List<Product> data = new ArrayList<>();

    private boolean flag = true;

    private final static Object signal = new Object();

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        PrintUtil.inform("IO Store信号量修改", Thread.currentThread().getName(), "入参",flag, "当前量", this.flag);
        this.flag = flag;
    }

    private String producer = "任务生产队列";

    private String consumer = "任务消费队列";

    @Override
    public void produce(List<Product> data, boolean flag) {
        synchronized (signal){
            try {
                long start = 0L;
                while ((this.data.size() + data.size()) > DEFAULT_CAPACITY) {
                    start = System.currentTimeMillis();
                    PrintUtil.inform(producer, Thread.currentThread().getName(), "print 仓库已满 --> waiting consume", "当前数量", this.data.size());
                    signal.wait();
                }
                if (start != 0L && System.currentTimeMillis() > start) {
                    PrintUtil.inform(producer, Thread.currentThread().getName(), "生产等待耗时", PrintUtil.timeCost(start), "毫秒");
                }
                this.data.addAll(data);
                PrintUtil.inform(producer, Thread.currentThread().getName(), "print 仓库 --> produced data", data.size(), "当前仓库数量", this.data.size());
                signal.notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void consume() {
        List<Product> data = new ArrayList<>();
        int count = 0;
        while (true) {
            synchronized (signal) {
                try {
                    while (this.data.isEmpty()) {
//                        Thread.sleep(2000);
//                        PrintUtil.inform("Job Consumer", Thread.currentThread().getName(), "Job store is empty now --> waiting producer", "current size", this.data.size());
                        signal.wait();
                    }
                    count += this.data.size();
                    data.addAll(this.data);
                    this.data.clear();
                    signal.notifyAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handleData(data);
            data.clear();
            if (flag == false && this.data.isEmpty())
                break;
        }
        PrintUtil.inform(consumer, Thread.currentThread().getName(), "Job Consumer --> finish consume", "data total： ", count);
    }

    private void handleData(List<Product> data){
        PrintUtil.inform("数据处理线程", Thread.currentThread().getName(), "handle data with size", data.size());
        List<Product> afterSorted = data.stream().sorted(Comparator.comparing(Product::getSerialID)).collect(Collectors.toList());
        if (!afterSorted.isEmpty()){
            PrintUtil.inform("数据处理线程", Thread.currentThread().getName(), "data range --> ", "from_"+ afterSorted.get(0).getSerialID()+
                    "_to_"+ afterSorted.get(afterSorted.size() - 1).getSerialID());
        }

    }

}
