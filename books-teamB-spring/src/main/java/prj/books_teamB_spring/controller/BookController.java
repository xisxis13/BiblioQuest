package prj.books_teamB_spring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.service.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@Validated
public class BookController {

    private final BookService bookService;

    // Méthodes simples pour compatibilité avec le frontend existant
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    // Méthodes avancées avec gestion utilisateur
    @PostMapping("/user")
    public ResponseEntity<Book> addBookForUser(@RequestHeader(name = "X-Username", required = false) String username,
            @Valid @RequestBody Book dto) {
        Book created = bookService.addBookForUser(username, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Book>> listBooksForUser(@RequestHeader(name = "X-Username", required = false) String username) {
        List<Book> list = bookService.listBooksForUser(username);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Book> getBookForUser(@RequestHeader(name = "X-Username", required = false) String username,
            @PathVariable Long id) {
        Book dto = bookService.getBookForUser(username, id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@RequestHeader(name = "X-Username", required = false) String username,
            @PathVariable Long id) {
        bookService.deleteBookForUser(username, id);
        return ResponseEntity.noContent().build();
    }
}
