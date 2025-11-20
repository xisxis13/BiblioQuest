package prj.books_teamB_spring.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
    private User user;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    @Column(name = "number_of_pages")
    private Integer numberOfPages;

    @Column(name = "physical_format")
    private String physicalFormat;

    @Column(length = 10)
    private String isbn;

    @Column(length = 13)
    private String isbn13;

    private String cover;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
        name = "EDITION_PUBLISHER",
        joinColumns = @JoinColumn(name = "edition_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @jakarta.persistence.PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @jakarta.persistence.PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
