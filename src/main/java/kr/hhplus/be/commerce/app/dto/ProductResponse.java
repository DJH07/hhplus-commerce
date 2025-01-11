package kr.hhplus.be.commerce.app.dto;

import lombok.Builder;

@Builder
public record ProductResponse(
        Long productId,
        String name,
        Long price,
        String description,
        Long stock
) {
}
