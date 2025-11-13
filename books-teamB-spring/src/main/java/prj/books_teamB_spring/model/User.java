package prj.books_teamB_spring.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "myBooks" }) // Exclure la collection bidirectionnelle
@ToString(exclude = { "myBooks" }) // Éviter aussi les problèmes avec toString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany
    @JoinTable(name = "user_books", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "book_id"))
    private Set<Book> myBooks = new HashSet<>();

    // Méthodes pour gérer les livres avec synchronisation bidirectionnelle
    public void addBook(Book book) {
        if (book != null) {
            this.myBooks.add(book);
            book.getOwners().add(this); // Synchronisation
        }
    }

    public void removeBook(Book book) {
        if (book != null) {
            this.myBooks.remove(book);
            book.getOwners().remove(this); // Synchronisation
        }
    }

    public boolean hasBook(Book book) {
        return book != null && this.myBooks.contains(book);
    }
}
