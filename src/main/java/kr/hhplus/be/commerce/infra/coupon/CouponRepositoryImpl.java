package kr.hhplus.be.commerce.infra.coupon;

import kr.hhplus.be.commerce.domain.coupon.Coupon;
import kr.hhplus.be.commerce.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {


    @Override
    public Coupon findById(Long couponId) {
        return null;
    }
}
