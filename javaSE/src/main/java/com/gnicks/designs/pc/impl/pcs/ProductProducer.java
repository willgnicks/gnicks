package com.gnicks.designs.pc.impl.pcs;

import com.gnicks.designs.pc.api.Storehouse;
import com.gnicks.designs.pc.impl.beans.Product;
import com.gnicks.designs.pc.utils.DataQueue;
import com.gnicks.designs.pc.utils.PrintUtil;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ProductProducer implements Runnable {

    private Storehouse<Product> storehouse;

    private DataQueue dataQueue;

    private BlockingQueue<List<Product>> queue;

    public ProductProducer(DataQueue dataQueue, Storehouse<Product> storehouse) {
        this.storehouse = storehouse;
        this.dataQueue = dataQueue;
        this.queue = dataQueue.getQueue();

    }

    private String role = "队列消费线程";

    @Override
    public void run() {
        // 当通知队列可用 或者队列资源大于0
        int count = 0;
        while (this.dataQueue.getSignal() || !queue.isEmpty()) {
            // 通过阻塞队列锁竞争，拿到已经出队的数据
            List<Product> products = queue.poll();
            // 获取到数据，完成竞争，此时手上已经是有数据的
            if (products != null) {
                ++count;
                PrintUtil.inform(role, Thread.currentThread().getName(),
                        "从队列中拉取数据批次",
                        count, "信号=", dataQueue.getSignal());
                // 生产数据到第一仓库
                // 最后一批数据如何通知
                // 首先肯定是要判断队列状态，如果最后一批已经插入完成，那么收到通知时队列不为空
                // 不满足条件，传入true
                PrintUtil.inform(role,Thread.currentThread().getName(),"input signal",this.dataQueue.getSignal());
                storehouse.produce(products, this.dataQueue.getSignal());
            }
        }
        PrintUtil.inform(role,Thread.currentThread().getName(),"data queue poller finished");
    }

}
