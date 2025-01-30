package kr.hhplus.be.commerce.infra.coupon;

import kr.hhplus.be.commerce.domain.coupon.CouponQuantity;
import kr.hhplus.be.commerce.domain.coupon.CouponQuantityRepository;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisCouponQuantityRepositoryImpl implements CouponQuantityRepository {


    private final CouponQuantityJpaRepository couponQuantityJpaRepository;

    @Override
    public Long updateCouponQuantityWithLock(Long couponId) {
        log.info("RedisCouponQuantityRepositoryImpl couponId : {}", couponId);

        CouponQuantity couponQuantity = findByCouponId(couponId);

        long decrementedQuantity = couponQuantity.getRemainingQuantity() - 1;

        if (decrementedQuantity < 0) {
            throw new BusinessException(BusinessErrorCode.OUT_OF_COUPONS);
        }

        couponQuantity.changeRemainingQuantity(decrementedQuantity);
        couponQuantityJpaRepository.save(couponQuantity);

        return decrementedQuantity;
    }

    private CouponQuantity findByCouponId(Long couponId) {
        return couponQuantityJpaRepository.findByCouponId(couponId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.COUPON_QUANTITY_NOT_FOUND));
    }
}
