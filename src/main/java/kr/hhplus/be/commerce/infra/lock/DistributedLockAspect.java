package kr.hhplus.be.commerce.infra.lock;

import kr.hhplus.be.commerce.domain.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedisLockManager redisLockManager;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(kr.hhplus.be.commerce.domain.lock.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(
                signature.getParameterNames(),
                joinPoint.getArgs(),
                distributedLock.key()
        );

        log.info("Acquiring lock with key: {}", key);

        return redisLockManager.executeWithLock(
                key,
                () -> {
                    try {
                        return aopForTransaction.proceed(joinPoint);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}