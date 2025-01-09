package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.app.dto.OrderProductItemRequest;
import kr.hhplus.be.commerce.domain.balance.BalanceService;
import kr.hhplus.be.commerce.domain.coupon.CouponService;
import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import kr.hhplus.be.commerce.domain.order.OrderResult;
import kr.hhplus.be.commerce.domain.order.OrderService;
import kr.hhplus.be.commerce.domain.order.RegisterOrderCommand;
import kr.hhplus.be.commerce.domain.payment.PaymentService;
import kr.hhplus.be.commerce.domain.payment.PaymentStatus;
import kr.hhplus.be.commerce.domain.product.OrderProductItemCommand;
import kr.hhplus.be.commerce.domain.product.OrderProductResult;
import kr.hhplus.be.commerce.domain.product.ProductService;
import kr.hhplus.be.commerce.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    // TODO : 현 OrderFacade의 책임이 크다. 추후 책임 분리 고려.

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;
    private final CouponService couponService;
    private final BalanceService balanceService;
    private final PaymentService paymentService;

    @Transactional
    public Long order(Long userId, List<OrderProductItemRequest> items) {

        userService.checkUserExists(userId);

        // productId와 quantity로 상품 조회 요청 및 재고 삭감
        List<OrderProductItemCommand> productItemCommands = items.stream()
                .map(itemRequest -> new OrderProductItemCommand(itemRequest.productId(), itemRequest.quantity()))
                .toList();
        OrderProductResult orderProductResult = productService.processOrderProducts(productItemCommands);

        // 주문 정보 저장
        return orderService.registerOrder(new RegisterOrderCommand(
                userId,
                orderProductResult.totalAmount(),
                orderProductResult.itemResultList()));

    }

    // FIXME : isFail 패러미터는 결제 실패 테스트용 패러미터. 추후 제거
    @Transactional
    public PaymentStatus payment(Long orderId, Long userCouponId, boolean isFail) {

        // TODO : 일정 시간 지난 주문 만료 처리

        // 주문 정보 조회
        OrderResult order = orderService.getOrderResult(orderId);

        // 쿠폰 사용 시, 쿠폰 유효성 검증 및 쿠폰 상태 변경 후 유효 쿠폰 결과 반환
        Long payTotalAmount = couponService.applyUserCoupon(userCouponId, order.totalAmount());

        // 사용자 잔액 확인 요청 및 차감
        balanceService.reduceBalance(order.userId(), payTotalAmount);

        // 결제 시도
        PaymentStatus paymentStatus = paymentService.processPayment(orderId, payTotalAmount, isFail);

        if (Objects.requireNonNull(paymentStatus).equals(PaymentStatus.SUCCESS)) {
            orderService.successOrder(orderId, payTotalAmount);
        } else if (paymentStatus.equals(PaymentStatus.FAILED)) {
            orderService.failOrder(orderId);
            productService.restoreProduct(order.itemResultList());
            throw new BusinessException(BusinessErrorCode.PAYMENT_FAILED);
        }

        return paymentStatus;

    }
}
