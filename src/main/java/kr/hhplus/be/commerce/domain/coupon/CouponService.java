package kr.hhplus.be.commerce.domain.coupon;

import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponRedisRepository couponRedisRepository;
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

    public void requestIssueCoupon(Long userId, Long couponId) {

        validateCouponExpiration(couponId);

        enqueueCouponRequest(userId, couponId);
    }

    void enqueueCouponRequest(Long userId, Long couponId) {
        couponRedisRepository.enqueueCouponRequest(userId, couponId);
    }

    public Long dequeueCouponRequest(Long couponId) {
        return couponRedisRepository.dequeueCouponRequest(couponId);
    }

    public List<Long> getAllCouponIds() {
        return couponRedisRepository.getAllCouponIds();
    }

    public void validateCouponExpiration(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId);
        if(coupon.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(BusinessErrorCode.COUPON_EXPIRED);
        }
    }

    public boolean tryIssueCoupon(Long userId, Long couponId, Long maxIssued) {
        Long issuedCount = couponRedisRepository.getIssuedCouponCount(couponId);

        if (issuedCount > maxIssued) {
            return false;
        }

        couponRedisRepository.markCouponAsIssued(userId, couponId);
        return true;
    }


    public Long getIssuedCouponCount(Long couponId) {
        return couponRedisRepository.getIssuedCouponCount(couponId);
    }

    public Long getMaxIssued(Long couponId) {
        return couponRepository.findById(couponId).getMaxIssued();
    }

    public void insertUserCouponList(List<Long> userIds, Long couponId) {
        List<UserCoupon> userCoupons = userIds.stream()
                .map(userId -> UserCoupon.create(userId, couponId, UserCouponStatus.ISSUED, LocalDateTime.now()))
                .toList();

        userCouponRepository.saveAll(userCoupons);
    }

    public List<CouponResult> getUserCouponList(Long userId) {
        return userCouponRepository.findAllByUserId(userId);
    }

}
