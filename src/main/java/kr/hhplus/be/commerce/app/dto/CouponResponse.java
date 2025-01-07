package kr.hhplus.be.commerce.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CouponResponse(
        Long couponId,
        String couponName,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime validFrom,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime validTo,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime issuedAt,
        String status
) {
}

