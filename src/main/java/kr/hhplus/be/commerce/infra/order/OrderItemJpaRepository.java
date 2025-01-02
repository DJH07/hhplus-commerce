package kr.hhplus.be.commerce.infra.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.commerce.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
}
