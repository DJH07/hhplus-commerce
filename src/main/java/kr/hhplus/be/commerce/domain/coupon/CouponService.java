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

        validateUserCoupon(coupon, userCoupon.getStatus(), amount);

        userCoupon.changeStatus(UserCouponStatus.USED);

        return calculateCoupon(amount, coupon.getDiscountType(), coupon.getDiscountValue());
    }

    public Long calculateCoupon(Long amount, DiscountType discountType, Long discountValue) {
        return switch (discountType) {
            case PERCENTAGE -> amount * (100 - discountValue) / 100;
            case AMOUNT -> amount - discountValue;
        };
    }

    private void validateUserCoupon(Coupon coupon, UserCouponStatus userCouponStatus, Long amount) {

        // 쿠폰 가능 조건 검사
        if(userCouponStatus.equals(UserCouponStatus.USED)) {
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

    @Transactional
    public Long processIssueCoupon(Long userId, Long couponId) {
        validateCouponExpiration(couponId);

        validateUserCouponIssued(userId, couponId);

        Long decrementedQuantity = updateCouponQuantity(couponId);

        decreaseCouponQuantity(couponId, decrementedQuantity);

        return issueUserCoupon(userId, couponId);
    }

    public void validateCouponExpiration(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId);
        if(coupon.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(BusinessErrorCode.COUPON_EXPIRED);
        }
    }

    public void validateUserCouponIssued(Long userId, Long couponId) {
        if(userCouponRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw new BusinessException(BusinessErrorCode.COUPON_ALREADY_ISSUED);
        }
    }

    public Long updateCouponQuantity(Long couponId) {
        CouponQuantity couponQuantity = couponQuantityRepository.findByIdWithLock(couponId);
        long decrementedQuantity = couponQuantity.getRemainingQuantity() - 1;
        if(decrementedQuantity < 0) {
            throw new BusinessException(BusinessErrorCode.OUT_OF_COUPONS);
        }
        couponQuantity.changeRemainingQuantity(decrementedQuantity);
        return decrementedQuantity;
    }

    public void decreaseCouponQuantity(Long couponId, Long decrementedQuantity) {
        Coupon coupon = couponRepository.findById(couponId);
        coupon.changeCouponQuantity(decrementedQuantity);
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
