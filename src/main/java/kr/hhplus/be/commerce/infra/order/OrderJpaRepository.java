package kr.hhplus.be.commerce.infra.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.commerce.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
