package prj.books_teamB_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import prj.books_teamB_spring.entity.ConversationParticipant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
    
    Optional<ConversationParticipant> findByConversationIdAndUserIdAndIsActiveTrue(Long conversationId, Long userId);
    
    List<ConversationParticipant> findByConversationIdAndIsActiveTrue(Long conversationId);
    
    List<ConversationParticipant> findByUserIdAndIsActiveTrue(Long userId);
    

    @Modifying
    @Query("UPDATE ConversationParticipant p SET p.lastReadAt = :readAt WHERE p.conversation.id = :conversationId AND p.user.id = :userId")
    void updateLastReadAt(@Param("conversationId") Long conversationId, 
                         @Param("userId") Long userId, 
                         @Param("readAt") LocalDateTime readAt);
    
    boolean existsByConversationIdAndUserIdAndIsActiveTrue(Long conversationId, Long userId);
    
    Long countByConversationIdAndIsActiveTrue(Long conversationId);
    

    @Modifying
    @Query("UPDATE ConversationParticipant p SET p.isActive = false, p.leftAt = :leftAt WHERE p.conversation.id = :conversationId AND p.user.id = :userId")
    void leaveConversation(@Param("conversationId") Long conversationId, 
                          @Param("userId") Long userId, 
                          @Param("leftAt") LocalDateTime leftAt);
}
