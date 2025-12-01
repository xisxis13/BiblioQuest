package prj.books_teamB_spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.UserRepository;
import prj.books_teamB_spring.service.UserService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("alice");
        testUser.setEmail("alice@example.com");
        testUser.setPassword("password123");
        testUser.setRole(Role.USER);
        userRepository.save(testUser);
    }

    @Test
    void requireUser_withValidUsername_returnsUser() {
        User result = userService.requireUser("alice");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void requireUser_withNullUsername_throws401() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.requireUser(null));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Missing X-Username header");
    }

    @Test
    void requireUser_withEmptyUsername_throws401() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.requireUser(""));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Missing X-Username header");
    }

    @Test
    void requireUser_withBlankUsername_throws401() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.requireUser("   "));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Missing X-Username header");
    }

    @Test
    void requireUser_withNonExistentUsername_throws401() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.requireUser("unknown"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Unknown user");
    }

    @Test
    void requireUser_withAdminUser_returnsAdminUser() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("admin123");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        User result = userService.requireUser("admin");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void requireUser_withAuteurUser_returnsAuteurUser() {
        User auteur = new User();
        auteur.setUsername("author");
        auteur.setEmail("author@example.com");
        auteur.setPassword("author123");
        auteur.setRole(Role.AUTEUR);
        userRepository.save(auteur);

        User result = userService.requireUser("author");

        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(Role.AUTEUR);
    }

    @Test
    void requireUser_withBibliothecaireUser_returnsBibliothecaireUser() {
        User bibliothecaire = new User();
        bibliothecaire.setUsername("librarian");
        bibliothecaire.setEmail("librarian@example.com");
        bibliothecaire.setPassword("librarian123");
        bibliothecaire.setRole(Role.BIBLIOTHECAIRE);
        userRepository.save(bibliothecaire);

        User result = userService.requireUser("librarian");

        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(Role.BIBLIOTHECAIRE);
    }

    @Test
    void requireUser_caseSensitiveUsername_findsExactMatch() {

        User result = userService.requireUser("alice");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("alice");
    }

    @Test
    void requireUser_caseSensitiveUsername_failsWithWrongCase() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.requireUser("ALICE"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Unknown user");
    }

    @Test
    void requireUser_multipleUsers_findsCorrectUser() {
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

        User resultAlice = userService.requireUser("alice");
        User resultBob = userService.requireUser("bob");
        User resultCharlie = userService.requireUser("charlie");

        assertThat(resultAlice.getUsername()).isEqualTo("alice");
        assertThat(resultBob.getUsername()).isEqualTo("bob");
        assertThat(resultCharlie.getUsername()).isEqualTo("charlie");
    }

    @Test
    void requireUser_withSpecialCharactersInUsername_returnsUser() {
        User specialUser = new User();
        specialUser.setUsername("user_123");
        specialUser.setEmail("user123@example.com");
        specialUser.setPassword("password");
        specialUser.setRole(Role.USER);
        userRepository.save(specialUser);

        User result = userService.requireUser("user_123");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("user_123");
    }

    @Test
    void requireUser_withLongUsername_returnsUser() {
        String longUsername = "a".repeat(50);
        User longUser = new User();
        longUser.setUsername(longUsername);
        longUser.setEmail("long@example.com");
        longUser.setPassword("password");
        longUser.setRole(Role.USER);
        userRepository.save(longUser);

        User result = userService.requireUser(longUsername);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(longUsername);
    }

    @Test
    void requireUser_consecutiveCalls_returnsSameUserData() {
        User result1 = userService.requireUser("alice");
        User result2 = userService.requireUser("alice");

        assertThat(result1.getId()).isEqualTo(result2.getId());
        assertThat(result1.getUsername()).isEqualTo(result2.getUsername());
        assertThat(result1.getEmail()).isEqualTo(result2.getEmail());
    }

    @Test
    void requireUser_afterUserDeletion_throws401() {
        userRepository.deleteAll();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.requireUser("alice"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Unknown user");
    }

    @Test
    void requireUser_withWhitespaceOnlyUsername_throws401() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.requireUser("\t\n "));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Missing X-Username header");
    }

    @Test
    void requireUser_withTabsInUsername_throws401() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.requireUser("\t\t"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Missing X-Username header");
    }
}