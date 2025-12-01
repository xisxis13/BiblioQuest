package prj.books_teamB_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import prj.books_teamB_spring.entity.UserOnlineStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserOnlineStatusRepository extends JpaRepository<UserOnlineStatus, Long> {
    
    Optional<UserOnlineStatus> findByUserId(Long userId);
    
    List<UserOnlineStatus> findByIsOnlineTrue();
    
    @Query("SELECT u FROM UserOnlineStatus u WHERE u.userId IN :userIds AND u.isOnline = true")
    List<UserOnlineStatus> findOnlineUsersByIds(@Param("userIds") List<Long> userIds);
    
    @Modifying
    @Query("UPDATE UserOnlineStatus u SET u.isOnline = :isOnline, u.lastSeenAt = :lastSeenAt, u.updatedAt = :updatedAt WHERE u.userId = :userId")
    void updateOnlineStatus(@Param("userId") Long userId, 
                           @Param("isOnline") Boolean isOnline, 
                           @Param("lastSeenAt") LocalDateTime lastSeenAt,
                           @Param("updatedAt") LocalDateTime updatedAt);
    
    @Modifying
    @Query("UPDATE UserOnlineStatus u SET u.statusMessage = :statusMessage, u.updatedAt = :updatedAt WHERE u.userId = :userId")
    void updateStatusMessage(@Param("userId") Long userId, 
                            @Param("statusMessage") String statusMessage,
                            @Param("updatedAt") LocalDateTime updatedAt);
    
    @Modifying
    @Query("UPDATE UserOnlineStatus u SET u.isOnline = false, u.updatedAt = :updatedAt")
    void markAllUsersOffline(@Param("updatedAt") LocalDateTime updatedAt);
    
    Long countByIsOnlineTrue();
}
