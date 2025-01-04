package kr.hhplus.be.commerce.domain;

import kr.hhplus.be.commerce.domain.error.BusinessErrorCode;
import kr.hhplus.be.commerce.domain.error.BusinessException;
import kr.hhplus.be.commerce.domain.user.UserRepository;
import kr.hhplus.be.commerce.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("사용자가 존재하지 않으면 USER_NOT_FOUND 예외 발생")
    void checkUserExists_ShouldThrowException_WhenUserNotFound() {
        // given
        final long userId = 1L;

        // when
        when(userRepository.existsById(userId)).thenReturn(false);

        // then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.checkUserExists(userId));

        assertEquals(BusinessErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

}
