package kr.hhplus.be.commerce.utils;

import kr.hhplus.be.commerce.app.CouponFacade;
import kr.hhplus.be.commerce.domain.coupon.CouponQuantityRepository;
import kr.hhplus.be.commerce.domain.coupon.CouponRepository;
import kr.hhplus.be.commerce.domain.coupon.CouponService;
import kr.hhplus.be.commerce.domain.coupon.UserCouponRepository;
import kr.hhplus.be.commerce.domain.user.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CouponFacadeLockTestConfig {

    @Bean(name = "redisCouponService")
    public CouponService redisCouponService(
            CouponRepository couponRepository,
            @Qualifier("redisCouponQuantityRepository") CouponQuantityRepository couponQuantityRepository,
            UserCouponRepository userCouponRepository) {
        return new CouponService(couponRepository, couponQuantityRepository, userCouponRepository);
    }

    @Bean(name = "jpaCouponService")
    public CouponService jpaCouponService(
            CouponRepository couponRepository,
            @Qualifier("jpaCouponQuantityRepository") CouponQuantityRepository couponQuantityRepository,
            UserCouponRepository userCouponRepository) {
        return new CouponService(couponRepository, couponQuantityRepository, userCouponRepository);
    }

    @Bean(name = "redisCouponFacade")
    public CouponFacade redisCouponFacade(
            @Qualifier("redisCouponService") CouponService couponService,
            UserService userService) {
        return new CouponFacade(couponService, userService);
    }

    @Bean(name = "jpaCouponFacade")
    public CouponFacade jpaCouponFacade(
            @Qualifier("jpaCouponService") CouponService couponService,
            UserService userService) {
        return new CouponFacade(couponService, userService);
    }

}
