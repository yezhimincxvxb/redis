package com.yzm.redis11.bloom;

import java.util.BitSet;


public class DefaultBloomFilter extends MyBloomFilter{

    //bit集合，用来存放结果
    private final int DEFAULT_SIZE = Integer.MAX_VALUE;
    private final BitSet bitSet = new BitSet(DEFAULT_SIZE);

    public DefaultBloomFilter() {
    }

    public Integer count() {
        return count;
    }

    public void clear() {
        bitSet.clear();
        count = 0;
    }

    public void push(Object key) {
        for (int i : ints) {
            bitSet.set(hash(key, i));
        }
        count++;
    }

    public boolean contains(Object key) {
        for (int i : ints) {
            boolean exist = bitSet.get(hash(key, i));
            if (!exist) return false;
        }
        return true;
    }

    public int hash(Object key, int i) {
        int h;
        int index = key == null ? 0 : (DEFAULT_SIZE - 1 - i) & ((h = key.hashCode()) ^ (h >>> 16));
        //bitSet下标不能小于0
        return index > 0 ? index : -index;
    }
}
