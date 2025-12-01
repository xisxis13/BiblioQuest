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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)  // Utiliser seulement l'ID
@ToString(exclude = { "myBooks" })                  // Exclure la collection
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include  // Inclure seulement l'ID pour equals/hashCode
    private long id;

    @NotBlank(message = "Username is required")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")

    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    @Column(nullable = false)
    private Role role;

    @ManyToMany
    @JoinTable(
        name = "user_books", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> myBooks = new HashSet<>();

    // Méthodes helper pour gérer les livres avec synchronisation bidirectionnelle
    
    public void addBook(Book book) {
        if (book != null) {
            this.myBooks.add(book);
            book.getOwners().add(this); // Synchronisation bidirectionnelle
        }
    }

    public void removeBook(Book book) {
        if (book != null) {
            this.myBooks.remove(book);
            book.getOwners().remove(this); // Synchronisation bidirectionnelle
        }
    }

    public boolean hasBook(Book book) {
        return book != null && this.myBooks.contains(book);
    }
    
    public int getBooksCount() {
        return this.myBooks.size();
    }
    
    public void clearBooks() {
        // Copie pour éviter ConcurrentModificationException
        Set<Book> booksCopy = new HashSet<>(this.myBooks);
        booksCopy.forEach(this::removeBook);
    }
}