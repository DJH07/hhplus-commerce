package kr.hhplus.be.commerce.domain.order;

import lombok.Builder;

@Builder
public record OrderItemResult(
        Long productId,
        Long quantity,
        Long totalPrice
) {
}
