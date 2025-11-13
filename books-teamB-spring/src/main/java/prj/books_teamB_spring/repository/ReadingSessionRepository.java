package prj.books_teamB_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prj.books_teamB_spring.model.ReadingSession;

import java.time.LocalDateTime;
import java.util.List;

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
    List<ReadingSession> findByUserIdAndBookId(Long userId, Long bookId);
    
    /**
     * Trouve les sessions d'un utilisateur dans une période
     */
    List<ReadingSession> findByUserIdAndSessionDateBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Trouve les sessions partagées publiquement
     */
    List<ReadingSession> findByIsShareableTrue();
    
    /**
     * Calcule le temps total de lecture d'un utilisateur
     */
    @Query("SELECT SUM(rs.duration) FROM ReadingSession rs WHERE rs.user.id = :userId")
    Long getTotalReadingTimeByUserId(@Param("userId") Long userId);
    
    /**
     * Calcule le nombre total de pages lues par un utilisateur
     */
    @Query("SELECT SUM(rs.pagesRead) FROM ReadingSession rs WHERE rs.user.id = :userId")
    Long getTotalPagesReadByUserId(@Param("userId") Long userId);
    
    /**
     * Calcule le temps de lecture pour un livre spécifique
     */
    @Query("SELECT SUM(rs.duration) FROM ReadingSession rs WHERE rs.user.id = :userId AND rs.book.id = :bookId")
    Long getReadingTimeForBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
    
    /**
     * Trouve les dernières sessions de lecture
     */
    @Query("SELECT rs FROM ReadingSession rs WHERE rs.user.id = :userId ORDER BY rs.sessionDate DESC")
    List<ReadingSession> findRecentSessions(@Param("userId") Long userId);
    
    /**
     * Statistiques par jour: sessions du jour
     */
    @Query("SELECT rs FROM ReadingSession rs WHERE rs.user.id = :userId AND DATE(rs.sessionDate) = CURRENT_DATE ORDER BY rs.sessionDate DESC")
    List<ReadingSession> findTodaySessions(@Param("userId") Long userId);
    
    /**
     * Statistiques par semaine: temps total cette semaine
     */
    @Query("SELECT SUM(rs.duration) FROM ReadingSession rs WHERE rs.user.id = :userId AND rs.sessionDate >= :weekStart")
    Long getWeeklyReadingTime(@Param("userId") Long userId, @Param("weekStart") LocalDateTime weekStart);
    
    /**
     * Moyenne de pages par minute pour un utilisateur
     */
    @Query("SELECT AVG(CAST(rs.pagesRead AS DOUBLE) / rs.duration) FROM ReadingSession rs WHERE rs.user.id = :userId AND rs.duration > 0")
    Double getAverageReadingSpeedByUserId(@Param("userId") Long userId);
}