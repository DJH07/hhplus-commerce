package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.app.dto.CouponResponse;
import kr.hhplus.be.commerce.domain.coupon.CouponService;
import kr.hhplus.be.commerce.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;
    private final UserService userService;

    // FIXME : 쿠폰 서비스의 메서드 하나로 통합 (쿠폰 서비스 내 통합 메서드 만들고, 내부에 세 메서드 넣기)
    // 통합하면서 트랜잭션 레벨 고려
    public Long issueCoupon(Long userId, Long couponId) {

        userService.checkUserExists(userId);

        couponService.validateCouponExpiration(couponId);

        couponService.updateCouponQuantity(couponId);

        return couponService.issueUserCoupon(userId, couponId);

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
