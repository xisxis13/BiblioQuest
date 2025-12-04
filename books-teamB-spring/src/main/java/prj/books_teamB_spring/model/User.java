package prj.books_teamB_spring.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@ToString(exclude = { "userBooks" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    @Column(nullable = false)
    private Role role;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "last_activity_at")
    private java.time.LocalDateTime lastActivityAt;

    @Column(name = "books_count")
    private Integer booksCount;

    @Column(name = "followers_count")
    private Integer followersCount;

    @Column(name = "following_count")
    private Integer followingCount;

    public User(String username, String email, String password, Role role){
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<UserBook> userBooks = new HashSet<>();

    public void addBook(Book book) {
        if (book != null && !hasBook(book)) {
            UserBook userBook = new UserBook(this, book);
            this.userBooks.add(userBook);
        }
    }

    public void removeBook(Book book) {
        if (book != null) {
            this.userBooks.removeIf(ub -> ub.getBook().equals(book));
        }
    }

    public boolean hasBook(Book book) {
        if (book == null)
            return false;
        return this.userBooks.stream()
                .anyMatch(ub -> ub.getBook().getId().equals(book.getId()));
    }

    public Set<Book> getMyBooks() {
        return this.userBooks.stream()
                .map(UserBook::getBook)
                .collect(Collectors.toSet());
    }
}