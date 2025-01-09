package kr.hhplus.be.commerce.infra.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.commerce.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
