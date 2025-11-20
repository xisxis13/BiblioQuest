package prj.books_teamB_spring.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Edition {

    @Id
    @Column(length = 20)
    private String id;

    // Chaque édition est liée à un livre, un livre peut avoir plusieurs éditions
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User librarian;

    @Column(name = "publish_date")
    private LocalDate publish_date;

    @Column(name = "number_of_pages")
    private Integer pages;

    @Column(length = 13)
    private String isbn;

    private String cover;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @jakarta.persistence.PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.created_at = now;
        this.updated_at = now;
    }

    @jakarta.persistence.PreUpdate
    public void onUpdate() {
        this.updated_at = LocalDateTime.now();
    }
}
