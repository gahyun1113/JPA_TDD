package jpa.jpa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jpa.jpa.dto.UserDto;
import jpa.jpa.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                        .contentType(MediaType.APPLICATION_JSON)
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
}
