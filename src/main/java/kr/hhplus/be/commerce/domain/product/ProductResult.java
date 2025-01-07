package kr.hhplus.be.commerce.domain.product;

import lombok.Builder;

@Builder
public record ProductResult(
        Long productId,
        String name,
        Long price,
        String description,
        Long stock
) {
}
