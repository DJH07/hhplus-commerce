package kr.hhplus.be.commerce.infra.user;

import jakarta.transaction.Transactional;
import kr.hhplus.be.commerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface UserJpaRepository extends JpaRepository<User, Long> {
}
