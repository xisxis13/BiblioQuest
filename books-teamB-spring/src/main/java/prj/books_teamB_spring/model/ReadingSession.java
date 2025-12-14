package prj.books_teamB_spring.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
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

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumns({
        @JoinColumn(name = "user_id", referencedColumnName = "userId"), // "name" = colonne dans reading_sessions
        @JoinColumn(name = "book_id", referencedColumnName = "bookId")  // "referencedColumnName" = champ dans UserBookId (ou colonne cible)
    })
    private UserBook userBook;

    @Min(1)
    @Column(nullable = false)
    private Integer duration;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime sessionDate;

    @Min(0)
    private Integer startPage;

    @Min(0)
    private Integer endPage;

    @Column(length = 500)
    private String sessionNotes;

    // Constructeur utilitaire
    public ReadingSession(UserBook userBook, Integer duration, Integer startPage, Integer endPage, String notes) {
        this.userBook = userBook;
        this.duration = duration;
        this.sessionDate = LocalDateTime.now();
        this.startPage = startPage;
        this.endPage = endPage;
        this.sessionNotes = notes;
    }
}