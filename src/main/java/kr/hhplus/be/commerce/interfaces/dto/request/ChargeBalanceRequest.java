package kr.hhplus.be.commerce.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record ChargeBalanceRequest(
        @NotNull(message = "사용자 ID는 필수값입니다.")
        @Positive(message = "유효하지 않은 사용자 ID입니다.")
        @Schema(description = "사용자 ID")
        Long userId,
        @NotNull(message = "충전 금액은 필수값입니다.")
        @Positive(message = "충전 금액이 유효하지 않습니다.")
        @Schema(description = "충전 금액")
        Long amount
) {
}
