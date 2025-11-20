package prj.books_teamB_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour encapsuler les filtres de recherche de livres
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchFilters {
    
    /**
     * Recherche par nom d'auteur (partiel)
     */
    private String author;
    
    /**
     * Recherche par genre/sujet (partiel)
     */
    private String subject;
    
    /**
     * Recherche par année de publication (exacte)
     */
    private Integer year;
    
    /**
     * Recherche par titre (partiel)
     */
    private String title;
    

}
