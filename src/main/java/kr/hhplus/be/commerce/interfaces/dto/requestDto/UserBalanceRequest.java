package kr.hhplus.be.commerce.interfaces.dto.requestDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record UserBalanceRequest(
        @NotNull(message = "사용자 ID는 필수값입니다.")
        @Positive(message = "유효하지 않은 사용자 ID입니다.")
        Long userId
) {
}
