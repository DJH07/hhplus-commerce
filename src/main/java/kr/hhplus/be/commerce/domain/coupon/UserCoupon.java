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
@Table(name = "user_coupon")
public class UserCoupon extends AuditingFields implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id", nullable = false)
    @Comment("사용자 쿠폰 ID")
    private Long userCouponId;

    @Column(name = "user_id", nullable = false)
    @Comment("사용자 ID")
    private Long userId;

    @Column(name = "coupon_id", nullable = false)
    @Comment("쿠폰 ID")
    private Long couponId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("쿠폰 상태 (ISSUED, USED)")
    private UserCouponStatus status;

    @Column(name = "issued_at")
    @Comment("쿠폰 발급 일시")
    private LocalDateTime issuedAt;

    @Column(name = "used_at", nullable = true)
    @Comment("쿠폰 사용 일시")
    private LocalDateTime usedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", referencedColumnName = "coupon_id", insertable = false, updatable = false)
    @Comment("쿠폰")
    private Coupon coupon;

    public static UserCoupon create(
            Long userId,
            Long couponId,
            UserCouponStatus status,
            LocalDateTime issuedAt
    ) {
        UserCoupon entity = new UserCoupon();
        entity.userId = userId;
        entity.couponId = couponId;
        entity.status = status;
        entity.issuedAt = issuedAt;
        return entity;
    }
}
