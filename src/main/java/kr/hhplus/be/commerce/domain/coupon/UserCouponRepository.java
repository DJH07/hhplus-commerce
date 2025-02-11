package kr.hhplus.be.commerce.domain.coupon;

import java.util.List;

public interface UserCouponRepository {
    UserCoupon findById(Long userCouponId);

    Long save(UserCoupon userCoupon);

    List<CouponResult> findAllByUserId(Long userId);

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    void saveAll(List<UserCoupon> userCouponList);
}
