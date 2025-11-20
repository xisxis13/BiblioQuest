package prj.books_teamB_spring.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.UserRepository;

/**
 * Service pour gérer les collections de livres des utilisateurs
 * Responsabilités : ajouter/supprimer des livres, lister les livres possédés
 */
@Service
@Transactional
public class UserBookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public UserBookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Récupère un utilisateur par son username
     * @throws ResponseStatusException si l'utilisateur n'existe pas
     */
    private User requireUser(String username) {
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing X-Username header");
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
    }

    /**
     * Ajoute un livre à la collection d'un utilisateur
     */
    public Book addBookForUser(String username, Book book) {
        User user = requireUser(username);
        book.setId(0L); // Force création d'une nouvelle entité
        Book saved = bookRepository.save(book);
        user.addBook(saved);
        userRepository.save(user);
        return saved;
    }

    /**
     * Liste tous les livres d'un utilisateur
     */
    public List<Book> listBooksForUser(String username) {
        User user = requireUser(username);
        Set<Book> books = user.getMyBooks();
        return books.stream().collect(Collectors.toList());
    }

    /**
     * Récupère un livre spécifique de la collection d'un utilisateur
     */
    public Book getBookForUser(String username, Long id) {
        User user = requireUser(username);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        if (!user.hasBook(book)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this book");
        }
        return book;
    }

    /**
     * Supprime un livre de la collection d'un utilisateur
     */
    public void deleteBookForUser(String username, Long id) {
        User user = requireUser(username);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        if (!user.hasBook(book)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this book");
        }
        user.removeBook(book);
        userRepository.save(user);
    }
}