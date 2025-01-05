package kr.hhplus.be.commerce.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.commerce.domain.utils.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "coupon_quantity")
public class CouponQuantity extends AuditingFields implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_quantity_id", nullable = false)
    @Comment("쿠폰 수량 ID")
    private Long couponQuantityId;

    @Column(name = "coupon_id", nullable = false)
    @Comment("쿠폰 ID")
    private Long couponId;

    @Column(name = "remaining_quantity", nullable = false)
    @Comment("남은 쿠폰 수량")
    private Long remainingQuantity;

    public static CouponQuantity create(Long couponId, Long remainingQuantity) {
        CouponQuantity entity = new CouponQuantity();
        entity.couponId = couponId;
        entity.remainingQuantity = remainingQuantity;
        return entity;
    }
}
