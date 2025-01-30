package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.app.dto.CouponResponse;
import kr.hhplus.be.commerce.domain.coupon.CouponService;
import kr.hhplus.be.commerce.domain.lock.DistributedLock;
import kr.hhplus.be.commerce.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;
    private final UserService userService;


    @DistributedLock(key = "'coupon:quantity:' + #couponId")
    public Long issueCoupon(Long userId, Long couponId) {

        userService.checkUserExists(userId);

        return couponService.processIssueCoupon(userId, couponId);

    }

    public List<CouponResponse> getUserCouponList(Long userId) {

        userService.checkUserExists(userId);

        return couponService.getUserCouponList(userId)
                .stream()
                .map(couponResult -> CouponResponse.builder()
                        .couponId(couponResult.couponId())
                        .couponName(couponResult.couponName())
                        .description(couponResult.description())
                        .validFrom(couponResult.validFrom())
                        .validTo(couponResult.validTo())
                        .issuedAt(couponResult.issuedAt())
                        .status(couponResult.status().name())
                        .build())
                .toList();

    }
}
