package kr.hhplus.be.commerce.domain.product;

import lombok.Builder;

@Builder
public record OrderProductItemResult(
        Long productId,
        Long quantity,
        Long totalPrice
) {
}
