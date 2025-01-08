package kr.hhplus.be.commerce.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.commerce.domain.utils.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "order")
public class Order extends AuditingFields implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    @Comment("주문 ID")
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    @Comment("사용자 ID")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("주문 상태: PENDING, PAID, CANCELED")
    private OrderStatus status;

    @Column(name = "order_total_amount", nullable = false)
    @Comment("총 주문 금액 (쿠폰 적용 이전가)")
    private Long orderTotalAmount;

    @Column(name = "pay_total_amount")
    @Comment("총 결제 금액 (쿠폰 적용가)")
    private Long payTotalAmount;

    @Column(name = "order_date", nullable = false)
    @Comment("주문 날짜")
    private LocalDateTime orderDate;

    @Column(name = "pay_date")
    @Comment("결제 날짜")
    private LocalDateTime payDate;


    public static Order create(Long userId, Long orderTotalAmount, LocalDateTime orderDate) {
        Order entity = new Order();
        entity.userId = userId;
        entity.status = OrderStatus.PENDING;
        entity.orderTotalAmount = orderTotalAmount;
        entity.orderDate = orderDate;
        return entity;
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

    public void changePayTotalAmount(Long payTotalAmount) {
        this.payTotalAmount = payTotalAmount;
    }

    public void changePayDate(LocalDateTime payDate) {
        this.payDate = payDate;
    }

}
