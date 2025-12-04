package prj.books_teamB_spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.UserRepository;
import prj.books_teamB_spring.service.UserService;

/**
 * Tests pour UserService
 * Teste la méthode requireUser avec différents scénarios
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Nettoyer la base de données
        userRepository.deleteAll();

        // Créer un utilisateur de test
        testUser = new User();
        testUser.setUsername("alice");
        testUser.setEmail("alice@example.com");
        testUser.setPassword("password123");
        testUser.setRole(Role.USER);
        userRepository.save(testUser);
    }

    @Test
    void requireUser_withValidUsername_returnsUser() {
        // Act
        User result = userService.requireUser("alice");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void requireUser_withNullUsername_throws401() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userService.requireUser(null)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Missing X-Username header");
    }

    @Test
    void requireUser_withEmptyUsername_throws401() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userService.requireUser("")
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Missing X-Username header");
    }

    @Test
    void requireUser_withBlankUsername_throws401() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userService.requireUser("   ")
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Missing X-Username header");
    }

    @Test
    void requireUser_withNonExistentUsername_throws401() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userService.requireUser("unknown")
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Unknown user");
    }

    @Test
    void requireUser_withAdminUser_returnsAdminUser() {
        // Arrange
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("admin123");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Act
        User result = userService.requireUser("admin");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void requireUser_withAuteurUser_returnsAuteurUser() {
        // Arrange
        User auteur = new User();
        auteur.setUsername("author");
        auteur.setEmail("author@example.com");
        auteur.setPassword("author123");
        auteur.setRole(Role.AUTEUR);
        userRepository.save(auteur);

        // Act
        User result = userService.requireUser("author");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(Role.AUTEUR);
    }

    @Test
    void requireUser_withBibliothecaireUser_returnsBibliothecaireUser() {
        // Arrange
        User bibliothecaire = new User();
        bibliothecaire.setUsername("librarian");
        bibliothecaire.setEmail("librarian@example.com");
        bibliothecaire.setPassword("librarian123");
        bibliothecaire.setRole(Role.BIBLIOTHECAIRE);
        userRepository.save(bibliothecaire);

        // Act
        User result = userService.requireUser("librarian");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(Role.BIBLIOTHECAIRE);
    }

    @Test
    void requireUser_caseSensitiveUsername_findsExactMatch() {
        // Arrange - Username "alice" existe déjà (créé dans setUp)
        
        // Act - Chercher avec la bonne casse
        User result = userService.requireUser("alice");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("alice");
    }

    @Test
    void requireUser_caseSensitiveUsername_failsWithWrongCase() {
        // Act & Assert - Chercher avec une casse différente
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userService.requireUser("ALICE")
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Unknown user");
    }

    @Test
    void requireUser_multipleUsers_findsCorrectUser() {
        // Arrange
        User bob = new User();
        bob.setUsername("bob");
        bob.setEmail("bob@example.com");
        bob.setPassword("bob123");
        bob.setRole(Role.USER);
        userRepository.save(bob);

        User charlie = new User();
        charlie.setUsername("charlie");
        charlie.setEmail("charlie@example.com");
        charlie.setPassword("charlie123");
        charlie.setRole(Role.USER);
        userRepository.save(charlie);

        // Act
        User resultAlice = userService.requireUser("alice");
        User resultBob = userService.requireUser("bob");
        User resultCharlie = userService.requireUser("charlie");

        // Assert
        assertThat(resultAlice.getUsername()).isEqualTo("alice");
        assertThat(resultBob.getUsername()).isEqualTo("bob");
        assertThat(resultCharlie.getUsername()).isEqualTo("charlie");
    }

    @Test
    void requireUser_withSpecialCharactersInUsername_returnsUser() {
        // Arrange
        User specialUser = new User();
        specialUser.setUsername("user_123");
        specialUser.setEmail("user123@example.com");
        specialUser.setPassword("password");
        specialUser.setRole(Role.USER);
        userRepository.save(specialUser);

        // Act
        User result = userService.requireUser("user_123");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("user_123");
    }

    @Test
    void requireUser_withLongUsername_returnsUser() {
        // Arrange
        String longUsername = "a".repeat(50);
        User longUser = new User();
        longUser.setUsername(longUsername);
        longUser.setEmail("long@example.com");
        longUser.setPassword("password");
        longUser.setRole(Role.USER);
        userRepository.save(longUser);

        // Act
        User result = userService.requireUser(longUsername);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(longUsername);
    }

    @Test
    void requireUser_consecutiveCalls_returnsSameUserData() {
        // Act
        User result1 = userService.requireUser("alice");
        User result2 = userService.requireUser("alice");

        // Assert
        assertThat(result1.getId()).isEqualTo(result2.getId());
        assertThat(result1.getUsername()).isEqualTo(result2.getUsername());
        assertThat(result1.getEmail()).isEqualTo(result2.getEmail());
    }

    @Test
    void requireUser_afterUserDeletion_throws401() {
        // Arrange - Supprimer l'utilisateur
        userRepository.deleteAll();

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userService.requireUser("alice")
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Unknown user");
    }

    @Test
    void requireUser_withWhitespaceOnlyUsername_throws401() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userService.requireUser("\t\n ")
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Missing X-Username header");
    }

    @Test
    void requireUser_withTabsInUsername_throws401() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userService.requireUser("\t\t")
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Missing X-Username header");
    }
}