package kr.hhplus.be.commerce.infra.order;

import kr.hhplus.be.commerce.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
