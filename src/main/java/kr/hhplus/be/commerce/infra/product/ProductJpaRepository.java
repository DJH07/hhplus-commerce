package kr.hhplus.be.commerce.infra.product;

import kr.hhplus.be.commerce.domain.product.Product;
import kr.hhplus.be.commerce.domain.product.ProductResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    @Query("select new kr.hhplus.be.commerce.domain.product.ProductResult(" +
            "p.productId, " +
            "p.name, " +
            "p.price, " +
            "p.description, " +
            "p.stock) " +
            "from Product p")
    Page<ProductResult> findAllProductResults(Pageable pageable);
}
