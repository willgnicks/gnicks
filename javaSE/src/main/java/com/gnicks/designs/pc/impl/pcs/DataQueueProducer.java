package com.gnicks.designs.pc.impl.pcs;

import com.gnicks.designs.pc.impl.beans.Product;
import com.gnicks.designs.pc.utils.DataQueue;
import com.gnicks.designs.pc.utils.PrintUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class DataQueueProducer implements Runnable {

    private DataQueue dataQueue;

    private BlockingQueue<List<Product>> queue;

    private static final int DEFAULT_DATA_SIZE = 500000;

    private static final int DEFAULT_BATCH_SIZE = 5000;

    private int dataSize = DEFAULT_DATA_SIZE;

    private int batchSize = DEFAULT_BATCH_SIZE;

    private int count = 0;

    private int amount = 0;

    private String role = "队列生产线程";

    public DataQueueProducer(DataQueue dataQueue, int dataSize, int batchSize) {
        if (dataQueue != null) {
            this.dataQueue = dataQueue;
            this.queue = dataQueue.getQueue();
        }
        if (dataSize > this.dataSize) {
            this.dataSize = dataSize;
        }
        if (batchSize > this.batchSize) {
            this.batchSize = batchSize;
        }
    }

    @Override
    public void run() {
        PrintUtil.inform(role, Thread.currentThread().getName(), "需处理数据量", dataSize, "设计每批数量", batchSize);
        long start = System.currentTimeMillis();
        List<Product> objects = new ArrayList<>();
        try {
            // 进行数据构造，数据量大小和每批数据大小由传参控制
            for (int i = 1; i <= dataSize; i++) {
                Product product = new Product(i, "iphone", "iphone" + i, 5 + i, this.count + 1);
                objects.add(product);
                // 数据每个batch size插入一次
                if (objects.size() > 0 && objects.size() % this.batchSize == 0) {
                    assert (!objects.isEmpty()) : "DataQueueProducer Internal Error";
                    insertAndInform(this.queue, objects);
                    objects.clear(); // 插入后进行删除 清理内存
                }
            }
            PrintUtil.inform(role, Thread.currentThread().getName(), "插入最后一批数据");
            // 不管最后这批数据是否有数据，在此插入队列是保证队列中是有可取元素
            // 此时通知其他queue使用者原子信息，队列有元素 并且信号为红
            insertAndInform(this.queue, objects);
            this.dataQueue.setSignal(false);
            objects.clear();
            // 将通知所有queue使用者已经结束生产
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PrintUtil.inform(role, Thread.currentThread().getName(), "队列生产完毕", "共生产数据量",
                this.amount, "队列耗时", PrintUtil.timeCost(start), "信号", dataQueue.getSignal());
    }

    private void insertAndInform(BlockingQueue<List<Product>> queue, List<Product> data) throws InterruptedException {
        ++this.count;
        this.amount += data.size();
        queue.put(new ArrayList<>(data));
        data.clear();
        PrintUtil.inform(role, Thread.currentThread().getName(),
                "批号", this.count,
                "当前队列长度", this.queue.size());
    }
}
