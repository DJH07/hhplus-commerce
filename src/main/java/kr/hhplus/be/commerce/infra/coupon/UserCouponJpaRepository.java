package kr.hhplus.be.commerce.infra.coupon;

import kr.hhplus.be.commerce.domain.coupon.CouponResult;
import kr.hhplus.be.commerce.domain.coupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

    @Query("select " +
            "new kr.hhplus.be.commerce.domain.coupon.CouponResult(" +
            "c.couponId, " +
            "c.couponName, " +
            "c.description, " +
            "c.startDate, " +
            "c.endDate, " +
            "uc.issuedAt, " +
            "uc.status" +
            ")" +
            "from UserCoupon uc " +
            "join Coupon c on uc.couponId = c.couponId " +
            "where uc.userId = :userId " +
            "order by uc.issuedAt desc")
    List<CouponResult> findAllByUserId(@Param("userId") Long userId);
}
