package prj.books_teamB_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour exposer les avis publics sur un livre
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    /**
     * ID de l'avis (correspond à l'ID du UserBook)
     */
    private Long id;
    
    /**
     * ID du livre concerné
     */
    private Long bookId;

    /**
     * Titre du livre (pour affichage)
     */
    private String bookTitle;

    /**
     * Nom d'utilisateur de l'auteur de l'avis
     */
    private String username;

    /**
     * Note sur 5 étoiles (1-5)
     */
    private Integer rating;

    /**
     * Commentaire/avis textuel (max 1000 caractères)
     */
    private String comment;

    /**
     * Date de création de l'avis
     */
    private LocalDate createdAt;

    /**
     * Indique si c'est l'avis de l'utilisateur connecté
     */
    private Boolean isOwnReview;
}