package com.gnicks.designs.pc.impl.pcs;

import com.gnicks.designs.pc.api.Storehouse;
import com.gnicks.designs.pc.impl.beans.Product;

public class ProductConsumer implements Runnable {

    private Storehouse<Product> st;


    public ProductConsumer(Storehouse<Product> st) {
        this.st = st;
    }


    @Override
    public void run() {
        st.consume();
    }
}
