package kr.hhplus.be.commerce.infra.lock;

import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisLockManager {

    private final RedissonClient redissonClient;

    private static final long DEFAULT_LOCK_WAIT_TIME = 5L;
    private static final long DEFAULT_LOCK_LEASE_TIME = 10L;

    public <T> T executeWithLock(String key, LockCallback<T> callback) {
        return executeWithLock(key, DEFAULT_LOCK_WAIT_TIME, DEFAULT_LOCK_LEASE_TIME, callback);
    }

    public <T> T executeWithLock(String key, long waitTime, long leaseTime, LockCallback<T> callback) {
        RLock lock = redissonClient.getLock(key);
        if (!tryAcquireLock(lock, waitTime, leaseTime)) {
            throw new BusinessException(BusinessErrorCode.LOCK_TIMEOUT);
        }

        try {
            return callback.doWithLock();
        } finally {
            releaseLock(lock);
        }
    }

    private boolean tryAcquireLock(RLock lock, long waitTime, long leaseTime) {
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(BusinessErrorCode.LOCK_TIMEOUT);
        }
    }

    private void releaseLock(RLock lock) {
        if (lock.isHeldByCurrentThread()) {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
            }
        }
    }


    @FunctionalInterface
    public interface LockCallback<T> {
        T doWithLock();
    }
}
