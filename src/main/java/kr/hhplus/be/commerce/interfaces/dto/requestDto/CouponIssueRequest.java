package kr.hhplus.be.commerce.interfaces.dto.requestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CouponIssueRequest(
        @NotNull(message = "사용자 ID는 필수 입력 값입니다.")
        Long userId,

        @NotNull(message = "쿠폰 ID는 필수 입력 값입니다.")
        Long couponId
) {
}

