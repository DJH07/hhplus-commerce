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
import static org.mockito.Mockito.*;

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
    @DisplayName("유효한 쿠폰이 성공적으로 적용된 경우 할인 금액 반환")
    void applyUserCoupon_ShouldReturnDiscountedAmount_WhenCouponIsValid() {
        // given
        final Long userCouponId = 1L;
        final Long amount = 10000L;

        UserCoupon userCoupon = mock(UserCoupon.class);
        when(userCoupon.getCouponId()).thenReturn(2L);
        when(userCoupon.getStatus()).thenReturn(UserCouponStatus.ISSUED);

        Coupon coupon = mock(Coupon.class);
        when(coupon.getDiscountType()).thenReturn(DiscountType.AMOUNT);
        when(coupon.getDiscountValue()).thenReturn(2000L);
        when(coupon.getMinOrderAmount()).thenReturn(5000L);
        when(coupon.getStartDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(coupon.getEndDate()).thenReturn(LocalDateTime.now().plusDays(1));

        when(userCouponRepository.findById(userCouponId)).thenReturn(userCoupon);
        when(couponRepository.findById(2L)).thenReturn(coupon);

        // when
        Long discountedAmount = couponService.applyUserCoupon(userCouponId, amount);

        // then
        assertEquals(8000L, discountedAmount);
        verify(userCoupon).changeStatus(UserCouponStatus.USED);
    }

    @Test
    @DisplayName("쿠폰이 이미 사용된 경우 COUPON_ALREADY_USED 예외 발생")
    void applyUserCoupon_ShouldThrowException_WhenCouponAlreadyUsed() {
        // given
        final Long userCouponId = 1L;
        final Long amount = 10000L;

        UserCoupon userCoupon = mock(UserCoupon.class);
        when(userCoupon.getStatus()).thenReturn(UserCouponStatus.USED);
        when(userCouponRepository.findById(userCouponId)).thenReturn(userCoupon);

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> couponService.applyUserCoupon(userCouponId, amount));

        // then
        assertEquals(BusinessErrorCode.COUPON_ALREADY_USED, exception.getErrorCode());
    }

    @Test
    @DisplayName("최소 주문 금액 조건을 만족하지 않는 경우 COUPON_MIN_ORDER_AMOUNT_NOT_MET 예외 발생")
    void applyUserCoupon_ShouldThrowException_WhenMinOrderAmountNotMet() {
        // given
        final Long userCouponId = 1L;
        final Long amount = 5000L;

        UserCoupon userCoupon = mock(UserCoupon.class);
        when(userCoupon.getCouponId()).thenReturn(2L);
        when(userCoupon.getStatus()).thenReturn(UserCouponStatus.ISSUED);

        Coupon coupon = mock(Coupon.class);
        when(coupon.getMinOrderAmount()).thenReturn(10000L);

        when(userCouponRepository.findById(userCouponId)).thenReturn(userCoupon);
        when(couponRepository.findById(2L)).thenReturn(coupon);

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> couponService.applyUserCoupon(userCouponId, amount));

        // then
        assertEquals(BusinessErrorCode.COUPON_MIN_ORDER_AMOUNT_NOT_MET, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰이 아직 활성화되지 않은 경우 COUPON_NOT_YET_ACTIVE 예외 발생")
    void applyUserCoupon_ShouldThrowException_WhenCouponNotYetActive() {
        // given
        final Long userCouponId = 1L;
        final Long amount = 10000L;

        UserCoupon userCoupon = mock(UserCoupon.class);
        when(userCoupon.getCouponId()).thenReturn(2L);
        when(userCoupon.getStatus()).thenReturn(UserCouponStatus.ISSUED);

        Coupon coupon = mock(Coupon.class);
        when(coupon.getStartDate()).thenReturn(LocalDateTime.now().plusDays(1));

        when(userCouponRepository.findById(userCouponId)).thenReturn(userCoupon);
        when(couponRepository.findById(2L)).thenReturn(coupon);

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> couponService.applyUserCoupon(userCouponId, amount));

        // then
        assertEquals(BusinessErrorCode.COUPON_NOT_YET_ACTIVE, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰이 만료된 경우 COUPON_EXPIRED 예외 발생")
    void validateCouponExpiration_ShouldThrowException_WhenCouponExpired() {
        // given
        final Long couponId = 1L;

        Coupon coupon = mock(Coupon.class);
        when(coupon.getEndDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(couponRepository.findById(couponId)).thenReturn(coupon);

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> couponService.validateCouponExpiration(couponId));

        // then
        assertEquals(BusinessErrorCode.COUPON_EXPIRED, exception.getErrorCode());
    }

    @Test
    @DisplayName("사용자가 이미 해당 쿠폰을 발급받은 경우 COUPON_ALREADY_ISSUED 예외 발생")
    void validateUserCouponIssued_ShouldThrowException_WhenCouponAlreadyIssued() {
        // given
        final Long userId = 1L;
        final Long couponId = 2L;

        when(userCouponRepository.existsByUserIdAndCouponId(userId, couponId)).thenReturn(true);

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> couponService.validateUserCouponIssued(userId, couponId));

        // then
        assertEquals(BusinessErrorCode.COUPON_ALREADY_ISSUED, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰 재고가 부족한 경우 OUT_OF_COUPONS 예외 발생")
    void updateCouponQuantity_ShouldThrowException_WhenOutOfCoupons() {
        // given
        final Long couponId = 1L;
        CouponQuantity couponQuantity = mock(CouponQuantity.class);
        when(couponQuantity.getRemainingQuantity()).thenReturn(0L);
        when(couponQuantityRepository.findByIdWithLock(couponId)).thenReturn(couponQuantity);

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> couponService.updateCouponQuantity(couponId));

        // then
        assertEquals(BusinessErrorCode.OUT_OF_COUPONS, exception.getErrorCode());
    }

}
