package kr.hhplus.be.commerce.interfaces.dto.requestDto;

import lombok.Builder;

@Builder
public record OrderItemRequest(
        Long productId,
        Integer quantity
) {
}
