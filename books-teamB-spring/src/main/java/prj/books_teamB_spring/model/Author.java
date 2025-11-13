package prj.books_teamB_spring.model;


import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String firstName;
    private String lastName; 
    
   // --- ICI LA CORRECTION ---
    // 'mappedBy' doit pointer vers le nom du champ
    // dans la classe 'Book' : "authors"
    @ManyToMany(mappedBy = "authors") // Corrigé (au lieu de "books")
    private Set<Book> books = new HashSet<>();
    
}

