package com.mysaasa.api.messages;

public class NetworkStateChange {
   private final int depth;

    public NetworkStateChange(int depth) {
        this.depth = depth;
    }

    public boolean isBusy() {
        return depth>0;
    }

    public int getDepth() {
        return depth;
    }
}
