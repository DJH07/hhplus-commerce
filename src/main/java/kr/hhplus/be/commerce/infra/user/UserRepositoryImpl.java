package kr.hhplus.be.commerce.infra.user;

import kr.hhplus.be.commerce.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean existsById(Long id) {
        return userJpaRepository.findById(id).isPresent();
    }
}
