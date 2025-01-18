package kr.hhplus.be.commerce.app;

import kr.hhplus.be.commerce.app.dto.OrderProductItemRequest;
import kr.hhplus.be.commerce.domain.order.OrderService;
import kr.hhplus.be.commerce.domain.order.RegisterOrderCommand;
import kr.hhplus.be.commerce.domain.product.OrderProductItemCommand;
import kr.hhplus.be.commerce.domain.product.OrderProductResult;
import kr.hhplus.be.commerce.domain.product.ProductService;
import kr.hhplus.be.commerce.domain.user.UserService;
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

}
