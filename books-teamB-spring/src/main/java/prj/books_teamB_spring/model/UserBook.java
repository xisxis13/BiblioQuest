package prj.books_teamB_spring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "user_books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserBook {

    @EmbeddedId
    private UserBookId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    @Min(0)
    private Integer progress = 0;

    private LocalDate startDate;
    private LocalDate endDate;

    @Min(1)
    @Max(5)
    private Integer rating;

    @Column(length = 1000)
    private String review;

    @CreationTimestamp
    @Column(name = "review_created_at", updatable = false)
    private LocalDateTime reviewCreatedAt;

    public UserBook(User user, Book book) {
        this.id = new UserBookId(user.getId(), book.getId());
        this.status = ReadingStatus.TO_READ;
        this.progress = 0;
    }

    public UserBook(User user, Book book, ReadingStatus status) {
        this.id = new UserBookId(user.getId(), book.getId());
        this.status = status;
        this.progress = 0;
    }

    public UserBook(Long userId, Long bookId, String review, Integer rating) {
        this.id = new UserBookId(userId, bookId);
        this.review = review;
        this.rating = rating;
        this.progress = 0;
        this.status = ReadingStatus.TO_READ;
    }
}
