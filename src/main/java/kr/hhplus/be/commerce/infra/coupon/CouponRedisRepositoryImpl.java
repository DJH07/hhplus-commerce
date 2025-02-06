package kr.hhplus.be.commerce.infra.coupon;

import kr.hhplus.be.commerce.domain.coupon.CouponRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CouponRedisRepositoryImpl implements CouponRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String COUPON_ISSUED_KEY = "coupon:issued:";
    private static final String COUPON_QUEUE_KEY = "coupon:queue:";

    @Override
    public void enqueueCouponRequest(Long userId, Long couponId) {
        String redisKey = COUPON_QUEUE_KEY + couponId;
        redisTemplate.opsForList().rightPush(redisKey, userId);
    }

    @Override
    public Long dequeueCouponRequest(Long couponId) {
        return (Long) redisTemplate.opsForList().leftPop(COUPON_QUEUE_KEY + couponId);
    }

    @Override
    public List<Long> getAllCouponIds() {
        Set<String> keys = redisTemplate.keys(COUPON_QUEUE_KEY + "*");
        return keys.stream()
                .map(key -> Long.valueOf(key.replace(COUPON_QUEUE_KEY, "")))
                .toList();
    }

    @Override
    public Long getIssuedCouponCount(Long couponId) {
        return redisTemplate.opsForSet().size(COUPON_ISSUED_KEY + couponId);
    }

    @Override
    public void markCouponAsIssued(Long userId, Long couponId) {
        redisTemplate.opsForSet().add(COUPON_ISSUED_KEY + couponId, userId);
    }

}
