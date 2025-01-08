package kr.hhplus.be.commerce.domain.coupon;

import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponQuantityRepository couponQuantityRepository;
    private final UserCouponRepository userCouponRepository;

    public Long applyUserCoupon(Long userCouponId, Long amount) {
        if(userCouponId == null) {
            return amount;
        }

        UserCoupon userCoupon = userCouponRepository.findById(userCouponId);
        Coupon coupon = couponRepository.findById(userCoupon.getCouponId());
        userCoupon.changeStatus(UserCouponStatus.USED);

        validateUserCoupon(userCoupon, coupon, amount);

        // 쿠폰 연산
        return switch (coupon.getDiscountType()) {
            case PERCENTAGE -> amount * (100 - coupon.getDiscountValue()) / 100;
            case AMOUNT -> amount - coupon.getDiscountValue();
        };
    }

    private void validateUserCoupon(UserCoupon userCoupon, Coupon coupon, Long amount) {

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

    }


    public void validateCouponExpiration(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId);
        if(coupon.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(BusinessErrorCode.COUPON_EXPIRED);
        }
    }

    @Transactional
    public void updateCouponQuantity(Long couponId) {
        CouponQuantity couponQuantity = couponQuantityRepository.findByIdWithLock(couponId);
        long decrementedQuantity = couponQuantity.getRemainingQuantity() - 1;
        if(decrementedQuantity < 0) {
            throw new BusinessException(BusinessErrorCode.OUT_OF_COUPONS);
        }
        couponQuantity.changeRemainingQuantity(decrementedQuantity);
    }

    public Long issueUserCoupon(Long userId, Long couponId) {
        UserCoupon userCoupon = UserCoupon.create(
                userId,
                couponId,
                UserCouponStatus.ISSUED,
                LocalDateTime.now());
        return userCouponRepository.save(userCoupon);
    }

    public List<CouponResult> getUserCouponList(Long userId) {
        return userCouponRepository.findAllByUserId(userId);
    }

}
