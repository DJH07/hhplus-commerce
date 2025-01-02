package kr.hhplus.be.commerce.interfaces.dto.responseDto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CouponResponse(
        Long couponId,
        String couponName,
        String details,
        LocalDate validFrom,
        LocalDate validTo,
        String status
) {
}

