package prj.books_teamB_spring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "book"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserBook {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @EqualsAndHashCode.Include
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    @EqualsAndHashCode.Include
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    @Min(0)
    private Integer progress = 0;

    private LocalDate startDate;
    private LocalDate endDate;

    @Min(1) @Max(5)
    private Integer rating;

    @Column(length = 1000)
    private String personalNote;

    public UserBook(User user, Book book) {
        this.user = user;
        this.book = book;
        this.status = ReadingStatus.TO_READ;
        this.progress = 0;
    }
    
    public UserBook(User user, Book book, ReadingStatus status) {
        this.user = user;
        this.book = book;
        this.status = status;
        this.progress = 0;
    }
}