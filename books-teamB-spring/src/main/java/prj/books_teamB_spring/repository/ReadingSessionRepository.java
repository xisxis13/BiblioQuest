package prj.books_teamB_spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import prj.books_teamB_spring.model.ReadingSession;

public interface ReadingSessionRepository extends JpaRepository<ReadingSession, Long> {
    

    @Query(
        value = """
            SELECT *
            FROM reading_sessions
            WHERE user_id = :userId
        """,
        nativeQuery = true
    )
    List<ReadingSession> findByUserId(@Param("userId") Long userId);

    @Query("SELECT rs FROM ReadingSession rs WHERE rs.userId = :userId AND rs.bookId = :bookId")
    List<ReadingSession> findByUserIdAndBookId(@Param("userId") Long userId,
            @Param("bookId") Long bookId);

}