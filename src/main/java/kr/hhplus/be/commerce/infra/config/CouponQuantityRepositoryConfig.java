package kr.hhplus.be.commerce.infra.config;

import kr.hhplus.be.commerce.domain.coupon.CouponQuantityRepository;
import kr.hhplus.be.commerce.infra.coupon.CouponQuantityJpaRepository;
import kr.hhplus.be.commerce.infra.coupon.CouponQuantityRepositoryImpl;
import kr.hhplus.be.commerce.infra.coupon.RedisCouponQuantityRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CouponQuantityRepositoryConfig {

    //TODO : 테스트를 위한 config로, 추후 적절한 코드만 남기고 제거 예정
    @Bean(name = "redisCouponQuantityRepository")
    @Primary // 기본으로 RedisCouponQuantityRepositoryImpl 사용
    public CouponQuantityRepository redisCouponQuantityRepository(
            CouponQuantityJpaRepository couponQuantityJpaRepository) {
        return new RedisCouponQuantityRepositoryImpl(couponQuantityJpaRepository);
    }

    @Bean(name = "jpaCouponQuantityRepository")
    // 테스트에서 직접 주입
    public CouponQuantityRepository jpaCouponQuantityRepository(
            CouponQuantityJpaRepository couponQuantityJpaRepository) {
        return new CouponQuantityRepositoryImpl(couponQuantityJpaRepository);
    }
}
