package kr.hhplus.be.commerce.domain;

import kr.hhplus.be.commerce.domain.coupon.*;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceUnitTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private CouponQuantityRepository couponQuantityRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰이 이미 사용된 경우 COUPON_ALREADY_USED 예외 발생")
    void applyCoupon_ShouldThrowException_WhenCouponAlreadyUsed() {
        // given
        final Long userCouponId = 1L;
        final Long amount = 10000L;

        UserCoupon userCoupon = mock(UserCoupon.class);
        when(userCoupon.getStatus()).thenReturn(UserCouponStatus.USED);
        when(userCouponRepository.findById(userCouponId)).thenReturn(userCoupon);

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> couponService.applyCoupon(userCouponId, amount));

        // then
        assertEquals(BusinessErrorCode.COUPON_ALREADY_USED, exception.getErrorCode());
    }

    @Test
    @DisplayName("최소 주문 금액 조건을 만족하지 않는 경우 COUPON_MIN_ORDER_AMOUNT_NOT_MET 예외 발생")
    void applyCoupon_ShouldThrowException_WhenMinOrderAmountNotMet() {
        // given
        final Long userCouponId = 1L;
        final Long amount = 5000L;

        Coupon coupon = mock(Coupon.class);
        when(coupon.getMinOrderAmount()).thenReturn(10000L);

        UserCoupon userCoupon = mock(UserCoupon.class);
        when(userCoupon.getStatus()).thenReturn(UserCouponStatus.ISSUED);
        when(userCoupon.getCoupon()).thenReturn(coupon);
        when(userCouponRepository.findById(userCouponId)).thenReturn(userCoupon);

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> couponService.applyCoupon(userCouponId, amount));

        // then
        assertEquals(BusinessErrorCode.COUPON_MIN_ORDER_AMOUNT_NOT_MET, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰이 아직 활성화되지 않은 경우 COUPON_NOT_YET_ACTIVE 예외 발생")
    void applyCoupon_ShouldThrowException_WhenCouponNotYetActive() {
        // given
        final Long userCouponId = 1L;
        final Long amount = 10000L;

        Coupon coupon = mock(Coupon.class);
        when(coupon.getStartDate()).thenReturn(LocalDateTime.now().plusDays(1));

        UserCoupon userCoupon = mock(UserCoupon.class);
        when(userCoupon.getStatus()).thenReturn(UserCouponStatus.ISSUED);
        when(userCoupon.getCoupon()).thenReturn(coupon);
        when(userCouponRepository.findById(userCouponId)).thenReturn(userCoupon);

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> couponService.applyCoupon(userCouponId, amount));

        // then
        assertEquals(BusinessErrorCode.COUPON_NOT_YET_ACTIVE, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰이 만료된 경우 COUPON_EXPIRED 예외 발생")
    void applyCoupon_ShouldThrowException_WhenCouponExpired() {
        // given
        final Long userCouponId = 1L;
        final Long amount = 10000L;

        Coupon coupon = mock(Coupon.class);
        when(coupon.getStartDate()).thenReturn(LocalDateTime.now().minusDays(10));
        when(coupon.getEndDate()).thenReturn(LocalDateTime.now().minusDays(1));

        UserCoupon userCoupon = mock(UserCoupon.class);
        when(userCoupon.getStatus()).thenReturn(UserCouponStatus.ISSUED);
        when(userCoupon.getCoupon()).thenReturn(coupon);
        when(userCouponRepository.findById(userCouponId)).thenReturn(userCoupon);

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> couponService.applyCoupon(userCouponId, amount));

        // then
        assertEquals(BusinessErrorCode.COUPON_EXPIRED, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰이 만료된 경우 COUPON_EXPIRED 예외 발생")
    void validateCouponExpiration_ShouldThrowException_WhenCouponExpired() {
        // given
        final Long couponId = 1L;
        Coupon coupon = mock(Coupon.class);
        when(coupon.getEndDate()).thenReturn(LocalDateTime.now().minusDays(1)); // 현재 시간보다 이전

        when(couponRepository.findById(couponId)).thenReturn(coupon);

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> couponService.validateCouponExpiration(couponId));

        // then
        assertEquals(BusinessErrorCode.COUPON_EXPIRED, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰 재고가 부족한 경우 OUT_OF_COUPONS 예외 발생")
    void updateCouponQuantity_ShouldThrowException_WhenOutOfCoupons() {
        // given
        final Long couponId = 1L;
        CouponQuantity couponQuantity = mock(CouponQuantity.class);
        when(couponQuantity.getRemainingQuantity()).thenReturn(0L); // 재고 0

        when(couponQuantityRepository.findByIdWithLock(couponId)).thenReturn(couponQuantity);

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> couponService.updateCouponQuantity(couponId));

        // then
        assertEquals(BusinessErrorCode.OUT_OF_COUPONS, exception.getErrorCode());
    }

}
