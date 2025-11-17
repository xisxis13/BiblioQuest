package prj.books_teamB_spring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant une session de lecture individuelle
 * Permet de tracker les sessions et calculer des statistiques
 */
@Entity
@Table(name = "reading_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingSession {
    
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_book_id", nullable = false)
    private UserBook userBook;

    /**
     * Durée de la session en minutes
     */
    @Min(1)
    @Column(nullable = false)
    private Integer duration;

    /**
     * Nombre de pages lues pendant cette session
     */
    @Min(0)
    @Column(nullable = false)
    private Integer pagesRead;

    /**
     * Date et heure de début de la session
     */
    @NotNull
    @Column(nullable = false)
    private LocalDateTime sessionDate;

    /**
     * Page de début de la session
     */
    @Min(0)
    private Integer startPage;

    /**
     * Page de fin de la session
     */
    @Min(0)
    private Integer endPage;

    /**
     * Notes/commentaires sur cette session de lecture
     */
    @Column(length = 500)
    private String sessionNotes;

    /**
     * Émotion/réaction pendant la lecture (emoji ou texte court)
     */
    @Column(length = 50)
    private String mood;

    /**
     * Si cette session peut être partagée publiquement
     */
    private Boolean isShareable = false;

    // Constructeurs utilitaires
    public ReadingSession(User user, Book book, UserBook userBook, Integer duration, Integer pagesRead) {
        this.user = user;
        this.book = book;
        this.userBook = userBook;
        this.duration = duration;
        this.pagesRead = pagesRead;
        this.sessionDate = LocalDateTime.now();
    }

    // Méthodes utilitaires
    public double getPagesPerMinute() {
        if (duration == null || duration == 0) return 0.0;
        return (double) pagesRead / duration;
    }

    public boolean isToday() {
        return sessionDate.toLocalDate().equals(java.time.LocalDate.now());
    }
}