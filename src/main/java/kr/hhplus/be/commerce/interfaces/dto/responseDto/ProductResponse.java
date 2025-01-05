package kr.hhplus.be.commerce.interfaces.dto.responseDto;

import lombok.Builder;

@Builder
public record ProductResponse(
        Long productId,
        String name,
        Integer price,
        String description,
        Integer stock
) {
}
