package com.gnicks.designs.pc.impl.pcs;

import com.gnicks.designs.pc.impl.houses.IOJobStorehouse;

public class IOJobConsumer implements Runnable{

    private com.gnicks.designs.pc.impl.houses.IOJobStorehouse ioJobStorehouse;

    public IOJobConsumer(IOJobStorehouse ioJobStorehouse){
        this.ioJobStorehouse = ioJobStorehouse;
    }
    @Override
    public void run() {
        ioJobStorehouse.consume();
    }
}
