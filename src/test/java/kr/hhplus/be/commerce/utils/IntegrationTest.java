package kr.hhplus.be.commerce.utils;

import kr.hhplus.be.commerce.domain.balance.BalanceRepository;
import kr.hhplus.be.commerce.domain.order.OrderItemRepository;
import kr.hhplus.be.commerce.domain.order.OrderRepository;
import kr.hhplus.be.commerce.domain.product.ProductRepository;
import kr.hhplus.be.commerce.domain.user.UserRepository;
import kr.hhplus.be.commerce.infra.balance.BalanceJpaRepository;
import kr.hhplus.be.commerce.infra.coupon.CouponJpaRepository;
import kr.hhplus.be.commerce.infra.coupon.CouponQuantityJpaRepository;
import kr.hhplus.be.commerce.infra.coupon.UserCouponJpaRepository;
import kr.hhplus.be.commerce.infra.order.OrderItemJpaRepository;
import kr.hhplus.be.commerce.infra.order.OrderJpaRepository;
import kr.hhplus.be.commerce.infra.payment.PaymentJpaRepository;
import kr.hhplus.be.commerce.infra.product.ProductJpaRepository;
import kr.hhplus.be.commerce.infra.product.ProductStockJpaRepository;
import kr.hhplus.be.commerce.infra.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class IntegrationTest {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected BalanceRepository balanceRepository;
    @Autowired
    protected ProductRepository productRepository;
    @Autowired
    protected OrderRepository orderRepository;
    @Autowired
    protected OrderItemRepository orderItemRepository;
    @Autowired
    protected UserJpaRepository userJpaRepository;
    @Autowired
    protected BalanceJpaRepository balanceJpaRepository;
    @Autowired
    protected CouponJpaRepository couponJpaRepository;
    @Autowired
    protected CouponQuantityJpaRepository couponQuantityJpaRepository;
    @Autowired
    protected UserCouponJpaRepository userCouponJpaRepository;
    @Autowired
    protected ProductJpaRepository productJpaRepository;
    @Autowired
    protected ProductStockJpaRepository productStockJpaRepository;
    @Autowired
    protected OrderJpaRepository orderJpaRepository;
    @Autowired
    protected OrderItemJpaRepository orderItemJpaRepository;
    @Autowired
    protected PaymentJpaRepository paymentJpaRepository;

    @BeforeEach
    void init() {
        paymentJpaRepository.deleteAllInBatch();
        balanceJpaRepository.deleteAllInBatch();
        userCouponJpaRepository.deleteAllInBatch();
        couponQuantityJpaRepository.deleteAllInBatch();
        couponJpaRepository.deleteAllInBatch();
        orderItemJpaRepository.deleteAllInBatch();
        orderJpaRepository.deleteAllInBatch();
        productStockJpaRepository.deleteAllInBatch();
        productJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
    }

}
