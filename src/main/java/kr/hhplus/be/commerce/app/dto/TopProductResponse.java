package kr.hhplus.be.commerce.app.dto;

import lombok.Builder;

@Builder
public record TopProductResponse(
        Long id,
        String name,
        Long price,
        String description,
        Long soldQuantity
) {
}
