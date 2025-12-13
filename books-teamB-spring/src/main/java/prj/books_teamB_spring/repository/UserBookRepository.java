package prj.books_teamB_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.model.UserBookId;

import java.util.List;
import java.util.Optional;

public interface UserBookRepository extends JpaRepository<UserBook, UserBookId> {

    @Query("SELECT ub FROM UserBook ub WHERE ub.id.userId = :userId")
    List<UserBook> findByUserId(@Param("userId") Long userId);

    @Query("SELECT ub FROM UserBook ub WHERE ub.id.userId = :userId AND ub.id.bookId = :bookId")
    Optional<UserBook> findByUserAndBook(@Param("userId") Long userId,
            @Param("bookId") Long bookId);

    @Query("SELECT ub FROM UserBook ub WHERE ub.id.bookId = :bookId")
    List<UserBook> findByBookId(@Param("bookId") Long bookId);
}