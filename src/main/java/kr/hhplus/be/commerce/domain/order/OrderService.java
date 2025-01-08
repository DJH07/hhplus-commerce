package kr.hhplus.be.commerce.domain.order;

import kr.hhplus.be.commerce.domain.product.OrderProductItemResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Long registerOrder(RegisterOrderCommand command) {
        Order order = Order.create(
                command.userId(),
                command.orderTotalAmount(),
                LocalDateTime.now());
        Long orderId = orderRepository.save(order);

        List<OrderItem> orderItemList = new ArrayList<>();
        for(OrderProductItemResult item : command.orderProductItemResults()) {
            OrderItem orderItem = OrderItem.create(
                    item.productId(),
                    item.quantity(),
                    item.totalPrice());
            orderItemList.add(orderItem);
        }
        orderItemRepository.saveAll(orderItemList);

        return orderId;
    }

    public OrderResult getOrderResult(Long orderId) {
        Order order = orderRepository.findById(orderId);

        List<OrderItemResult> items = orderItemRepository.findAllByOrderId(order.getOrderId());

        return new OrderResult(order.getPayTotalAmount(), order.getUserId(), items);
    }

    public void successOrder(Long orderId, Long amount) {
        Order order = orderRepository.findById(orderId);
        order.changeStatus(OrderStatus.PAID);
        order.changePayDate(LocalDateTime.now());
        order.changePayTotalAmount(amount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        order.changeStatus(OrderStatus.FAILED);
    }
}
