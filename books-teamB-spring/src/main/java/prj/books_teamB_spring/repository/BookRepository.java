package prj.books_teamB_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prj.books_teamB_spring.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    
}
