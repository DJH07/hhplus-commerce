package kr.hhplus.be.commerce.app.dto;

import lombok.Builder;

@Builder
public record OrderProductItemCommand(
        Long productId,
        Long quantity
) {
}
