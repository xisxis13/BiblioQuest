package prj.books_teamB_spring.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import prj.books_teamB_spring.dto.BookDetailDto;
import prj.books_teamB_spring.dto.BookSearchFilters;
import prj.books_teamB_spring.dto.BookSummaryDto;
import prj.books_teamB_spring.service.BookService;

/**
 * Controller pour le catalogue public des livres
 * Endpoints: /api/catalog/*
 */
@RestController
@RequestMapping("/api/catalog")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CatalogController {

    private final BookService bookService;

    public CatalogController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Recherche de livres dans le catalogue public
     * Endpoint: GET /api/catalog/search
     */
    @GetMapping("/search")
    public ResponseEntity<Page<BookSummaryDto>> searchBooks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        BookSearchFilters filters = new BookSearchFilters(author, subject, year, title);
        Pageable pageable = PageRequest.of(page, size);
        Page<BookSummaryDto> results = bookService.searchBooks(filters, pageable);
        
        return ResponseEntity.ok(results);
    }

    /**
     * Récupère les détails d'un livre par son ID
     * Endpoint: GET /api/catalog/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailDto> getBookDetails(@PathVariable Long id) {
        BookDetailDto book = bookService.findBookById(id);
        return ResponseEntity.ok(book);
    }
}
