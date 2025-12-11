package prj.books_teamB_spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import prj.books_teamB_spring.dto.BookDto;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.dto.AuthorDto;
import prj.books_teamB_spring.service.BookService;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable Long id) {
        BookDto bookDto = bookService.findById(id);
        return ResponseEntity.ok(bookDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/add")
    public ResponseEntity<BookDto> addBook(@Valid @RequestBody BookDto bookDto) {
        BookDto created = bookService.addBook(bookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{bookId}/authors")
    public ResponseEntity<Book> addAuthor(
            @PathVariable Long bookId,
            @Valid @RequestBody AuthorDto authorDto) {
        Book updated = bookService.addAuthor(bookId, authorDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{bookId}/authors/{authorId}")
    public ResponseEntity<Book> deleteAuthor(
            @PathVariable Long bookId,
            @PathVariable Long authorId) {
        Book updated = bookService.deleteAuthor(bookId, authorId);
        return ResponseEntity.ok(updated);
    }
}
