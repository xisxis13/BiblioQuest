package prj.books_teamB_spring.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)  // Utiliser seulement l'ID
@ToString(exclude = {"authors", "subject","owners"})         // Exclure les relations
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include  // Inclure seulement l'ID pour equals/hashCode
    private long id;
    
    private String title;

    @ManyToMany
    @JoinTable(
        name = "book_author",
        joinColumns = @JoinColumn(name = "book"),
        inverseJoinColumns = @JoinColumn(name = "author")
    )
    private Set<Author> authors = new HashSet<>();
   
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToMany(mappedBy = "myBooks")  // Relation inverse avec User
    private Set<User> owners = new HashSet<>();

    @Column(length = 2000)
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