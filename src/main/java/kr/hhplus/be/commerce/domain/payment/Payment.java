package kr.hhplus.be.commerce.domain.payment;

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
@Table(name = "payment")
public class Payment extends AuditingFields implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false)
    @Comment("결제 ID")
    private Long paymentId;

    @Column(name = "order_id", nullable = false)
    @Comment("주문 ID")
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("결제 상태: SUCCESS, FAILURE")
    private PaymentStatus status;

    @Column(name = "payment_amount", nullable = false)
    @Comment("결제 금액")
    private Long paymentAmount;

    @Column(name = "payment_date", nullable = false)
    @Comment("결제 처리 날짜")
    private LocalDateTime paymentDate;

    @Column(name = "failure_reason")
    @Comment("결제 실패 사유")
    private String failureReason;

    public static Payment create(Long orderId, Long paymentAmount, PaymentStatus status, LocalDateTime paymentDate, String failureReason) {
        Payment entity = new Payment();
        entity.orderId = orderId;
        entity.paymentAmount = paymentAmount;
        entity.status = status;
        entity.paymentDate = paymentDate;
        entity.failureReason = failureReason;
        return entity;
    }

}
