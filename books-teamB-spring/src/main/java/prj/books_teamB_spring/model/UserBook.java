package prj.books_teamB_spring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entité représentant un livre dans la bibliothèque d'un utilisateur
 * Gère le statut de lecture et la progression
 */
@Entity
@Table(name = "user_books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBook {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    /**
     * Progression en pages lues (0 à nombre total de pages du livre)
     */
    @Min(0)
    private Integer progress = 0;

    /**
     * Date de début de lecture
     */
    private LocalDate startDate;

    /**
     * Date de fin de lecture (si terminé ou abandonné)
     */
    private LocalDate endDate;

    /**
     * Note personnelle sur 5 étoiles (optionnelle)
     */
    @Min(1)
    @Max(5)
    private Integer rating;

    /**
     * Commentaire/avis personnel (optionnel)
     */
    @Column(length = 1000)
    private String personalNote;

    // Constructeurs utilitaires
    public UserBook(User user, Book book, ReadingStatus status) {
        this.user = user;
        this.book = book;
        this.status = status;
        this.progress = 0;
    }

    // Méthodes utilitaires
    public boolean isCompleted() {
        return status == ReadingStatus.COMPLETED;
    }

    public boolean isInProgress() {
        return status == ReadingStatus.IN_PROGRESS;
    }

    public double getProgressPercentage() {
        if (book == null || progress == null || book.getTotalPages() == null || book.getTotalPages() == 0) {
            return 0.0;
        }
        return Math.min(100.0, (progress.doubleValue() / book.getTotalPages()) * 100.0);
    }
    
    public boolean isFinished() {
        return book != null && progress != null && book.getTotalPages() != null 
            && progress >= book.getTotalPages();
    }
}