package prj.books_teamB_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import prj.books_teamB_spring.model.WebSocketMessage;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WebSocketMessageRepository extends JpaRepository<WebSocketMessage, Long> {
    
    // Récupérer les messages publics récents
    @Query("SELECT m FROM WebSocketMessage m WHERE m.room = 'public' AND m.isDeleted = false ORDER BY m.createdAt DESC")
    Page<WebSocketMessage> findPublicMessages(Pageable pageable);
    
    // Récupérer les messages publics récents (liste simple)
    @Query("SELECT m FROM WebSocketMessage m WHERE m.room = 'public' AND m.isDeleted = false ORDER BY m.createdAt DESC")
    List<WebSocketMessage> findRecentPublicMessages(Pageable pageable);
    
    // Récupérer les messages privés entre deux utilisateurs
    @Query("SELECT m FROM WebSocketMessage m WHERE " +
           "((m.senderName = :user1 AND m.receiverName = :user2) OR " +
           "(m.senderName = :user2 AND m.receiverName = :user1)) " +
           "AND m.type = 'PRIVATE' AND m.isDeleted = false " +
           "ORDER BY m.createdAt ASC")
    List<WebSocketMessage> findPrivateMessagesBetweenUsers(
        @Param("user1") String user1, 
        @Param("user2") String user2
    );
    
    // Récupérer les messages récents d'un utilisateur
    @Query("SELECT m FROM WebSocketMessage m WHERE " +
           "(m.senderName = :username OR m.receiverName = :username) " +
           "AND m.isDeleted = false " +
           "ORDER BY m.createdAt DESC")
    List<WebSocketMessage> findMessagesByUser(@Param("username") String username, Pageable pageable);
    
    // Compter les messages non lus pour un utilisateur
    @Query("SELECT COUNT(m) FROM WebSocketMessage m WHERE " +
           "m.receiverName = :username AND m.type = 'PRIVATE' " +
           "AND m.createdAt > :lastReadTime AND m.isDeleted = false")
    Long countUnreadMessagesForUser(
        @Param("username") String username, 
        @Param("lastReadTime") LocalDateTime lastReadTime
    );
    
    // Récupérer les messages depuis une certaine date
    @Query("SELECT m FROM WebSocketMessage m WHERE " +
           "m.room = 'public' AND m.createdAt >= :since " +
           "AND m.isDeleted = false ORDER BY m.createdAt ASC")
    List<WebSocketMessage> findPublicMessagesSince(@Param("since") LocalDateTime since);
    
    // Supprimer les anciens messages (soft delete)
    @Query("UPDATE WebSocketMessage m SET m.isDeleted = true WHERE m.createdAt < :cutoffDate")
    void softDeleteOldMessages(@Param("cutoffDate") LocalDateTime cutoffDate);
}
