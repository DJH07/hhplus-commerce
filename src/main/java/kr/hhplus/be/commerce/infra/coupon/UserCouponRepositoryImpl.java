package kr.hhplus.be.commerce.infra.coupon;

import kr.hhplus.be.commerce.domain.coupon.CouponResult;
import kr.hhplus.be.commerce.domain.coupon.UserCoupon;
import kr.hhplus.be.commerce.domain.coupon.UserCouponRepository;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private UserCouponJpaRepository userCouponJpaRepository;

    @Override
    public UserCoupon findById(Long userCouponId) {
        return userCouponJpaRepository.findById(userCouponId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_COUPON_NOT_FOUND));
    }

    @Override
    public Long save(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon).getCouponId();
    }

    @Override
    public List<CouponResult> findAllByUserId(Long userId) {
        return userCouponJpaRepository.findAllByUserId(userId);
    }
}
