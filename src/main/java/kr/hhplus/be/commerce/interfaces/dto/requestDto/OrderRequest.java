package kr.hhplus.be.commerce.interfaces.dto.requestDto;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderRequest(
        Long userId,
        List<OrderItemRequest> items,
        Long userCouponId
) {
}
