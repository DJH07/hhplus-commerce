package kr.hhplus.be.commerce.infra.product;

import kr.hhplus.be.commerce.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}
