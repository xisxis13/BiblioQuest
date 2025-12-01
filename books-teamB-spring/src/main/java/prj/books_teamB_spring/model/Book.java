package prj.books_teamB_spring.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "authors", "subject", "userBooks" })
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

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

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE)
    private Set<UserBook> userBooks = new HashSet<>();

    @Column(length = 2000)
    private String desc_txt;
    private String cover;
    private int serie_id;
    private int serie_pos;
    
    /**
     * Nombre total de pages du livre (pour calculs de progression)
     */
    private Integer totalPages;
    private String isbn;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}