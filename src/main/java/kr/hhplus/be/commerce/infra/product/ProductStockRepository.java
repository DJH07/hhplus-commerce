package kr.hhplus.be.commerce.infra.product;

import jakarta.transaction.Transactional;
import kr.hhplus.be.commerce.domain.product.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
}
