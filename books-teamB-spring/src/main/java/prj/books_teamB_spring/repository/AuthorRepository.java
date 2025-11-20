package prj.books_teamB_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prj.books_teamB_spring.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
