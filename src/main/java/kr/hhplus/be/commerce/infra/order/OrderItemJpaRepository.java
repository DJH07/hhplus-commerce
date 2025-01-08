package kr.hhplus.be.commerce.infra.order;

import kr.hhplus.be.commerce.domain.order.OrderItem;
import kr.hhplus.be.commerce.domain.order.OrderItemResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {

    @Query("select " +
            "new kr.hhplus.be.commerce.domain.order.OrderItemResult( " +
            "oi.productId, " +
            "oi.quantity, " +
            "oi.price " +
            ") " +
            "from OrderItem oi " +
            "where oi.orderId = :orderId")
    List<OrderItemResult> findOrderItemResultByOrderId(@Param("orderId") Long orderId);
}
