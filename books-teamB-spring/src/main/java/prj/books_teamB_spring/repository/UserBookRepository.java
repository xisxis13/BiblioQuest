package prj.books_teamB_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.model.Book;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des livres dans la bibliothèque utilisateur
 */
public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    
    /**
     * Trouve tous les livres d'un utilisateur par ID
     */
    List<UserBook> findByUserId(Long userId);


/**
 * Trouve le livre en fonction de son User et Book
 */
    Optional<UserBook> findByUserAndBook(User user, Book book);
    
}