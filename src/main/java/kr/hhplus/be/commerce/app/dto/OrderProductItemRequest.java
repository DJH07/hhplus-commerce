package kr.hhplus.be.commerce.app.dto;

import lombok.Builder;

@Builder
public record OrderProductItemRequest(
        Long productId,
        Long quantity
) {
}
