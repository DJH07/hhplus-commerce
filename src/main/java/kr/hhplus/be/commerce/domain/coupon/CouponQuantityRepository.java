package kr.hhplus.be.commerce.domain.coupon;

public interface CouponQuantityRepository {
    CouponQuantity findByIdWithLock(Long couponId);
}
