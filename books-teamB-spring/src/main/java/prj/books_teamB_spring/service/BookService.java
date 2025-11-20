package prj.books_teamB_spring.service;

<<<<<<< HEAD
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.repository.BookRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public List<Book> searchBooks(String query) {
        // Pour l'instant recherche simple par titre, peut être étendu plus tard
        return (List<Book>) bookRepository.findAll();
    }

    public List<Book> getAllBooks() {
        return (List<Book>) bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Livre introuvable"));
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
=======
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

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    private User requireUser(String username) {
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing X-Username header");
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
    }

    public Book addBookForUser(String username, Book book) {
        User user = requireUser(username);
        // Ignore any client-provided id: ensure a new entity is created
        book.setId(0L);
        Book saved = bookRepository.save(book);
        user.getMyBooks().add(saved);
        userRepository.save(user);
        return saved;
    }

    public List<Book> listBooksForUser(String username) {
        User user = requireUser(username);
        Set<Book> books = user.getMyBooks();
        return books.stream().collect(Collectors.toList());
    }

    public Book getBookForUser(String username, Long id) {
        User user = requireUser(username);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        if (!user.getMyBooks().contains(book)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this book");
        }
        return book;
    }

    public void deleteBookForUser(String username, Long id) {
        User user = requireUser(username);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        if (!user.getMyBooks().contains(book)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this book");
        }
        user.getMyBooks().remove(book);
        userRepository.save(user);
>>>>>>> a76d254ab4455776398815b3844f2de757eeac8d
    }
}
