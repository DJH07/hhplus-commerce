package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.domain.coupon.Coupon;
import kr.hhplus.be.commerce.domain.coupon.CouponQuantity;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.utils.IntegrationTest;
import kr.hhplus.be.commerce.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

class CouponConcurrencyIntegrationTest extends IntegrationTest {
    @Autowired
    private CouponFacade couponFacade;

    @Test
    @DisplayName("쿠폰 발급 동시성 테스트")
    void issueCoupon_ConcurrentRequests_ShouldHandleCorrectly() throws InterruptedException {
        // given
        Coupon coupon = TestUtils.createTestCoupon(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10), 10L);
        couponJpaRepository.save(coupon);

        CouponQuantity couponQuantity = CouponQuantity.create(coupon.getCouponId(), 10L);
        couponQuantityJpaRepository.save(couponQuantity);

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        List<Future<Long>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    User user = userJpaRepository.save(TestUtils.createTestUser());
                    return couponFacade.issueCoupon(user.getUserId(), coupon.getCouponId());
                } finally {
                    latch.countDown();
                }
            }));
        }

        latch.await();
        executorService.shutdown();

        // then
        List<Long> issuedCoupons = new ArrayList<>();
        for (Future<Long> future : futures) {
            try {
                issuedCoupons.add(future.get());
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof BusinessException businessException) {
                    Assertions.assertEquals(BusinessErrorCode.OUT_OF_COUPONS, businessException.getErrorCode());
                } else {
                    throw new RuntimeException(cause);
                }
            }
        }

        long successfulIssues = issuedCoupons.stream().filter(Objects::nonNull).count();
        Assertions.assertEquals(10, successfulIssues);

        Coupon updatedCoupon = couponJpaRepository.findById(coupon.getCouponId()).orElseThrow();
        Assertions.assertEquals(0L, updatedCoupon.getCouponQuantity());
    }

}
