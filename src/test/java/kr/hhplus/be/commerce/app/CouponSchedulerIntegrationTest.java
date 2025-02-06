package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.domain.coupon.Coupon;
import kr.hhplus.be.commerce.domain.coupon.CouponRedisRepository;
import kr.hhplus.be.commerce.domain.coupon.CouponResult;
import kr.hhplus.be.commerce.domain.coupon.UserCouponStatus;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.utils.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.commerce.utils.TestUtils.createTestCoupon;
import static kr.hhplus.be.commerce.utils.TestUtils.createTestUser;

class CouponSchedulerIntegrationTest extends IntegrationTest {
    @Autowired
    private CouponScheduler couponScheduler;

    @Autowired
    private CouponRedisRepository couponRedisRepository;

    @Test
    @DisplayName("사용자에게 쿠폰이 정상 발급되고 조회")
    void testProcessCouponQueue() {
        // given
        User user = userJpaRepository.save(createTestUser());
        Coupon coupon = couponJpaRepository.save(createTestCoupon(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10), 100L));

        couponRedisRepository.enqueueCouponRequest(user.getUserId(), coupon.getCouponId());

        // when
        couponScheduler.processCouponQueue();

        // then
        List<CouponResult> issuedCoupons = userCouponJpaRepository.findAllByUserId(user.getUserId());

        Assertions.assertNotNull(issuedCoupons);
        Assertions.assertEquals(1, issuedCoupons.size());

        CouponResult issuedCoupon = issuedCoupons.get(0);
        Assertions.assertEquals(coupon.getCouponId(), issuedCoupon.couponId());
        Assertions.assertEquals(UserCouponStatus.ISSUED, issuedCoupon.status());
    }

}
