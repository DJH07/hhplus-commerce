package kr.hhplus.be.commerce.domain.coupon;

import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public Long applyCoupon(Long userCouponId, Long amount) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId);
        Coupon coupon = userCoupon.getCoupon();

        // 쿠폰 가능 조건 검사
        if(userCoupon.getStatus().equals(UserCouponStatus.USED)) {
            throw new BusinessException(BusinessErrorCode.COUPON_ALREADY_USED);
        }
        if(coupon.getMinOrderAmount() > amount) {
            throw new BusinessException(BusinessErrorCode.COUPON_MIN_ORDER_AMOUNT_NOT_MET);
        }
        LocalDateTime currentTime = LocalDateTime.now();
        if(coupon.getStartDate().isAfter(currentTime)) {
            throw new BusinessException(BusinessErrorCode.COUPON_NOT_YET_ACTIVE);
        } else if(coupon.getEndDate().isBefore(currentTime)) {
            throw new BusinessException(BusinessErrorCode.COUPON_EXPIRED);
        }

        // 쿠폰 연산
        return switch (coupon.getDiscountType()) {
            case PERCENTAGE -> amount * (100 - coupon.getDiscountValue()) / 100;
            case AMOUNT -> amount - coupon.getDiscountValue();
        };
    }
}
