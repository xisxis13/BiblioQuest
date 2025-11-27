package prj.books_teamB_spring.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}