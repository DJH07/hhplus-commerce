package kr.hhplus.be.commerce.domain.coupon;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CouponResult(
        Long couponId,
        String couponName,
        String description,
        LocalDateTime validFrom,
        LocalDateTime validTo,
        LocalDateTime issuedAt,
        UserCouponStatus status
) {
}

