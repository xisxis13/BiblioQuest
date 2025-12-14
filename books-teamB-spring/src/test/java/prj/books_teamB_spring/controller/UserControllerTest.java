package prj.books_teamB_spring.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import prj.books_teamB_spring.exception.GlobalExceptionHandler;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        user1 = new User();
        user1.setId(1L);
        user1.setUsername("john_doe");
        user1.setEmail("john@example.com");
        user1.setPassword("password123");
        user1.setRole(Role.USER);
        user1.setFollowersCount(10);
        user1.setFollowingCount(5);
        user1.setLastActivityAt(LocalDateTime.now());

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("jane_smith");
        user2.setEmail("jane@example.com");
        user2.setPassword("password456");
        user2.setRole(Role.AUTEUR);
        user2.setFollowersCount(25);
        user2.setFollowingCount(12);
        user2.setLastActivityAt(LocalDateTime.now());
    }

    @Test
    void getById_shouldReturnUser() throws Exception {
        when(userService.findById(1L)).thenReturn(user1);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("john_doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.followersCount", is(10)))
                .andExpect(jsonPath("$.followingCount", is(5)));
    }

    @Test
    void getById_shouldReturnAuteurUser() throws Exception {
        when(userService.findById(2L)).thenReturn(user2);

        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.username", is("jane_smith")))
                .andExpect(jsonPath("$.email", is("jane@example.com")))
                .andExpect(jsonPath("$.role", is("AUTEUR")))
                .andExpect(jsonPath("$.followersCount", is(25)))
                .andExpect(jsonPath("$.followingCount", is(12)));
    }

    @Test
    void getAll_shouldReturnListOfUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("john_doe")))
                .andExpect(jsonPath("$[0].role", is("USER")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("jane_smith")))
                .andExpect(jsonPath("$[1].role", is("AUTEUR")));
    }

    @Test
    void getAll_shouldReturnEmptyList() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
