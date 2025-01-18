package kr.hhplus.be.commerce.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record PaymentRequest(
        @NotNull(message = "주문 ID는 필수값입니다.")
        @Positive(message = "유효하지 않은 주문 ID입니다.")
        @Schema(description = "주문 ID")
        Long orderId,
        @Positive(message = "유효하지 않은 유저 보유 쿠폰 ID입니다.")
        @Schema(description = "유저 보유 쿠폰 ID")
        Long userCouponId
) {
}
