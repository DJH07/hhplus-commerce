package kr.hhplus.be.commerce.infra.order;

import kr.hhplus.be.commerce.domain.order.OrderItem;
import kr.hhplus.be.commerce.domain.order.OrderItemRepository;
import kr.hhplus.be.commerce.domain.order.OrderItemResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public void saveAll(List<OrderItem> orderItemList) {
        orderItemJpaRepository.saveAll(orderItemList);
    }

    @Override
    public List<OrderItemResult> findAllByOrderId(Long orderId) {
        return orderItemJpaRepository.findOrderItemResultByOrderId(orderId);
    }
}
