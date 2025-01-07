package kr.hhplus.be.commerce.infra.coupon;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import kr.hhplus.be.commerce.domain.coupon.CouponQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponQuantityJpaRepository extends JpaRepository<CouponQuantity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "5000")
    })
    @Query("SELECT c FROM CouponQuantity c WHERE c.couponId = :couponId")
    Optional<CouponQuantity> findByCouponIdWithLock(@Param("couponId") Long couponId);
}
