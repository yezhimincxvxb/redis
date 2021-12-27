package com.yzm.redis11.bloom;


public abstract class MyBloomFilter {

    //用来生成不同的hash值，可以随便给，但别给奇数
    protected final int[] ints = {8, 22, 32, 66, 88, 128};
    //布隆过滤器中数据个数
    protected Integer count = 0;

    //数据量
    public abstract Integer count();

    //清空数据
    public abstract void clear();

    //添加数据
    public abstract void push(Object key);

    //判断key是否存在，true不一定说明key存在，但是false一定说明不存在
    public abstract boolean contains(Object key);

    //hash算法
    public abstract int hash(Object key, int i);
}
