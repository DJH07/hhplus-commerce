package kr.hhplus.be.commerce.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.commerce.domain.utils.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "order_item")
public class OrderItem extends AuditingFields implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id", nullable = false)
    @Comment("주문 항목 ID")
    private Long orderItemId;

    @Column(name = "order_id", nullable = false, insertable = false, updatable = false)
    @Comment("주문 ID")
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    @Comment("상품 ID")
    private Long productId;

    @Column(name = "quantity", nullable = false)
    @Comment("주문 수량")
    private Long quantity;

    @Column(name = "price", nullable = false)
    @Comment("상품 주문 당시 가격")
    private Long price;

    @Column(name = "total_price", nullable = false)
    @Comment("주문 항목 총 금액")
    private Long totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
    @Comment("주문")
    private Order order;

    public static OrderItem create(Long productId, Long quantity, Long price, Long totalPrice) {
        OrderItem entity = new OrderItem();
        entity.productId = productId;
        entity.quantity = quantity;
        entity.price = price;
        entity.totalPrice = totalPrice;
        return entity;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.orderId = order.getOrderId();
    }
}
