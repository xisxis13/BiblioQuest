package prj.books_teamB_spring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.service.BookService;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Header X-Username used to identify user (simple approach for assignment)

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestHeader(name = "X-Username", required = false) String username,
            @Valid @RequestBody Book dto) {
        Book created = bookService.addBookForUser(username, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Book>> listBooks(@RequestHeader(name = "X-Username", required = false) String username) {
        List<Book> list = bookService.listBooksForUser(username);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@RequestHeader(name = "X-Username", required = false) String username,
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
