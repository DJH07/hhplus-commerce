package kr.hhplus.be.commerce.infra.order;

import kr.hhplus.be.commerce.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
}
