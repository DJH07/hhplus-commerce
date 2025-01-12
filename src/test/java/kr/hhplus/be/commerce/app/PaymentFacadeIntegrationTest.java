package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.app.dto.OrderProductItemRequest;
import kr.hhplus.be.commerce.domain.balance.Balance;
import kr.hhplus.be.commerce.domain.coupon.Coupon;
import kr.hhplus.be.commerce.domain.coupon.UserCoupon;
import kr.hhplus.be.commerce.domain.coupon.UserCouponStatus;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import kr.hhplus.be.commerce.domain.order.Order;
import kr.hhplus.be.commerce.domain.order.OrderStatus;
import kr.hhplus.be.commerce.domain.payment.Payment;
import kr.hhplus.be.commerce.domain.payment.PaymentStatus;
import kr.hhplus.be.commerce.domain.product.Product;
import kr.hhplus.be.commerce.domain.product.ProductStatus;
import kr.hhplus.be.commerce.domain.product.ProductStock;
import kr.hhplus.be.commerce.domain.user.User;
import kr.hhplus.be.commerce.utils.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.commerce.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentFacadeIntegrationTest extends IntegrationTest {
    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private PaymentFacade paymentFacade;

    @Test
    @DisplayName("결제 실패 시 주문과 결제 정보가 롤백되며, 상품 재고는 복구")
    void payment_ShouldRollback_WhenPaymentFailed() {
        // given
        User user = userJpaRepository.save(createTestUser());
        balanceJpaRepository.save(Balance.create(user.getUserId(), 100000000L));

        int productCount = 2;
        List<Product> products = new ArrayList<>();
        List<OrderProductItemRequest> orderItems = new ArrayList<>();

        for (int i = 0; i < productCount; i++) {
            Product product = createTestProduct(i + 1L, ProductStatus.AVAILABLE, (i + 1) * 10L);
            Product savedProduct = productJpaRepository.save(product);
            ProductStock productStock = ProductStock.create(savedProduct.getProductId(), product.getStock());
            productStockJpaRepository.save(productStock);
            products.add(product);

            orderItems.add(new OrderProductItemRequest(product.getProductId(), (long) i + 1));
        }

        Long orderId = orderFacade.order(user.getUserId(), orderItems);

        Coupon coupon = couponJpaRepository.save(createTestCoupon(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(5), 50L));
        UserCoupon userCoupon = UserCoupon.create(user.getUserId(), coupon.getCouponId(), UserCouponStatus.ISSUED, LocalDateTime.now());
        UserCoupon savedUserCoupon = userCouponJpaRepository.save(userCoupon);

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentFacade.payment(orderId, userCoupon.getUserCouponId(), true);
        });

        // then
        Assertions.assertEquals(BusinessErrorCode.PAYMENT_FAILED, exception.getErrorCode());

        Order savedOrder = orderRepository.findById(orderId);
        Assertions.assertEquals(OrderStatus.FAILED, savedOrder.getStatus());

        for (int i = 0; i < productCount; i++) {
            Product updatedProduct = productJpaRepository.findById(products.get(i).getProductId()).orElseThrow();
            Assertions.assertEquals((i + 1) * 10L, updatedProduct.getStock());
        }

        List<Payment> payments = paymentJpaRepository.findAll();
        Assertions.assertFalse(payments.isEmpty());
        Payment payment = payments.get(0);
        Assertions.assertEquals(payment.getOrderId(), orderId);
        Assertions.assertEquals(PaymentStatus.FAILED, payment.getStatus());

        Order afterOrder = orderRepository.findById(orderId);
        Assertions.assertEquals(OrderStatus.FAILED, afterOrder.getStatus());

        Optional<UserCoupon> userCouponOptional = userCouponJpaRepository.findById(savedUserCoupon.getUserCouponId());
        Assertions.assertTrue(userCouponOptional.isPresent());
        Assertions.assertEquals(UserCouponStatus.ISSUED, userCouponOptional.get().getStatus());
    }

    @Test
    @DisplayName("결제 성공 시 주문 상태가 PAID로 변경되고 결제 정보가 저장되며, 상품 재고가 정상 차감")
    void payment_ShouldSucceed_WhenPaymentSuccessful() {
        // given
        User user = userJpaRepository.save(createTestUser());
        balanceJpaRepository.save(Balance.create(user.getUserId(), 100000000L));

        int productCount = 2;
        List<Product> products = new ArrayList<>();
        List<OrderProductItemRequest> orderItems = new ArrayList<>();

        for (int i = 0; i < productCount; i++) {
            Product product = createTestProduct(i + 1L, ProductStatus.AVAILABLE, (i + 1) * 10L);
            Product savedProduct = productJpaRepository.save(product);
            ProductStock productStock = ProductStock.create(savedProduct.getProductId(), product.getStock());
            productStockJpaRepository.save(productStock);
            products.add(product);

            orderItems.add(new OrderProductItemRequest(product.getProductId(), (long) i + 1));
        }

        Long orderId = orderFacade.order(user.getUserId(), orderItems);

        Coupon coupon = couponJpaRepository.save(createTestCoupon(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(5), 50L));
        UserCoupon userCoupon = UserCoupon.create(user.getUserId(), coupon.getCouponId(), UserCouponStatus.ISSUED, LocalDateTime.now());
        userCouponJpaRepository.save(userCoupon);

        // when
        paymentFacade.payment(orderId, userCoupon.getUserCouponId(), false);

        // then
        Order savedOrder = orderRepository.findById(orderId);
        Assertions.assertEquals(OrderStatus.PAID, savedOrder.getStatus());

        for (int i = 0; i < productCount; i++) {
            Product updatedProduct = productJpaRepository.findById(products.get(i).getProductId()).orElseThrow();
            Assertions.assertEquals((i + 1) * 10L - ((long) i + 1), updatedProduct.getStock());
        }

        List<Payment> payments = paymentJpaRepository.findAll();
        Assertions.assertFalse(payments.isEmpty());
        Payment payment = payments.get(0);
        Assertions.assertEquals(payment.getOrderId(), orderId);
        Assertions.assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
    }


}
