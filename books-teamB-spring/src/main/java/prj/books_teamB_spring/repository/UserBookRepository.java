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

    
    /**
     * Récupère tous les avis d'un livre (UserBooks avec rating non null)
     * @param bookId ID du livre
     * @return Liste des UserBook avec avis
     */
    @Query("SELECT ub FROM UserBook ub WHERE ub.book.id = :bookId AND ub.rating IS NOT NULL")
    List<UserBook> findByBookIdAndRatingIsNotNull(@Param("bookId") Long bookId);
    
    /**
     * Compte le nombre d'avis pour un livre
     * @param bookId ID du livre
     * @return Nombre d'avis
     */
    @Query("SELECT COUNT(ub) FROM UserBook ub WHERE ub.book.id = :bookId AND ub.rating IS NOT NULL")
    Long countReviewsByBookId(@Param("bookId") Long bookId);
    
    /**
     * Calcule la note moyenne d'un livre
     * @param bookId ID du livre
     * @return Note moyenne (null si aucun avis)
     */
    @Query("SELECT AVG(ub.rating) FROM UserBook ub WHERE ub.book.id = :bookId AND ub.rating IS NOT NULL")
    Double getAverageRatingByBookId(@Param("bookId") Long bookId);
    
    /**
     * Récupère la distribution des notes pour un livre
     * @param bookId ID du livre
     * @return Liste d'objets [rating, count]
     */
    @Query("SELECT ub.rating, COUNT(ub) FROM UserBook ub WHERE ub.book.id = :bookId AND ub.rating IS NOT NULL GROUP BY ub.rating ORDER BY ub.rating DESC")
    List<Object[]> getRatingDistributionByBookId(@Param("bookId") Long bookId);
    
    /**
     * Récupère tous les avis d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des UserBook avec avis
     */
    @Query("SELECT ub FROM UserBook ub WHERE ub.user.id = :userId AND ub.rating IS NOT NULL ORDER BY ub.endDate DESC")
    List<UserBook> findUserReviews(@Param("userId") Long userId);
    
    /**
     * Vérifie si un utilisateur a déjà laissé un avis sur un livre
     * @param userId ID de l'utilisateur
     * @param bookId ID du livre
     * @return true si un avis existe
     */
    @Query("SELECT CASE WHEN COUNT(ub) > 0 THEN true ELSE false END FROM UserBook ub WHERE ub.user.id = :userId AND ub.book.id = :bookId AND ub.rating IS NOT NULL")
    boolean hasUserReviewedBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}