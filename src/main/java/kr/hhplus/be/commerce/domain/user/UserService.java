package kr.hhplus.be.commerce.domain.user;

import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND);
        }
    }
}
