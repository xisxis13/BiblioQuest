package prj.books_teamB_spring.repository;

import org.springframework.data.repository.CrudRepository;
import prj.books_teamB_spring.model.Author;

public interface AuthorRepository extends CrudRepository<Author, Long> {
    
}
