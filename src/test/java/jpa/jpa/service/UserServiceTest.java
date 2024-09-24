package jpa.jpa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jpa.jpa.entity.User;
import jpa.jpa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserServiceTest {

    // Mock으로 만들면 실제 디비에 접근하지 않고도 테스트 가능
    @Mock
    private UserRepository userRepository;

    // Mock 의존성 주입
    @InjectMocks
    private UserService userService;

    // 테스트 메소드가 실행되기 전에 이게 먼저 실행됨
    @BeforeEach
    public void setup() {
        //Mock으로 정의된 애들은 이 메소드에 의해 초기화됨
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser() {
        User user = new User("john", "john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser("john", "john@example.com");

        assertNotNull(createdUser);
        assertEquals("john", createdUser.getUsername());
        assertEquals("john@example.com", createdUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = Arrays.asList(new User("john", "john@example.com"), new User("jane", "jane@example.com"));
        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = userService.getAllUsers();

        assertEquals(2, allUsers.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUserByUsername() {
        User user = new User("john", "john@example.com");
        when(userRepository.findByUsername("john")).thenReturn(user);

        User foundUser = userService.getUserByUsername("john");

        assertNotNull(foundUser);
        assertEquals("john", foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("john");
    }

    @DisplayName("사용자 삭제 성공")
    @Test
    public void deleteUserById_UserExists_ShouldDeleteUser() {
        Long userId = 1L;

        // 해당 아이디를 가진 유저가 존재하는것을 가정하고 시작
        when(userRepository.existsById(userId)).thenReturn(true);

        // 메소드 호출
        userService.deleteUserById(userId);

        // 메소드가 한버 호출된게 맞는지 확인
        verify(userRepository, times(1)).deleteById(userId);
    }

    @DisplayName("사용자 삭제 실패 - 사용자 없음")
    @Test
    public void deleteUserById_UserDoesNotExist_ShouldThrowException() {
        Long userId = 1L;

        // 해당 아이디를 가진 유저가 없다고 가정
        when(userRepository.existsById(userId)).thenReturn(false);

        // 해당 서비스에서 발생시키는 예외가 런타임 예외와 같은지 확인
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUserById(userId);
        });

        // 예외 메세지가 동일한지 확인
        assertEquals("User not found with id: " + userId, exception.getMessage());

        // 해당 메소드가 호출되지 않았는지 확인
        verify(userRepository, never()).deleteById(userId);
    }

    @DisplayName("기존 사용자 업데이트 테스트 (Update)")
    @Test
    public void saveUser_ShouldUpdateExistingUser() {
        // 기존 엔티티 설정 (ID가 1인 사용자)
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("John");
        existingUser.setEmail("john@example.com");

        // 저장된 엔티티 찾는 동작에 대한 Mock 설정
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // 업데이트할 새로운 정보 설정
        existingUser.setEmail("newemail@example.com");

        // 저장 동작에 대한 Mock 설정
        when(userRepository.save(existingUser)).thenReturn(existingUser); // 수정된 user 객체를 저장하도록 Mock 설정

        // 서비스 호출
        User updatedUser = userService.updateUser(1L, "John", "newemail@example.com");

        // 업데이트 결과 검증
        assertNotNull(updatedUser);
        assertEquals("John", updatedUser.getUsername());
        assertEquals("newemail@example.com", updatedUser.getEmail());

        // save 메소드가 한 번 호출되었는지 검증
        verify(userRepository, times(1)).save(existingUser);
    }

}
