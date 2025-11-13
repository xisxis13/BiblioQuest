package prj.books_teamB_spring.model;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;


    @ManyToMany
    @JoinTable(
        name = "book_author", // Nom de la table qui fera le lien
        joinColumns = @JoinColumn(name = "book_id"), 
        inverseJoinColumns = @JoinColumn(name = "author_id") 
    )
    private Set<Author> authors = new HashSet<>();
   
    @ManyToOne
    @JoinColumn(name = "subject_id") // C'est une bonne pratique de l'ajouter
    private Subject subject; 

    private String desc_txt; 
    private String cover;
    private String serie_id;
    private String serie_pos;
    
    /**
     * Nombre total de pages du livre (pour calculs de progression)
     */
    private Integer totalPages;
    
    /**
     * ISBN du livre (pour identification unique)
     */
    private String isbn;
    
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    
}
