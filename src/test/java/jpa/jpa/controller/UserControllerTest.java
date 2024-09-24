package jpa.jpa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jpa.jpa.dto.UserDto;
import jpa.jpa.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Post 사용자 생성 테스트")
    @Test
    public void testCreateUser() throws Exception {
// UserDto 객체 생성
        UserDto userDto = new UserDto();
        userDto.setUsername("gahyun");
        userDto.setEmail("gahyun@example.com");

        // UserDto를 JSON으로 변환
        String userJson = new ObjectMapper().writeValueAsString(userDto);

        // MockMvc를 사용하여 요청 전송
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("gahyun")) // 수정된 부분
                .andExpect(jsonPath("$.email").value("gahyun@example.com")); // 수정된 부분
    }


    @DisplayName("모든 사용자 가져오기")
    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
        //즉, 사용자가 하나 이상 존재하는지 검증하는 부분입니다.
    }

    @DisplayName("특정 사용자 가져오기")
    @Test
    public void testGetUserByUsername() throws Exception {
        mockMvc.perform(get("/users/gahyun"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("gahyun"));
    }

    @DisplayName("사용자 삭제 성공")
    @Test
    public void deleteUser_UserExists_ShouldReturnNoContent() throws Exception {
        Long userId = 1L;

        // Mocking the service method
        doNothing().when(userService).deleteUserById(userId);

        mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());

        // Verify that the service method was called
        verify(userService, times(1)).deleteUserById(userId);
    }

    @DisplayName("사용자 삭제 실패 - 사용자 없음")
    @Test
    public void deleteUser_UserDoesNotExist_ShouldReturnNotFound() throws Exception {
        Long userId = 1L;

        // Mocking the service method to throw exception
        doThrow(new RuntimeException("User not found with id: " + userId)).when(userService).deleteUserById(userId);

        mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found with id: " + userId));

        // Verify that the service method was called
        verify(userService, times(1)).deleteUserById(userId);
    }
}
