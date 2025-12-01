package prj.books_teamB_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import prj.books_teamB_spring.entity.Conversation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    @Query("SELECT DISTINCT c FROM Conversation c " +
           "JOIN c.participants p " +
           "WHERE p.user.id = :userId AND p.isActive = true AND c.isActive = true " +
           "ORDER BY c.lastMessageAt DESC NULLS LAST")
    List<Conversation> findByUserIdOrderByLastMessageAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Conversation c " +
           "WHERE c.type = 'DIRECT' AND c.isActive = true " +
           "AND EXISTS (SELECT 1 FROM ConversationParticipant p1 WHERE p1.conversation = c AND p1.user.id = :userId1 AND p1.isActive = true) " +
           "AND EXISTS (SELECT 1 FROM ConversationParticipant p2 WHERE p2.conversation = c AND p2.user.id = :userId2 AND p2.isActive = true)")
    Optional<Conversation> findDirectConversationBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    @Query("SELECT COUNT(p) FROM ConversationParticipant p WHERE p.conversation.id = :conversationId AND p.isActive = true")
    Long countActiveParticipants(@Param("conversationId") Long conversationId);
    
    @Query("SELECT DISTINCT c FROM Conversation c " +
           "JOIN c.participants p " +
           "JOIN c.messages m " +
           "WHERE p.user.id = :userId AND p.isActive = true AND c.isActive = true " +
           "AND (p.lastReadAt IS NULL OR m.createdAt > p.lastReadAt) " +
           "AND m.sender.id != :userId " +
           "ORDER BY c.lastMessageAt DESC")
    List<Conversation> findConversationsWithUnreadMessages(@Param("userId") Long userId);
}
