package prj.books_teamB_spring;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.Subject;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.UserRepository;

@DataJpaTest
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
        // Créer un sujet
        subject = new Subject();
        subject.setText("Science-Fiction");

        // Créer des livres
        book1 = new Book();
        book1.setTitle("1984");
        book1.setDesc_txt("Un roman dystopique");
        book1.setSubject(subject);
        book1.setCreated_at(LocalDateTime.now());
        book1.setUpdated_at(LocalDateTime.now());

        book2 = new Book();
        book2.setTitle("Brave New World");
        book2.setDesc_txt("Un roman de contrôle social");
        book2.setSubject(subject);
        book2.setCreated_at(LocalDateTime.now());
        book2.setUpdated_at(LocalDateTime.now());

        // Créer un utilisateur
        user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setRole(Role.USER);
    }

    // Test 1: Test ajout d'un livre à un utilisateur
    @Test
    void testAddBookToUser() {
        bookRepository.save(book1);
        user.addBook(book1);
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getMyBooks(), "La collection myBooks ne doit pas être null");
        assertEquals(1, savedUser.getMyBooks().size(), "L'utilisateur doit avoir 1 livre");
        assertTrue(savedUser.getMyBooks().contains(book1), "Le livre 1984 doit être dans la collection");
    }

    // Test 2: Test ajout de plusieurs livres
    @Test
    void testAddMultipleBooksToUser() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        user.addBook(book1);
        user.addBook(book2);
        User savedUser = userRepository.save(user);

        assertEquals(2, savedUser.getMyBooks().size(), "L'utilisateur doit avoir 2 livres");
        assertTrue(savedUser.getMyBooks().contains(book1), "Le livre 1 doit être présent");
        assertTrue(savedUser.getMyBooks().contains(book2), "Le livre 2 doit être présent");
    }

    // Test 3: Test suppression d'un livre d'un utilisateur
    @Test
    void testRemoveBookFromUser() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        user.addBook(book1);
        user.addBook(book2);
        User savedUser = userRepository.save(user);

        assertEquals(2, savedUser.getMyBooks().size(), "L'utilisateur doit avoir 2 livres initialement");

        user.removeBook(book1);
        savedUser = userRepository.save(user);

        assertEquals(1, savedUser.getMyBooks().size(), "L'utilisateur doit avoir 1 livre après suppression");
        assertFalse(savedUser.getMyBooks().contains(book1), "Le livre 1984 ne doit plus être présent");
        assertTrue(savedUser.getMyBooks().contains(book2), "Le livre 2 doit toujours être présent");
    }

    // Test 4: Test vérification si l'utilisateur possède un livre
    @Test
    void testHasBook() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        user.addBook(book1);
        User savedUser = userRepository.save(user);

        assertTrue(savedUser.hasBook(book1), "L'utilisateur doit avoir le livre 1");
        assertFalse(savedUser.hasBook(book2), "L'utilisateur ne doit pas avoir le livre 2");
    }

    // Test 5: Test hasBook avec null
    @Test
    void testHasBookWithNull() {
        User savedUser = userRepository.save(user);
        assertFalse(savedUser.hasBook(null), "hasBook(null) doit retourner false");
    }

    // Test 6: Test récupération des livres d'un utilisateur
    @Test
    void testGetUserBooks() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        user.addBook(book1);
        user.addBook(book2);
        User savedUser = userRepository.save(user);

        long userId = savedUser.getId();
        Optional<User> retrievedUser = userRepository.findById(userId);

        assertTrue(retrievedUser.isPresent(), "L'utilisateur doit être trouvé");
        Set<Book> userBooks = retrievedUser.get().getMyBooks();
        
        assertNotNull(userBooks, "La collection myBooks ne doit pas être null");
        assertEquals(2, userBooks.size(), "L'utilisateur doit avoir 2 livres");
        assertTrue(userBooks.contains(book1), "Le livre 1 doit être dans la collection");
        assertTrue(userBooks.contains(book2), "Le livre 2 doit être dans la collection");
    }

    // Test 7: Test persistance de la relation en base de données
    @Test
    void testRelationPersistence() {
        bookRepository.save(book1);
        user.addBook(book1);
        User savedUser = userRepository.save(user);
        long userId = savedUser.getId();

        // Récupérer l'utilisateur depuis la base
        Optional<User> retrievedUser = userRepository.findById(userId);
        assertTrue(retrievedUser.isPresent(), "L'utilisateur doit exister en base");

        Set<Book> retrievedBooks = retrievedUser.get().getMyBooks();
        assertEquals(1, retrievedBooks.size(), "L'utilisateur doit avoir 1 livre en base");
        
        Book retrievedBook = retrievedBooks.iterator().next();
        assertEquals("1984", retrievedBook.getTitle(), "Le titre du livre doit être '1984'");
    }

    // Test 8: Test ajout d'un livre null
    @Test
    void testAddNullBook() {
        User savedUser = userRepository.save(user);
        
        savedUser.addBook(null);
        savedUser = userRepository.save(savedUser);

        assertEquals(0, savedUser.getMyBooks().size(), "L'utilisateur ne doit avoir aucun livre");
    }

    // Test 9: Test suppression d'un livre null
    @Test
    void testRemoveNullBook() {
        bookRepository.save(book1);
        user.addBook(book1);
        User savedUser = userRepository.save(user);

        assertEquals(1, savedUser.getMyBooks().size(), "L'utilisateur doit avoir 1 livre");

        savedUser.removeBook(null);
        savedUser = userRepository.save(savedUser);

        assertEquals(1, savedUser.getMyBooks().size(), "L'utilisateur doit toujours avoir 1 livre");
    }

    // Test 10: Test collections vides
    @Test
    void testEmptyBooksCollection() {
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getMyBooks(), "La collection myBooks ne doit pas être null");
        assertTrue(savedUser.getMyBooks().isEmpty(), "La collection myBooks doit être vide");
        assertEquals(0, savedUser.getMyBooks().size(), "La taille doit être 0");
    }

    // Test 11: Test relation bidirectionnelle
    @Test
    void testBidirectionalRelation() {
        bookRepository.save(book1);
        user.addBook(book1);
        User savedUser = userRepository.save(user);

        long bookId = book1.getId();
        Optional<Book> retrievedBook = bookRepository.findById(bookId);

        assertTrue(retrievedBook.isPresent(), "Le livre doit exister en base");
        Set<User> bookOwners = retrievedBook.get().getOwners();

        assertNotNull(bookOwners, "La collection owners ne doit pas être null");
        assertEquals(1, bookOwners.size(), "Le livre doit être possédé par 1 utilisateur");
    }

    // Test 12: Test suppression d'un utilisateur avec livres
    @Test
    void testDeleteUserWithBooks() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        user.addBook(book1);
        user.addBook(book2);
        User savedUser = userRepository.save(user);

        long userId = savedUser.getId();
        assertEquals(2, savedUser.getMyBooks().size(), "L'utilisateur doit avoir 2 livres");

        userRepository.deleteById(userId);

        Optional<User> deletedUser = userRepository.findById(userId);
        assertTrue(deletedUser.isEmpty(), "L'utilisateur doit être supprimé");
        
        // Vérifier que les livres existent toujours
        Optional<Book> book1Check = bookRepository.findById(book1.getId());
        Optional<Book> book2Check = bookRepository.findById(book2.getId());
        
        assertTrue(book1Check.isPresent(), "Le livre 1 doit toujours exister");
        assertTrue(book2Check.isPresent(), "Le livre 2 doit toujours exister");
    }
}
