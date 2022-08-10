package com.gnicks.designs.pc.api;

import java.util.List;

/**
 * 仓库接口，对固定种类的商品进行消费和生产
 *
 * @param <T> 指定商品
 */
public interface Storehouse<T> {
    // 仓库默认容量
    public static final int DEFAULT_CAPACITY = 50000;

    /**
     * 将传入数据生产
     *
     * @param data 数据
     * @param flag 标识符，用于退出消费
     */
    void produce(List<T> data, boolean flag);

    /**
     * 进行内部库存消费
     */
    void consume();
}
