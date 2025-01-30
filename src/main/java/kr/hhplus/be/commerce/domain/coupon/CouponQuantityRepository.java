package kr.hhplus.be.commerce.domain.coupon;

public interface CouponQuantityRepository {
    Long updateCouponQuantityWithLock(Long couponId);
}
