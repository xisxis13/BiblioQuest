package prj.books_teamB_spring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "password", Role.USER);
        testUser.setId(1L);
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User foundUser = userService.findById(1L);

        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.findById(99L);
        });

        assertEquals("404 NOT_FOUND \"User not found\"", exception.getMessage());
        verify(userRepository).findById(99L);
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        User anotherUser = new User("anotheruser", "another@example.com", "password", Role.USER);
        anotherUser.setId(2L);
        when(userRepository.findAll()).thenReturn(List.of(testUser, anotherUser));

        List<User> users = userService.findAll();

        assertNotNull(users);
        assertEquals(2, users.size());
        verify(userRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> users = userService.findAll();

        assertNotNull(users);
        assertTrue(users.isEmpty());
        verify(userRepository).findAll();
    }
}