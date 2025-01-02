package kr.hhplus.be.commerce.infra.product;

import jakarta.transaction.Transactional;
import kr.hhplus.be.commerce.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}
