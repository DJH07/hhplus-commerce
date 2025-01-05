package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.app.dto.OrderProductItemCommand;
import kr.hhplus.be.commerce.app.dto.OrderProductResult;
import kr.hhplus.be.commerce.app.dto.RegisterOrderCommand;
import kr.hhplus.be.commerce.domain.balance.BalanceService;
import kr.hhplus.be.commerce.domain.coupon.CouponService;
import kr.hhplus.be.commerce.domain.order.OrderService;
import kr.hhplus.be.commerce.domain.product.ProductService;
import kr.hhplus.be.commerce.domain.user.UserService;
import kr.hhplus.be.commerce.interfaces.dto.requestDto.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;
    private final CouponService couponService;
    private final BalanceService balanceService;

    @Transactional
    public Long order(OrderRequest request) {

        userService.checkUserExists(request.userId());

        // productId와 quantity로 상품 조회 요청 및 재고 삭감
        // 확인 시 총 주문 금액 반환
        // TODO : 재고 감소와 상품 정보 조회 분리
        List<OrderProductItemCommand> productItemCommands = request.items().stream()
                .map(itemRequest -> new OrderProductItemCommand(itemRequest.productId(), itemRequest.quantity()))
                .toList();
        productService.reduceProductsStock(productItemCommands);
        OrderProductResult orderProductResult = productService.getOrderProductResult(productItemCommands);

        // 쿠폰 사용 시, 쿠폰 유효성 검증 및 쿠폰 상태 변경 후 유효 쿠폰 결과 반환
        Long payTotalAmount = request.userCouponId() == null ?
                orderProductResult.totalAmount() :
                couponService.applyCoupon(request.userCouponId(), orderProductResult.totalAmount());

        // 사용자 잔액 확인 요청 및 차감
        balanceService.deductBalance(request.userId(), payTotalAmount);

        // 주문 정보 저장
        return orderService.registerOrder(new RegisterOrderCommand(
                request.userId(),
                orderProductResult.totalAmount(),
                payTotalAmount));

    }
}
