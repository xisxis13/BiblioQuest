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
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindUser() {
        User user = new User();
        user.setUsername("batman");
        user.setEmail("batman@gotham.city");
        user.setPassword("secret123");
        user.setRole(Role.ADMIN);

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
        assertThat(savedUser.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void testFindByUsername() {
        User user = new User();
        user.setUsername("joker");
        user.setEmail("joker@arkham.com");
        user.setPassword("haha");
        user.setRole(Role.USER);
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("joker");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("joker@arkham.com");
    }

    @Test
    void testFindByEmail() {
        User user = new User();
        user.setUsername("robin");
        user.setEmail("robin@sidekick.com");
        user.setPassword("batcave");
        user.setRole(Role.USER);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("robin@sidekick.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("robin");
    }

    @Test
    void testFindNonExistentUser() {
        Optional<User> found = userRepository.findByUsername("ghost");
        assertThat(found).isEmpty();
    }
}