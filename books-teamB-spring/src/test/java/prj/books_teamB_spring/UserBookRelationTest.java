package prj.books_teamB_spring;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.Subject;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.UserRepository;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
public class UserBookRelationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private User user;
    private Book book1;
    private Book book2;
    private Subject subject;

    @BeforeEach
    void setUp() {
        subject = new Subject();
        subject.setText("Science-Fiction");

        book1 = new Book();
        book1.setTitle("1984");
        book1.setDesc_txt("A dystopian novel");
        book1.setSubject(subject);
        book1.setCreated_at(LocalDateTime.now());
        book1.setUpdated_at(LocalDateTime.now());

        book2 = new Book();
        book2.setTitle("Brave New World");
        book2.setDesc_txt("A novel about social control");
        book2.setSubject(subject);
        book2.setCreated_at(LocalDateTime.now());
        book2.setUpdated_at(LocalDateTime.now());

        user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setRole(Role.USER);
    }

    @Test
    void testAddBookToUser() {
        bookRepository.save(book1);
        user.addBook(book1);
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getMyBooks(), "myBooks collection must not be null");
        assertEquals(1, savedUser.getMyBooks().size(), "User must have 1 book");
        assertTrue(savedUser.getMyBooks().contains(book1), "The book 1984 must be in the collection");
    }

    @Test
    void testAddMultipleBooksToUser() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        user.addBook(book1);
        user.addBook(book2);
        User savedUser = userRepository.save(user);

        assertEquals(2, savedUser.getMyBooks().size(), "User must have 2 books");
        assertTrue(savedUser.getMyBooks().contains(book1), "Book 1 must be present");
        assertTrue(savedUser.getMyBooks().contains(book2), "Book 2 must be present");
    }

    @Test
    void testRemoveBookFromUser() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        user.addBook(book1);
        user.addBook(book2);
        User savedUser = userRepository.save(user);

        assertEquals(2, savedUser.getMyBooks().size(), "User must have 2 books initially");

        user.removeBook(book1);
        savedUser = userRepository.save(user);

        assertEquals(1, savedUser.getMyBooks().size(), "User must have 1 book after removal");
        assertFalse(savedUser.getMyBooks().contains(book1), "Book 1984 must no longer be present");
        assertTrue(savedUser.getMyBooks().contains(book2), "Book 2 must still be present");
    }

    @Test
    void testHasBook() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        user.addBook(book1);
        User savedUser = userRepository.save(user);

        assertTrue(savedUser.hasBook(book1), "User must have book 1");
        assertFalse(savedUser.hasBook(book2), "User must not have book 2");
    }

    @Test
    void testHasBookWithNull() {
        User savedUser = userRepository.save(user);
        assertFalse(savedUser.hasBook(null), "hasBook(null) must return false");
    }

    @Test
    void testGetUserBooks() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        user.addBook(book1);
        user.addBook(book2);
        User savedUser = userRepository.save(user);

        long userId = savedUser.getId();
        Optional<User> retrievedUser = userRepository.findById(userId);

        assertTrue(retrievedUser.isPresent(), "User must be found");
        Set<Book> userBooks = retrievedUser.get().getMyBooks();

        assertNotNull(userBooks, "myBooks collection must not be null");
        assertEquals(2, userBooks.size(), "User must have 2 books");
        assertTrue(userBooks.contains(book1), "Book 1 must be in the collection");
        assertTrue(userBooks.contains(book2), "Book 2 must be in the collection");
    }

    @Test
    void testRelationPersistence() {
        bookRepository.save(book1);
        user.addBook(book1);
        User savedUser = userRepository.save(user);
        long userId = savedUser.getId();

        Optional<User> retrievedUser = userRepository.findById(userId);
        assertTrue(retrievedUser.isPresent(), "User must exist in database");

        Set<Book> retrievedBooks = retrievedUser.get().getMyBooks();
        assertEquals(1, retrievedBooks.size(), "User must have 1 book in database");

        Book retrievedBook = retrievedBooks.iterator().next();
        assertEquals("1984", retrievedBook.getTitle(), "Book title must be '1984'");
    }

    @Test
    void testAddNullBook() {
        User savedUser = userRepository.save(user);

        savedUser.addBook(null);
        savedUser = userRepository.save(savedUser);

        assertEquals(0, savedUser.getMyBooks().size(), "User must not have any books");
    }

    @Test
    void testRemoveNullBook() {
        bookRepository.save(book1);
        user.addBook(book1);
        User savedUser = userRepository.save(user);

        assertEquals(1, savedUser.getMyBooks().size(), "User must have 1 book");

        savedUser.removeBook(null);
        savedUser = userRepository.save(savedUser);

        assertEquals(1, savedUser.getMyBooks().size(), "User must still have 1 book");
    }

    @Test
    void testEmptyBooksCollection() {
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getMyBooks(), "myBooks collection must not be null");
        assertTrue(savedUser.getMyBooks().isEmpty(), "myBooks collection must be empty");
        assertEquals(0, savedUser.getMyBooks().size(), "Size must be 0");
    }

    @Test
    void testDeleteUserWithBooks() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        user.addBook(book1);
        user.addBook(book2);
        User savedUser = userRepository.save(user);

        long userId = savedUser.getId();
        assertEquals(2, savedUser.getMyBooks().size(), "User must have 2 books");

        userRepository.deleteById(userId);

        Optional<User> deletedUser = userRepository.findById(userId);
        assertTrue(deletedUser.isEmpty(), "User must be deleted");

        Optional<Book> book1Check = bookRepository.findById(book1.getId());
        Optional<Book> book2Check = bookRepository.findById(book2.getId());

        assertTrue(book1Check.isPresent(), "Book 1 must still exist");
        assertTrue(book2Check.isPresent(), "Book 2 must still exist");
    }
}