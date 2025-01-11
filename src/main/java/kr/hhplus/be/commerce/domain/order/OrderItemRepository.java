package kr.hhplus.be.commerce.domain.order;

import java.util.List;

public interface OrderItemRepository {

    void saveAll(List<OrderItem> orderItemList);

    List<OrderItemResult> findAllByOrderId(Long orderId);
}
