package prj.books_teamB_spring.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;


@Embeddable
public class UserBookId implements Serializable {

    private Long userId;
    private Long bookId;

    public UserBookId() {}

    public UserBookId(Long userId, Long bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

    public Long getUserId(){
        return this.userId;
    }

    public Long getBookId(){
        return this.bookId;
    }
}