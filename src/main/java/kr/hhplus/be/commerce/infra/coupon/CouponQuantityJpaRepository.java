package kr.hhplus.be.commerce.infra.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.commerce.domain.coupon.CouponQuantity;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface CouponQuantityJpaRepository extends JpaRepository<CouponQuantity, Long> {
}
