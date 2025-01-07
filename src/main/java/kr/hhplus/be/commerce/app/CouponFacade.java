package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.domain.coupon.CouponService;
import kr.hhplus.be.commerce.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;
    private final UserService userService;

    public Long issueCoupon(Long userId, Long couponId) {

        userService.checkUserExists(userId);

        couponService.validateCouponExpiration(couponId);

        couponService.updateCouponQuantity(couponId);

        return couponService.issueUserCoupon(userId, couponId);

    }
}
