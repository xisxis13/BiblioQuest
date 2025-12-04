package prj.books_teamB_spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.Subject;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.SubjectRepository;
import prj.books_teamB_spring.repository.UserRepository;
import prj.books_teamB_spring.service.UserBookService;

/**
 * Tests pour UserBookService
 * Teste la gestion des collections de livres des utilisateurs
 */
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserBookServiceTest {

    @Autowired
    private UserBookService userBookService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private User testUser;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
        // Nettoyer la base de données
        bookRepository.deleteAll();
        userRepository.deleteAll();
        subjectRepository.deleteAll();

        // Créer un sujet
        testSubject = new Subject();
        testSubject.setText("Fantasy");
        testSubject = subjectRepository.save(testSubject);

        // Créer un utilisateur de test
        testUser = new User();
        testUser.setUsername("alice");
        testUser.setEmail("alice@example.com");
        testUser.setPassword("password123");
        testUser.setRole(Role.USER);
        testUser = userRepository.save(testUser);
    }

    @Test
    void addBookForUser_withValidUser_createsAndAddsBook() {
        // Arrange
        Book book = new Book();
        book.setTitle("The Hobbit");
        book.setDesc_txt("A fantasy adventure");
        book.setSubject(testSubject);
        book.setCover("https://example.com/hobbit.jpg");
        book.setCreated_at(LocalDateTime.now());
        book.setUpdated_at(LocalDateTime.now());

        // Act
        Book savedBook = userBookService.addBookForUser("alice", book);

        // Assert
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isGreaterThan(0);
        assertThat(savedBook.getTitle()).isEqualTo("The Hobbit");

        // Vérifier que le livre est bien dans la collection de l'utilisateur
        User updatedUser = userRepository.findByUsername("alice").orElseThrow();
        assertThat(updatedUser.getMyBooks()).hasSize(1);
        assertThat(updatedUser.getMyBooks()).contains(savedBook);
    }

    @Test
    void addBookForUser_withInvalidUser_throwsUnauthorized() {
        // Arrange
        Book book = new Book();
        book.setTitle("Test Book");

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.addBookForUser("unknown", book)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Unknown user");
    }

    @Test
    void addBookForUser_withNullUsername_throwsUnauthorized() {
        // Arrange
        Book book = new Book();
        book.setTitle("Test Book");

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.addBookForUser(null, book)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Missing X-Username header");
    }

    @Test
    void addBookForUser_withBlankUsername_throwsUnauthorized() {
        // Arrange
        Book book = new Book();
        book.setTitle("Test Book");

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.addBookForUser("   ", book)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Missing X-Username header");
    }

    @Test
    void listBooksForUser_withNoBooks_returnsEmptyList() {
        // Act
        List<Book> books = userBookService.listBooksForUser("alice");

        // Assert
        assertThat(books).isEmpty();
    }

    @Test
    void listBooksForUser_withMultipleBooks_returnsAllBooks() {
        // Arrange
        Book book1 = createAndSaveBook("Book 1");
        Book book2 = createAndSaveBook("Book 2");

        testUser.addBook(book1);
        testUser.addBook(book2);
        userRepository.save(testUser);

        // Act
        List<Book> books = userBookService.listBooksForUser("alice");

        // Assert
        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getTitle)
            .containsExactlyInAnyOrder("Book 1", "Book 2");
    }

    @Test
    void listBooksForUser_withInvalidUser_throwsUnauthorized() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.listBooksForUser("unknown")
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Unknown user");
    }

    @Test
    void getBookForUser_whenUserOwnsBook_returnsBook() {
        // Arrange
        Book book = createAndSaveBook("Owned Book");
        testUser.addBook(book);
        userRepository.save(testUser);

        // Act
        Book retrievedBook = userBookService.getBookForUser("alice", book.getId());

        // Assert
        assertThat(retrievedBook).isNotNull();
        assertThat(retrievedBook.getId()).isEqualTo(book.getId());
        assertThat(retrievedBook.getTitle()).isEqualTo("Owned Book");
    }

    @Test
    void getBookForUser_whenUserDoesNotOwnBook_throwsForbidden() {
        // Arrange
        Book book = createAndSaveBook("Not Owned Book");
        // Ne pas ajouter le livre à l'utilisateur

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.getBookForUser("alice", book.getId())
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(exception.getReason()).isEqualTo("Not owner of this book");
    }

    @Test
    void getBookForUser_withNonExistentBook_throwsNotFound() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.getBookForUser("alice", 99999L)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Book not found");
    }

    @Test
    void getBookForUser_withInvalidUser_throwsUnauthorized() {
        // Arrange
        Book book = createAndSaveBook("Test Book");

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.getBookForUser("unknown", book.getId())
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Unknown user");
    }

    @Test
    void deleteBookForUser_whenUserOwnsBook_removesBook() {
        // Arrange
        Book book = createAndSaveBook("Book to Delete");
        testUser.addBook(book);
        userRepository.save(testUser);

        Long bookId = book.getId();
        assertThat(testUser.getMyBooks()).hasSize(1);

        // Act
        userBookService.deleteBookForUser("alice", bookId);

        // Assert
        User updatedUser = userRepository.findByUsername("alice").orElseThrow();
        assertThat(updatedUser.getMyBooks()).isEmpty();
        
        // Vérifier que le livre existe toujours en base
        assertThat(bookRepository.findById(bookId)).isPresent();
    }

    @Test
    void deleteBookForUser_whenUserDoesNotOwnBook_throwsForbidden() {
        // Arrange
        Book book = createAndSaveBook("Not Owned Book");
        // Ne pas ajouter le livre à l'utilisateur

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.deleteBookForUser("alice", book.getId())
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(exception.getReason()).isEqualTo("Not owner of this book");
    }

    @Test
    void deleteBookForUser_withNonExistentBook_throwsNotFound() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.deleteBookForUser("alice", 99999L)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Book not found");
    }

    @Test
    void deleteBookForUser_withInvalidUser_throwsUnauthorized() {
        // Arrange
        Book book = createAndSaveBook("Test Book");

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.deleteBookForUser("unknown", book.getId())
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("Unknown user");
    }

    @Test
    void addBookForUser_multipleUsers_isolatesCollections() {
        // Arrange
        User bob = new User();
        bob.setUsername("bob");
        bob.setEmail("bob@example.com");
        bob.setPassword("password456");
        bob.setRole(Role.USER);
        bob = userRepository.save(bob);

        Book aliceBook = new Book();
        aliceBook.setTitle("Alice's Book");
        aliceBook.setSubject(testSubject);
        aliceBook.setCreated_at(LocalDateTime.now());
        aliceBook.setUpdated_at(LocalDateTime.now());

        Book bobBook = new Book();
        bobBook.setTitle("Bob's Book");
        bobBook.setSubject(testSubject);
        bobBook.setCreated_at(LocalDateTime.now());
        bobBook.setUpdated_at(LocalDateTime.now());

        // Act
        userBookService.addBookForUser("alice", aliceBook);
        userBookService.addBookForUser("bob", bobBook);

        // Assert
        List<Book> aliceBooks = userBookService.listBooksForUser("alice");
        List<Book> bobBooks = userBookService.listBooksForUser("bob");

        assertThat(aliceBooks).hasSize(1);
        assertThat(aliceBooks.get(0).getTitle()).isEqualTo("Alice's Book");

        assertThat(bobBooks).hasSize(1);
        assertThat(bobBooks.get(0).getTitle()).isEqualTo("Bob's Book");
    }

    @Test
    void addBookForUser_sameBookToDifferentUsers_createsMultipleInstances() {
        // Arrange
        User bob = new User();
        bob.setUsername("bob");
        bob.setEmail("bob@example.com");
        bob.setPassword("password456");
        bob.setRole(Role.USER);
        userRepository.save(bob);

        Book book1 = new Book();
        book1.setTitle("Shared Title");
        book1.setSubject(testSubject);
        book1.setCreated_at(LocalDateTime.now());
        book1.setUpdated_at(LocalDateTime.now());

        Book book2 = new Book();
        book2.setTitle("Shared Title");
        book2.setSubject(testSubject);
        book2.setCreated_at(LocalDateTime.now());
        book2.setUpdated_at(LocalDateTime.now());

        // Act
        Book aliceBook = userBookService.addBookForUser("alice", book1);
        Book bobBook = userBookService.addBookForUser("bob", book2);

        // Assert
        assertThat(aliceBook.getId()).isNotEqualTo(bobBook.getId());
        assertThat(bookRepository.count()).isEqualTo(2);
    }

    @Test
    void deleteBookForUser_withMultipleBooks_removesOnlySpecifiedBook() {
        // Arrange
        Book book1 = createAndSaveBook("Book 1");
        Book book2 = createAndSaveBook("Book 2");
        Book book3 = createAndSaveBook("Book 3");

        testUser.addBook(book1);
        testUser.addBook(book2);
        testUser.addBook(book3);
        userRepository.save(testUser);

        // Act
        userBookService.deleteBookForUser("alice", book2.getId());

        // Assert
        User updatedUser = userRepository.findByUsername("alice").orElseThrow();
        assertThat(updatedUser.getMyBooks()).hasSize(2);
        assertThat(updatedUser.getMyBooks()).extracting(Book::getTitle)
            .containsExactlyInAnyOrder("Book 1", "Book 3");
    }

    @Test
    void listBooksForUser_afterAddingAndDeleting_returnsCorrectList() {
        // Arrange
        Book book1 = createAndSaveBook("Book 1");
        Book book2 = createAndSaveBook("Book 2");

        // Act - Ajouter 2 livres
        testUser.addBook(book1);
        testUser.addBook(book2);
        userRepository.save(testUser);

        List<Book> booksAfterAdd = userBookService.listBooksForUser("alice");
        assertThat(booksAfterAdd).hasSize(2);

        // Act - Supprimer 1 livre
        userBookService.deleteBookForUser("alice", book1.getId());

        List<Book> booksAfterDelete = userBookService.listBooksForUser("alice");

        // Assert
        assertThat(booksAfterDelete).hasSize(1);
        assertThat(booksAfterDelete.get(0).getTitle()).isEqualTo("Book 2");
    }

    // Méthode helper pour créer et sauvegarder un livre
    private Book createAndSaveBook(String title) {
        Book book = new Book();
        book.setTitle(title);
        book.setDesc_txt("Description de " + title);
        book.setSubject(testSubject);
        book.setCover("https://example.com/cover.jpg");
        book.setCreated_at(LocalDateTime.now());
        book.setUpdated_at(LocalDateTime.now());
        return bookRepository.save(book);
    }
}