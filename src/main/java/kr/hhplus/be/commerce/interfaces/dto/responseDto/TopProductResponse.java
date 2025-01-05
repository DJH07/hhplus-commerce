package kr.hhplus.be.commerce.interfaces.dto.responseDto;

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
