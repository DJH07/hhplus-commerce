package kr.hhplus.be.commerce.infra.product;

import kr.hhplus.be.commerce.domain.order.OrderStatus;
import kr.hhplus.be.commerce.domain.product.Product;
import kr.hhplus.be.commerce.domain.product.ProductResult;
import kr.hhplus.be.commerce.domain.product.TopProductResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    @Query("select " +
            "new kr.hhplus.be.commerce.domain.product.ProductResult(" +
            "p.productId, " +
            "p.name, " +
            "p.price, " +
            "p.description, " +
            "p.stock) " +
            "from Product p")
    Page<ProductResult> findAllProductResults(Pageable pageable);

    @Query("select " +
            "new kr.hhplus.be.commerce.domain.product.TopProductResult(" +
            "p.productId, " +
            "p.name, " +
            "p.price, " +
            "p.description, " +
            "SUM(oi.quantity)) " +
            "from Product p " +
            "left join OrderItem oi on oi.productId = p.productId " +
            "left join Order o on o.orderId = oi.orderId " +
            "where o.status = :status " +
            "and o.payDate between :startDate and :endDate " +
            "group by p.productId " +
            "order by SUM(oi.quantity) desc")
    List<TopProductResult> findTopProducts(@Param("status") OrderStatus status,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);
}
