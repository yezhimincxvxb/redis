package com.yzm.redisson.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
public class RedissonService {

    @Autowired
    private RedissonClient singleServer;


    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void test() {
//        demo01();
        RList<Object> list = singleServer.getList("l_key");
        System.out.println("list = " + list);
//        List<Object> range = list.range(0, -1);
//        System.out.println("range = " + range);
    }

    public void demo01() {
        RLock lock = singleServer.getLock("yzm");


        Thread t1 = new Thread(getRunnable(lock), "t1");
        Thread t2 = new Thread(getRunnable(lock), "t2");
        Thread t3 = new Thread(getRunnable(lock), "t3");

        t1.start();
        t2.start();
        t3.start();
    }

    public void demo02() {

    }

    private Runnable getRunnable(RLock lock) {
        return () -> {
            log.info("线程：" + Thread.currentThread().getName() + " is begin");
            try {
                Thread.sleep(1000);
                lock.lock();
                log.info("线程：" + Thread.currentThread().getName() + " need to work");
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (lock.isHeldByCurrentThread()) lock.unlock();
            }
            log.info("线程：" + Thread.currentThread().getName() + " is over");
        };
    }
}
