package prj.books_teamB_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prj.books_teamB_spring.model.ReadingStatus;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.model.UserBook;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des livres dans la bibliothèque utilisateur
 */
public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    
    /**
     * Trouve tous les livres d'un utilisateur
     */
    List<UserBook> findByUser(User user);
    
    /**
     * Trouve tous les livres d'un utilisateur par ID
     */
    List<UserBook> findByUserId(Long userId);
    
    /**
     * Trouve les livres d'un utilisateur par statut
     */
    List<UserBook> findByUserIdAndStatus(Long userId, ReadingStatus status);
    
    /**
     * Trouve un livre spécifique dans la bibliothèque d'un utilisateur
     */
    Optional<UserBook> findByUserIdAndBookId(Long userId, Long bookId);
    
    /**
     * Vérifie si un utilisateur a déjà ce livre dans sa bibliothèque
     */
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    
    /**
     * Compte le nombre de livres par statut pour un utilisateur
     */
    @Query("SELECT COUNT(ub) FROM UserBook ub WHERE ub.user.id = :userId AND ub.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ReadingStatus status);
    
    /**
     * Trouve les livres actuellement en cours de lecture
     */
    @Query("SELECT ub FROM UserBook ub WHERE ub.user.id = :userId AND ub.status = 'IN_PROGRESS' ORDER BY ub.startDate DESC")
    List<UserBook> findCurrentlyReading(@Param("userId") Long userId);
    
    /**
     * Trouve les derniers livres terminés
     */
    @Query("SELECT ub FROM UserBook ub WHERE ub.user.id = :userId AND ub.status = 'COMPLETED' ORDER BY ub.endDate DESC")
    List<UserBook> findRecentlyCompleted(@Param("userId") Long userId);
    
    /**
     * Statistiques: nombre total de livres dans la bibliothèque
     */
    Long countByUserId(Long userId);
}