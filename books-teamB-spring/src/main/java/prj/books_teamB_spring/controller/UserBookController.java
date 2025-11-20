package prj.books_teamB_spring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.service.UserBookService;

/**
 * Contrôleur pour gérer les collections personnelles de livres
 * Gère les livres possédés par chaque utilisateur
 */
@RestController
@RequestMapping("/api/users/books") 
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UserBookController {

    private final UserBookService userBookService;

    public UserBookController(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    /**
     * POST /api/users/books - Ajoute un livre à la collection de l'utilisateur
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Book> addBookToCollection(
            @RequestHeader("X-Username") String username,
            @RequestBody Book book) {
        Book savedBook = userBookService.addBookForUser(username, book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    /**
     * GET /api/users/books - Liste tous les livres de l'utilisateur connecté
     */
    @GetMapping
    public ResponseEntity<List<Book>> listMyBooks(
            @RequestHeader("X-Username") String username) {
        List<Book> books = userBookService.listBooksForUser(username);
        return ResponseEntity.ok(books);
    }

    /**
     * GET /api/users/books/{id} - Récupère un livre spécifique de la collection
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getMyBook(
            @RequestHeader("X-Username") String username,
            @PathVariable Long id) {
        Book book = userBookService.getBookForUser(username, id);
        return ResponseEntity.ok(book);
    }

    /**
     * DELETE /api/users/books/{id} - Supprime un livre de la collection
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> removeBookFromCollection(
            @RequestHeader("X-Username") String username,
            @PathVariable Long id) {
        userBookService.deleteBookForUser(username, id);
        return ResponseEntity.noContent().build();
    }
}