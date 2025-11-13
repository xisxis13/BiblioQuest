package prj.books_teamB_spring.repository;

import org.springframework.data.repository.CrudRepository;
import prj.books_teamB_spring.model.Book;

public interface BookRepository extends CrudRepository<Book, Long> {
    
}
