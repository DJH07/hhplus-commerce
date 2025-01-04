package kr.hhplus.be.commerce.infra.product;

import kr.hhplus.be.commerce.domain.product.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
}
