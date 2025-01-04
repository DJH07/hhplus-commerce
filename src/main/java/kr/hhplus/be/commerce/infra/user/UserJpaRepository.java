package kr.hhplus.be.commerce.infra.user;

import kr.hhplus.be.commerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
