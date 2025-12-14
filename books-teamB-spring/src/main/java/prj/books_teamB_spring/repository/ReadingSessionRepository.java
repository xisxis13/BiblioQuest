package prj.books_teamB_spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import prj.books_teamB_spring.model.ReadingSession;
import prj.books_teamB_spring.model.UserBook;

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

    @Query("SELECT rs FROM ReadingSession rs WHERE rs.userBook.id.userId = :userId AND rs.userBook.id.bookId = :bookId")
    Optional<UserBook> findByUserIdAndBookId(@Param("userId") Long userId,
            @Param("bookId") Long bookId);

}