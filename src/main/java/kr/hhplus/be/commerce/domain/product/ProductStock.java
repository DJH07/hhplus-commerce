package kr.hhplus.be.commerce.domain.product;

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
@Table(name = "product_stock")
public class ProductStock extends AuditingFields implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_stock_id", nullable = false)
    @Comment("상품 재고 ID")
    private Long productStockId;

    @Column(name = "product_id", nullable = false, unique = true)
    @Comment("상품 ID")
    private Long productId;

    @Column(name = "remaining_stock", nullable = false)
    @Comment("잔여 재고 수량")
    private Long remainingStock;


    public static ProductStock create(Long productId, Long remainingStock) {
        ProductStock entity = new ProductStock();
        entity.productId = productId;
        entity.remainingStock = remainingStock;
        return entity;
    }

    public void changeRemainingStock(Long stock) {
        this.remainingStock = stock;
    }
}
