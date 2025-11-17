package prj.books_teamB_spring;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.UserRepository;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.defer-datasource-initialization=true")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindUser() {
        // 1. Création (Arrange)
        User user = new User();
        user.setUsername("batman");
        user.setEmail("batman@gotham.city");
        user.setPassword("secret123");
        user.setRole(Role.ADMIN);

        // 2. Action (Act)
        User savedUser = userRepository.save(user);

        // 3. Vérification (Assert)
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
        assertThat(savedUser.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void testFindByUsername() {
        // Arrange
        User user = new User(0, "joker", "joker@arkham.com", "haha", Role.USER);
        userRepository.save(user);

        // Act
        Optional<User> found = userRepository.findByUsername("joker");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("joker@arkham.com");
    }

    @Test
    void testFindByEmail() {
        // Arrange
        User user = new User(0, "robin", "robin@sidekick.com", "batcave", Role.USER);
        userRepository.save(user);

        // Act
        Optional<User> found = userRepository.findByEmail("robin@sidekick.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("robin");
    }

    @Test
    void testFindNonExistentUser() {
        Optional<User> found = userRepository.findByUsername("ghost");
        assertThat(found).isEmpty();
    }
}