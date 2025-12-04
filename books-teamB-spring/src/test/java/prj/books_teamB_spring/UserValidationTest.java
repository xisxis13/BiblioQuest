package prj.books_teamB_spring;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.*;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void valid_user_should_pass() {
    User u = new User(
      "alice",
      "alice@example.com",
      "$2a$10$hash",
      Role.USER);

    Set<ConstraintViolation<User>> violations = validator.validate(u);
    assertTrue(violations.isEmpty(), () -> "Violations: " + violations);
  }

  @Test
  void invalid_user_should_fail_on_constraints() {
    User u = new User();
    u.setUsername("");
    u.setEmail("not-an-email");
    u.setPassword("");
    u.setRole(null);

    Set<ConstraintViolation<User>> violations = validator.validate(u);
    assertFalse(violations.isEmpty());

    var props = violations.stream()
        .map(v -> v.getPropertyPath().toString())
        .collect(Collectors.toSet());

    assertTrue(props.contains("username"));
    assertTrue(props.contains("email"));
    assertTrue(props.contains("password"));
    assertTrue(props.contains("role"));
  }
}
