package kr.hhplus.be.commerce.interfaces.dto.responseDto;

import lombok.Builder;

@Builder
public record TopProductResponse(
        Long id,
        String name,
        Integer price,
        String description,
        Integer soldQuantity
) {
}
