package prj.books_teamB_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import prj.books_teamB_spring.model.Book;

import java.util.Optional;

public interface BookRepository extends 
    CrudRepository<Book, Long>, 
    PagingAndSortingRepository<Book, Long>,
    JpaSpecificationExecutor<Book> {
    
    @EntityGraph(attributePaths = {"authors", "subject"})
    Page<Book> findAll(Specification<Book> spec, Pageable pageable);
    
    @EntityGraph(attributePaths = {"authors", "subject"})
    Optional<Book> findById(Long id);
}