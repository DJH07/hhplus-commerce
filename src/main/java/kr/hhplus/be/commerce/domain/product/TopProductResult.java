package kr.hhplus.be.commerce.domain.product;

import lombok.Builder;

@Builder
public record TopProductResult(
        Long productId,
        String name,
        Long price,
        String description,
        Long soldQuantity
) {
}
