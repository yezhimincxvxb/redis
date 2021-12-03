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


    //    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void test() {
//        demo01();
//        demo01_2();
    }

    /**
     * 报错：RedisCommandExecutionException: ERR EXEC without MULTI
     * 在执行 EXEC 命令之前，没有执行 MULTI 命令。这很奇怪，我们明明在测试方法的第一句就执行了 MULTI。
     * 由于 enableTransactionSupport 属性的默认值是 false，导致了每一个 RedisConnection 都是重新获取的。所以，我们刚刚执行的 MULTI 和 EXEC 这两个命令不在同一个 Connection 中。
     */
    private void demo01() {
        redisTemplate.multi();
        redisTemplate.opsForValue().set("k1", "v1");
        redisTemplate.opsForValue().set("k2", "v2");
        redisTemplate.opsForValue().get("k2");
        redisTemplate.opsForValue().set("k3", "v3");
        System.out.println("exec = " + redisTemplate.exec());
    }

    /**
     * 正确事务执行
     */
    private void demo01_2() {
        SessionCallback<Object> callback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForValue().set("k1", 1);
                operations.opsForValue().set("k2", 2);
                operations.opsForValue().get("k2");
                operations.opsForValue().set("k3", 3);
                return operations.exec();
            }
        };
        System.out.println("exec = " + redisTemplate.execute(callback));
    }

}
