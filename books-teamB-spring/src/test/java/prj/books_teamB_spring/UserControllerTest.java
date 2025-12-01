package prj.books_teamB_spring;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import prj.books_teamB_spring.controller.UserController;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.service.UserService;

@WebMvcTest(controllers = UserController.class)
@WithMockUser
class UserControllerTest {

        @Autowired
        MockMvc mvc;

        @MockBean
        UserService userService;

        @Test
        void getCurrentUser_withValidUsername_returnsUserProfile() throws Exception {
                User user = new User();
                user.setId(1L);
                user.setUsername("alice");
                user.setEmail("alice@example.com");
                user.setPassword("hashed_password");
                user.setRole(Role.USER);

                when(userService.requireUser("alice")).thenReturn(user);

                mvc.perform(get("/api/users/me")
                                .header("X-Username", "alice"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.username").value("alice"))
                                .andExpect(jsonPath("$.email").value("alice@example.com"))
                                .andExpect(jsonPath("$.role").value("USER"));
        }

        @Test
        void getCurrentUser_withAdminRole_returnsAdminProfile() throws Exception {
                User admin = new User();
                admin.setId(2L);
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword("admin_password");
                admin.setRole(Role.ADMIN);

                when(userService.requireUser("admin")).thenReturn(admin);

                mvc.perform(get("/api/users/me")
                                .header("X-Username", "admin"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("admin"))
                                .andExpect(jsonPath("$.email").value("admin@example.com"))
                                .andExpect(jsonPath("$.role").value("ADMIN"));
        }

        @Test
        void getCurrentUser_withoutUsername_returns400() throws Exception {
                mvc.perform(get("/api/users/me"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void getCurrentUser_withEmptyUsername_returns401() throws Exception {
                when(userService.requireUser(""))
                                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                                "Missing X-Username header"));

                mvc.perform(get("/api/users/me")
                                .header("X-Username", ""))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void getCurrentUser_withInvalidUsername_returns401() throws Exception {
                when(userService.requireUser("unknown"))
                                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));

                mvc.perform(get("/api/users/me")
                                .header("X-Username", "unknown"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void getCurrentUser_withBlankUsername_returns401() throws Exception {
                when(userService.requireUser("   "))
                                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                                "Missing X-Username header"));

                mvc.perform(get("/api/users/me")
                                .header("X-Username", "   "))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void getCurrentUser_withSpecialCharactersInUsername_returnsUser() throws Exception {
                User user = new User();
                user.setId(3L);
                user.setUsername("user_123");
                user.setEmail("user123@example.com");
                user.setPassword("password");
                user.setRole(Role.USER);

                when(userService.requireUser("user_123")).thenReturn(user);

                mvc.perform(get("/api/users/me")
                                .header("X-Username", "user_123"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("user_123"));
        }

        @Test
        void getCurrentUser_multipleRequests_returnsConsistentData() throws Exception {
                User user = new User();
                user.setId(4L);
                user.setUsername("bob");
                user.setEmail("bob@example.com");
                user.setPassword("password");
                user.setRole(Role.USER);

                when(userService.requireUser("bob")).thenReturn(user);

                mvc.perform(get("/api/users/me")
                                .header("X-Username", "bob"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("bob"));

                mvc.perform(get("/api/users/me")
                                .header("X-Username", "bob"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("bob"))
                                .andExpect(jsonPath("$.email").value("bob@example.com"));
        }

        @Test
        void getCurrentUser_caseInsensitiveUsername_shouldRespectExactCase() throws Exception {
                User user = new User();
                user.setId(5L);
                user.setUsername("Alice");
                user.setEmail("alice@example.com");
                user.setPassword("password");
                user.setRole(Role.USER);

                when(userService.requireUser("Alice")).thenReturn(user);

                mvc.perform(get("/api/users/me")
                                .header("X-Username", "Alice"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("Alice"));
        }

        @Test
        void getCurrentUser_longUsername_returnsUser() throws Exception {
                String longUsername = "a".repeat(50);
                User user = new User();
                user.setId(6L);
                user.setUsername(longUsername);
                user.setEmail("long@example.com");
                user.setPassword("password");
                user.setRole(Role.USER);

                when(userService.requireUser(longUsername)).thenReturn(user);

                mvc.perform(get("/api/users/me")
                                .header("X-Username", longUsername))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value(longUsername));
        }
}