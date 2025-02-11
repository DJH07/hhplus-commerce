package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.app.dto.CouponResponse;
import kr.hhplus.be.commerce.domain.coupon.Coupon;
import kr.hhplus.be.commerce.domain.coupon.UserCoupon;
import kr.hhplus.be.commerce.domain.coupon.UserCouponStatus;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.utils.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static kr.hhplus.be.commerce.utils.TestUtils.createTestCoupon;
import static kr.hhplus.be.commerce.utils.TestUtils.createTestUser;

class CouponFacadeIntegrationTest extends IntegrationTest {
    @Autowired
    private CouponFacade couponFacade;

    @Test
    @DisplayName("사용자에게 쿠폰이 정상 발급되고 조회")
    void getUserCouponList_ShouldReturnCoupons_WhenSuccessful() {
        // given
        User user = userJpaRepository.save(createTestUser());

        int couponCount = 2;
        List<Coupon> coupons = new ArrayList<>();

        for (int i = 0; i < couponCount; i++) {
            Coupon coupon = createTestCoupon(i + 1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10), 100L);
            couponJpaRepository.save(coupon);
            coupons.add(coupon);

            UserCoupon userCoupon = UserCoupon.create(user.getUserId(), coupon.getCouponId(), UserCouponStatus.ISSUED, LocalDateTime.now().minusDays(1));
            userCouponJpaRepository.save(userCoupon);
        }

        // when
        List<CouponResponse> couponResponses = couponFacade.getUserCouponList(user.getUserId());

        // then
        Assertions.assertNotNull(couponResponses);
        Assertions.assertEquals(couponCount, couponResponses.size());

        for (int i = 0; i < couponCount; i++) {
            Coupon coupon = coupons.get(i);
            CouponResponse couponResponse = couponResponses.get(couponCount - i - 1);

            Assertions.assertEquals(coupon.getCouponId(), couponResponse.couponId());
            Assertions.assertEquals(coupon.getCouponName(), couponResponse.couponName());
            Assertions.assertEquals(coupon.getDescription(), couponResponse.description());
        }
    }

}
