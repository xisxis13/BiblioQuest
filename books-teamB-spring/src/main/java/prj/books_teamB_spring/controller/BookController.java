package prj.books_teamB_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @Autowired
    private BookService bookService;

    /**
     * Récupère les détails d'un livre par son ID
     * GET /api/books/{id}
     * @return BookDetailDto avec toutes les informations du livre
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailDto> getBookById(@PathVariable Long id) {
        BookDetailDto book = bookService.findBookById(id);
        return ResponseEntity.ok(book);
    }

    /**
     * Recherche des livres avec filtres et pagination
     * GET /api/books/search?author=tolkien&genre=fantasy&year=2020&page=0&size=10&sort=title,asc
     * @return Page de BookSummaryDto avec métadonnées de pagination
     */
    @GetMapping("/search")
    public ResponseEntity<Page<BookSummaryDto>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) Integer year,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {

        BookSearchFilters filters = new BookSearchFilters();
        filters.setTitle(title);
        filters.setAuthor(author);
        filters.setSubject(subject);
        filters.setYear(year);

        Page<BookSummaryDto> results = bookService.searchBooks(filters, pageable);
        return ResponseEntity.ok(results);
    }
}