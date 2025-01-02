package kr.hhplus.be.commerce.domain.coupon;

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
@Table(name = "balance")
public class Coupon extends AuditingFields implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false)
    @Comment("쿠폰 ID")
    private Long couponId;

    @Column(name = "coupon_code", nullable = false, unique = true, length = 100)
    @Comment("유니크 쿠폰 코드")
    private String couponCode;

    @Column(name = "discount_type", nullable = false)
    @Comment("할인 유형")
    private String discountType;

    @Column(name = "discount_value", nullable = false)
    @Comment("할인 값 (퍼센트 또는 고정 금액)")
    private Integer discountValue;

    @Column(name = "min_order_amount", nullable = false)
    @Comment("최소 주문 금액")
    private Integer minOrderAmount;

    @Column(name = "start_date", nullable = false)
    @Comment("쿠폰 활성화 시작 날짜")
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @Comment("쿠폰 만료 날짜")
    private LocalDateTime endDate;

    @Column(name = "max_issued", nullable = false)
    @Comment("최대 발급 가능 수")
    private Integer maxIssued;

    public static Coupon create(
            String couponCode,
            String discountType,
            Integer discountValue,
            Integer minOrderAmount,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Integer maxIssued
    ) {
        Coupon entity = new Coupon();
        entity.couponCode = couponCode;
        entity.discountType = discountType;
        entity.discountValue = discountValue;
        entity.minOrderAmount = minOrderAmount;
        entity.startDate = startDate;
        entity.endDate = endDate;
        entity.maxIssued = maxIssued;
        return entity;
    }
}