package kr.hhplus.be.commerce.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record OrderProductItemRequest(
        @NotNull(message = "상품 ID는 필수값입니다.")
        @Positive(message = "유효하지 않은 상품 ID입니다.")
        @Schema(description = "상품 ID")
        Long productId,
        @NotNull(message = "상품 수량은 필수값입니다.")
        @Positive(message = "유효하지 않은 상품 수량입니다.")
        @Schema(description = "상품 수량")
        Long quantity
) {
}
