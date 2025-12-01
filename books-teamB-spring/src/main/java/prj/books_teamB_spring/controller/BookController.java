package prj.books_teamB_spring.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import prj.books_teamB_spring.dto.BookDetailDto;
import prj.books_teamB_spring.dto.BookSearchFilters;
import prj.books_teamB_spring.dto.BookSummaryDto;
import prj.books_teamB_spring.service.BookService;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Endpoint Public : Recherche de livres
     * GET /api/books/search?author=Tolkien
     */
    @GetMapping("/search")
    public ResponseEntity<Page<BookSummaryDto>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String subject,
            Pageable pageable) {
        
        // Construction du filtre (DTO)
        BookSearchFilters filters = new BookSearchFilters();
        filters.setTitle(title);
        filters.setAuthor(author);
        filters.setSubject(subject);

        Page<BookSummaryDto> results = bookService.searchBooks(filters, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Endpoint Public : Détail d'un livre
     * GET /api/books/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailDto> getBook(@PathVariable Long id) {
        // Utilise la méthode publique findBookById qui retourne un DTO
        BookDetailDto bookDto = bookService.findBookById(id);
        return ResponseEntity.ok(bookDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@RequestHeader(name = "X-Username", required = false) String username,
            @PathVariable Long id) {
        bookService.deleteBookForUser(username, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/add")
    public ResponseEntity<BookDetailDto> createBook(@Valid @RequestBody BookDetailDto bookDto) {
        BookDetailDto created = bookService.createBook(bookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDetailDto> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDetailDto bookDto) {
        BookDetailDto updated = bookService.updateBook(id, bookDto);
        return ResponseEntity.ok(updated);
    }
}
