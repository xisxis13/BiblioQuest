package prj.books_teamB_spring.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import prj.books_teamB_spring.model.Book;


public interface BookRepository extends 
    CrudRepository<Book, Long>, 
    PagingAndSortingRepository<Book, Long>,
    JpaSpecificationExecutor<Book> {

}