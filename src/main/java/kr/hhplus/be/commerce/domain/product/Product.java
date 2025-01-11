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
@Table(name = "product")
public class Product extends AuditingFields implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    @Comment("상품 ID")
    private Long productId;

    @Column(name = "name", nullable = false)
    @Comment("상품 이름")
    private String name;

    @Column(name = "price", nullable = false)
    @Comment("상품 가격")
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("상품 상태")
    private ProductStatus status;

    @Column(name = "description", columnDefinition = "TEXT")
    @Comment("상품 설명")
    private String description;

    @Column(name = "stock", nullable = false)
    @Comment("상품 가격")
    private Long stock;

    public static Product create(String name, Long price, ProductStatus status, String description, Long stock) {
        Product entity = new Product();
        entity.name = name;
        entity.price = price;
        entity.status = status;
        entity.description = description;
        entity.stock = stock;
        return entity;
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }
    public void changeStock(Long stock) {
        this.stock = stock;
    }
}
