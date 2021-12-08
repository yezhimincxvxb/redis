package com.yzm.redis03.utils;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class RedisTransactionUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTransactionUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //开启事务
    public void multi() {
        redisTemplate.multi();
    }

    //监控
    public void watch(String key) {
        redisTemplate.watch(key);
    }

    public void watch(Collection<String> keys) {
        redisTemplate.watch(keys);
    }

    public void unWatch() {
        redisTemplate.unwatch();
    }

    //事务提交
    public List<Object> exec() {
        return redisTemplate.exec();
    }

    //事务回滚
    public void discard() {
        redisTemplate.discard();
    }

    /**
     * 报错：RedisCommandExecutionException: ERR EXEC without MULTI
     * 在执行 EXEC 命令之前，没有执行 MULTI 命令。这很奇怪，我们明明在测试方法的第一句就执行了 MULTI。
     * 由于 enableTransactionSupport 属性的默认值是 false，导致了每一个 RedisConnection 都是重新获取的。所以，我们刚刚执行的 MULTI 和 EXEC 这两个命令不在同一个 Connection 中。
     */
    public void test01() {
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();
        redisTemplate.opsForValue().set("k1", "v1");
        redisTemplate.opsForValue().set("k2", "v2");
        redisTemplate.opsForValue().get("k2");
        redisTemplate.opsForValue().set("k3", "v3");
        List<Object> exec = redisTemplate.exec();
        System.out.println("exec = " + exec);
    }

    /**
     * 事务提交
     */
    public void test02() {
        SessionCallback<Object> callback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForValue().set("k1", 1);
                operations.opsForValue().set("k2", 2);
                operations.opsForValue().get("k2");
                operations.opsForValue().set("k3", 3);
                List exec = operations.exec();
                System.out.println("exec = " + exec);
                return "事务提交";
            }
        };
        System.out.println(redisTemplate.execute(callback));
        System.out.println(redisTemplate.opsForValue().get("k2"));
    }

    /**
     * 事务回滚
     */
    public void test03() {
        SessionCallback<Object> callback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForValue().set("k4", 4);
                operations.opsForValue().set("k5", 5);
                operations.discard();
                return "事务回滚";
            }
        };
        System.out.println(redisTemplate.execute(callback));
        System.out.println(redisTemplate.opsForValue().get("k4"));
    }

    /**
     * 语法错误
     */
    public void test04() {
        SessionCallback<Object> callback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForValue().set("k6", 6);
                operations.opsForValue().set("k7", "7");
//                operations.opsForValue().increment("k7");
                int i = 1 / 0;
                operations.opsForValue().set("k8", 8);
                List exec = operations.exec();
                System.out.println("exec = " + exec);
                return "语法错误";
            }
        };
        System.out.println(redisTemplate.execute(callback));
        System.out.println(redisTemplate.opsForValue().get("k6"));
    }

    /**
     * 监控
     */
    public void test05() {
        redisTemplate.opsForValue().set("watch", "ok");
        SessionCallback<Object> callback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch("watch");
                operations.multi();
                operations.opsForValue().set("k8", 8);
                operations.opsForValue().set("k9", 9);
                List exec = operations.exec();
                System.out.println("exec = " + exec);
                return "监控";
            }
        };
        System.out.println(redisTemplate.execute(callback));
        System.out.println(redisTemplate.opsForValue().get("k8"));
    }

    /**
     * 监控
     */
    public void test06() {
        redisTemplate.opsForValue().set("watch2", "ok");
        SessionCallback<Object> callback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch("watch2");
                operations.multi();
                operations.opsForValue().set("k10", 10);
                operations.opsForValue().set("watch2", "no");
                operations.opsForValue().set("k11", 11);
                List exec = operations.exec();
                System.out.println("exec = " + exec);
                return "监控";
            }
        };
        System.out.println(redisTemplate.execute(callback));
        System.out.println(redisTemplate.opsForValue().get("k10"));
    }

    /**
     * 监控
     */
    public void test07() {
        redisTemplate.opsForValue().set("watch3", "ok");
        SessionCallback<Object> callback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch("watch3");
                operations.multi();
                operations.opsForValue().set("k12", 10);

                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                operations.opsForValue().set("k13", 11);
                List exec = operations.exec();
                System.out.println("exec = " + exec);
                return "监控";
            }
        };
        System.out.println(redisTemplate.execute(callback));
        System.out.println(redisTemplate.opsForValue().get("k12"));
    }
}
