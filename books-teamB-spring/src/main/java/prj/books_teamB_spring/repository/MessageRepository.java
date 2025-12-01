package prj.books_teamB_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import prj.books_teamB_spring.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findByConversationIdAndIsDeletedFalseOrderByCreatedAtDesc(Long conversationId, Pageable pageable);
    
    List<Message> findByConversationIdAndCreatedAtAfterAndIsDeletedFalseOrderByCreatedAtAsc(
        Long conversationId, LocalDateTime after);
    
    @Query("SELECT COUNT(m) FROM Message m " +
           "JOIN ConversationParticipant p ON p.conversation.id = m.conversation.id " +
           "WHERE m.conversation.id = :conversationId " +
           "AND p.user.id = :userId " +
           "AND p.isActive = true " +
           "AND m.sender.id != :userId " +
           "AND m.isDeleted = false " +
           "AND (p.lastReadAt IS NULL OR m.createdAt > p.lastReadAt)")
    Long countUnreadMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.isDeleted = false ORDER BY m.createdAt DESC LIMIT 1")
    Message findLastMessageByConversationId(@Param("conversationId") Long conversationId);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId " +
           "AND LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND m.isDeleted = false ORDER BY m.createdAt DESC")
    List<Message> searchMessagesInConversation(@Param("conversationId") Long conversationId, 
                                             @Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(m) FROM Message m " +
           "JOIN ConversationParticipant p ON p.conversation.id = m.conversation.id " +
           "WHERE p.user.id = :userId " +
           "AND p.isActive = true " +
           "AND m.sender.id != :userId " +
           "AND m.isDeleted = false " +
           "AND (p.lastReadAt IS NULL OR m.createdAt > p.lastReadAt)")
    Long countTotalUnreadMessages(@Param("userId") Long userId);
}
