package kr.hhplus.be.commerce.app.dto;

import lombok.Builder;

@Builder
public record TopProductResponse(
        Long productId,
        Integer rank,
        String name,
        Long price,
        String description,
        Long soldQuantity
) {
}
