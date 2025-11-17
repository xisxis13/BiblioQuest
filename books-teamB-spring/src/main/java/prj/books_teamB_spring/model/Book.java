package prj.books_teamB_spring.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@ToString(exclude = {"authors", "subject"})         // Exclure les relations
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include  // Inclure seulement l'ID pour equals/hashCode
    private long id;
    
    private String title;

    @ManyToMany
    @JoinTable(
        name = "book_author",
        joinColumns = @JoinColumn(name = "book_id"), 
        inverseJoinColumns = @JoinColumn(name = "author_id") 
    )
    private Set<Author> authors = new HashSet<>();
   
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject; 

    private String desc_txt; 
    private String cover;
    private String serie_id;
    private String serie_pos;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}