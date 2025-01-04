package kr.hhplus.be.commerce.infra.coupon;

import kr.hhplus.be.commerce.domain.coupon.CouponQuantity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponQuantityJpaRepository extends JpaRepository<CouponQuantity, Long> {
}
