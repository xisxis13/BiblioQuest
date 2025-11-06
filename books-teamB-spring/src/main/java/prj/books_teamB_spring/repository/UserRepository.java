package prj.books_teamB_spring.repository;

import java.util.Optional;
import prj.books_teamB_spring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
