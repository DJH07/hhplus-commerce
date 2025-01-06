package kr.hhplus.be.commerce.infra.order;

import kr.hhplus.be.commerce.domain.order.Order;
import kr.hhplus.be.commerce.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Long save(Order order) {
        return orderJpaRepository.save(order).getOrderId();
    }
}
