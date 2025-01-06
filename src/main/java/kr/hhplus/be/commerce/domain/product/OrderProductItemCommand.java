package kr.hhplus.be.commerce.domain.product;

import lombok.Builder;

@Builder
public record OrderProductItemCommand(
        Long productId,
        Long quantity
) {
}
