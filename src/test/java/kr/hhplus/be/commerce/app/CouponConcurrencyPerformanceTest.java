package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.domain.coupon.Coupon;
import kr.hhplus.be.commerce.domain.coupon.CouponQuantity;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.infra.coupon.CouponJpaRepository;
import kr.hhplus.be.commerce.infra.coupon.CouponQuantityJpaRepository;
import kr.hhplus.be.commerce.infra.coupon.UserCouponJpaRepository;
import kr.hhplus.be.commerce.infra.user.UserJpaRepository;
import kr.hhplus.be.commerce.utils.CouponFacadeLockTestConfig;
import kr.hhplus.be.commerce.utils.PerformanceMeasure;
import kr.hhplus.be.commerce.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

@Import(CouponFacadeLockTestConfig.class)
@SpringBootTest
@ActiveProfiles("test")
class CouponConcurrencyPerformanceTest {

    @Autowired
    @Qualifier("redisCouponFacade")
    private CouponFacade redisCouponFacade;

    @Autowired
    @Qualifier("jpaCouponFacade")
    private CouponFacade jpaCouponFacade;

    @Autowired
    protected UserJpaRepository userJpaRepository;
    @Autowired
    protected CouponJpaRepository couponJpaRepository;
    @Autowired
    protected CouponQuantityJpaRepository couponQuantityJpaRepository;
    @Autowired
    protected UserCouponJpaRepository userCouponJpaRepository;

    @BeforeEach
    void init() {
        userCouponJpaRepository.deleteAllInBatch();
        couponQuantityJpaRepository.deleteAllInBatch();
        couponJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("JPA 비관적 락 기반 쿠폰발급 동시성 제어 성능 측정")
    void measureJpaLockPerformance() {
        // given
        Coupon coupon = TestUtils.createTestCoupon(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10), 10L);
        couponJpaRepository.save(coupon);

        CouponQuantity couponQuantity = CouponQuantity.create(coupon.getCouponId(), 10L);
        couponQuantityJpaRepository.save(couponQuantity);

        int threadCount = 10;

        List<Future<Long>> futures = new ArrayList<>();

        PerformanceMeasure.measure("Redis Lock", () -> {
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                futures.add(executorService.submit(() -> {
                    try {
                        User user = userJpaRepository.save(TestUtils.createTestUser());
                        return jpaCouponFacade.issueCoupon(user.getUserId(), coupon.getCouponId());
                    } finally {
                        latch.countDown();
                    }
                }));
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executorService.shutdown();
            return null;
        });

        // then
        List<Long> issuedCoupons = new ArrayList<>();
        for (Future<Long> future : futures) {
            try {
                issuedCoupons.add(future.get());
            } catch (ExecutionException | InterruptedException e) {
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

    @Test
    @DisplayName("Redis 분산락 기반 쿠폰발급 동시성 제어 성능 측정")
    void measureRedisLockPerformance() {
        // given
        Coupon coupon = TestUtils.createTestCoupon(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10), 10L);
        couponJpaRepository.save(coupon);

        CouponQuantity couponQuantity = CouponQuantity.create(coupon.getCouponId(), 10L);
        couponQuantityJpaRepository.save(couponQuantity);

        int threadCount = 10;

        List<Future<Long>> futures = new ArrayList<>();

        PerformanceMeasure.measure("Redis Lock", () -> {
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                futures.add(executorService.submit(() -> {
                    try {
                        User user = userJpaRepository.save(TestUtils.createTestUser());
                        return redisCouponFacade.issueCoupon(user.getUserId(), coupon.getCouponId());
                    } finally {
                        latch.countDown();
                    }
                }));
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executorService.shutdown();
            return null;
        });

        // then
        List<Long> issuedCoupons = new ArrayList<>();
        for (Future<Long> future : futures) {
            try {
                issuedCoupons.add(future.get());
            } catch (ExecutionException | InterruptedException e) {
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
