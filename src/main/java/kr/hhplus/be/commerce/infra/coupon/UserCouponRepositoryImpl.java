package kr.hhplus.be.commerce.infra.coupon;

import kr.hhplus.be.commerce.domain.coupon.UserCoupon;
import kr.hhplus.be.commerce.domain.coupon.UserCouponRepository;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private UserCouponJpaRepository userCouponJpaRepository;

    @Override
    public UserCoupon findById(Long userCouponId) {
        return userCouponJpaRepository.findById(userCouponId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_COUPON_NOT_FOUND));
    }
}
