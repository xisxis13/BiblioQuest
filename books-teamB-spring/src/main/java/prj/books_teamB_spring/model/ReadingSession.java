package prj.books_teamB_spring.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "user_book_id", nullable = false)
    private UserBook userBook;

    @Min(1)
    @Column(nullable = false)
    private Integer duration;

    @Min(0)
    @Column(nullable = false)
    private Integer pagesRead;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime sessionDate;


    @Min(0)
    private Integer startPage;

    @Min(0)
    private Integer endPage;

    @Column(length = 500)
    private String sessionNotes;


    // Constructeurs utilitaires
    public ReadingSession( UserBook userBook, Integer duration, Integer pagesRead) {
        this.userBook = userBook;
        this.duration = duration;
        this.pagesRead = pagesRead;
        this.sessionDate = LocalDateTime.now();
    }
}