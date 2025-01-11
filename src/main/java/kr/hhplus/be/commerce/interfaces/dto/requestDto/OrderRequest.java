package kr.hhplus.be.commerce.interfaces.dto.requestDto;

import kr.hhplus.be.commerce.app.dto.OrderProductItemRequest;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderRequest(
        Long userId,
        List<OrderProductItemRequest> items,
        Long userCouponId
) {
}
