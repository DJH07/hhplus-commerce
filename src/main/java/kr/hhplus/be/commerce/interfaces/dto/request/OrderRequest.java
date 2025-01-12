package kr.hhplus.be.commerce.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kr.hhplus.be.commerce.app.dto.OrderProductItemRequest;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderRequest(
        @NotNull(message = "사용자 ID는 필수값입니다.")
        @Positive(message = "유효하지 않은 사용자 ID입니다.")
        @Schema(description = "사용자 ID")
        Long userId,
        @NotNull(message = "주문 상품 목록은 필수값입니다.")
        @Size(min = 1, message = "주문 상품은 최소 1개 이상이어야 합니다.")
        @Valid
        List<OrderProductItemRequest> items
) {
}
