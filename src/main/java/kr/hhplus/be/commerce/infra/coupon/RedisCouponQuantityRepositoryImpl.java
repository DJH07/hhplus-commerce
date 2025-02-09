package kr.hhplus.be.commerce.infra.coupon;

import kr.hhplus.be.commerce.domain.coupon.CouponQuantity;
import kr.hhplus.be.commerce.domain.coupon.CouponQuantityRepository;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisCouponQuantityRepositoryImpl implements CouponQuantityRepository {


    private final CouponQuantityJpaRepository couponQuantityJpaRepository;

    @Override
    public CouponQuantity findByIdWithLock(Long couponId) {
        return couponQuantityJpaRepository.findByCouponId(couponId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.COUPON_QUANTITY_NOT_FOUND));
    }

}
