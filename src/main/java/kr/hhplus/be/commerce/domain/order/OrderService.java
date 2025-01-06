package kr.hhplus.be.commerce.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Long registerOrder(RegisterOrderCommand command) {
        Order order = Order.create(
                command.userId(),
                command.orderTotalAmount(),
                command.payTotalAmount(),
                LocalDateTime.now());
        return orderRepository.save(order);
    }
}
