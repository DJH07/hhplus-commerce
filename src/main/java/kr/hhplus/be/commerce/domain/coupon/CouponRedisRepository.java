package kr.hhplus.be.commerce.domain.coupon;

import java.util.List;

public interface CouponRedisRepository {
    void enqueueCouponRequest(Long userId, Long couponId);

    Long dequeueCouponRequest(Long couponId);

    List<Long> getAllCouponIds();

    Long getIssuedCouponCount(Long couponId);

    void markCouponAsIssued(Long userId, Long couponId);


}
