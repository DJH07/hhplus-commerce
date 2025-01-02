package kr.hhplus.be.commerce.infra.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.commerce.domain.coupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {
}
