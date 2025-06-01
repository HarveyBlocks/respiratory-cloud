package org.harvey.respiratory.cloud.common.utils;


import lombok.Getter;
import org.harvey.respiratory.cloud.common.constants.RedisConstants;
import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.lang.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson锁
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-21 21:20
 */
public class UserDtoRedissonLock {
    private final RedissonClient redissonClient;

    @Getter
    private UserDto errorResult = null;

    public UserDtoRedissonLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public static ServerException haveToExecuteSlowException() {
        return new HaveToExecuteSlowException();
    }

    public void setErrorResult(UserDto errorResult) {
        this.errorResult = errorResult;
    }

    /**
     * 分布式锁
     *
     * @param lockKey    lock的键
     * @param fastSupply 获取值的快方法
     * @param slowSupply 获取值的慢方法
     * @return id
     */

    @Nullable
    public UserDto asynchronousLock(
            String lockKey, Supplier<UserDto> fastSupply, Supplier<UserDto> slowSupply) throws InterruptedException {
        // 获取锁(可重入)
        try {
            return fastSupply.get();
        } catch (HaveToExecuteSlowException ignore1) {
            // 只能执行下面的慢操作
            RLock lock = redissonClient.getLock(lockKey);
            // 尝试获取锁, 参数分别为: 获取锁的最大等待时间(期间会重试),锁自动释放时间, 时间单位
            long waitTime = -1L;// 等待时间, 默认-1不等待
            long releaseTime = RedisConstants.LOCK_TTL;// 自动释放时间,默认30s
            boolean isLock = lock.tryLock(waitTime, releaseTime, TimeUnit.SECONDS);
            if (!isLock) {
                // 没有成功上锁
                return errorResult;
            }
            try {
                // 上完锁之后是不是还要再分析一下
                try {
                    return fastSupply.get();
                } catch (HaveToExecuteSlowException ignore2) {
                    return slowSupply.get();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public boolean isErrorResult(UserDto result) {
        return result == errorResult;
    }

    private static class HaveToExecuteSlowException extends ServerException {

    }

}
