package kr.hhplus.be.commerce.domain.order;

import kr.hhplus.be.commerce.domain.product.OrderProductItemResult;

import java.util.List;

public record RegisterOrderCommand(Long userId,
                                   Long orderTotalAmount,
                                   List<OrderProductItemResult> orderProductItemResults) {
}
