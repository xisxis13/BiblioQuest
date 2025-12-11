package prj.books_teamB_spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import prj.books_teamB_spring.model.ReadingSession;
import prj.books_teamB_spring.model.UserBook;

/**
 * Repository pour la gestion des sessions de lecture
 */
public interface ReadingSessionRepository extends JpaRepository<ReadingSession, Long> {
    
    /**
     * Trouve toutes les sessions d'un utilisateur
     */
    List<ReadingSession> findByUserId(Long userId);
    
    /**
     * Trouve les sessions d'un utilisateur pour un livre spécifique
     */
    List<ReadingSession> findByUserBook(UserBook userBook);
    
    
}