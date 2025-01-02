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
    private Integer price;

    @Column(name = "status", nullable = false)
    @Comment("상품 상태")
    private String status;

    @Column(name = "description", columnDefinition = "TEXT")
    @Comment("상품 설명")
    private String description;

    public static Product create(String name, Integer price, String status, String description) {
        Product entity = new Product();
        entity.name = name;
        entity.price = price;
        entity.status = status;
        entity.description = description;
        return entity;
    }
}
