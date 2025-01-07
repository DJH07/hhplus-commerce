package kr.hhplus.be.commerce.infra.coupon;

import jakarta.persistence.LockTimeoutException;
import kr.hhplus.be.commerce.domain.coupon.CouponQuantity;
import kr.hhplus.be.commerce.domain.coupon.CouponQuantityRepository;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponQuantityRepositoryImpl implements CouponQuantityRepository {

    private final CouponQuantityJpaRepository couponQuantityJpaRepository;

    @Override
    public CouponQuantity findByIdWithLock(Long couponId) {
        try {
            return couponQuantityJpaRepository.findByCouponIdWithLock(couponId)
                    .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND_COUPON_QUANTITY));
        } catch (LockTimeoutException e) {
            throw new BusinessException(BusinessErrorCode.LOCK_TIMEOUT);
        }
    }
}
