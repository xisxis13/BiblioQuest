package prj.books_teamB_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import prj.books_teamB_spring.dto.BookDetailDto;
import prj.books_teamB_spring.dto.BookSearchFilters;
import prj.books_teamB_spring.dto.BookSummaryDto;
import prj.books_teamB_spring.exception.BookNotFoundException;
import prj.books_teamB_spring.mapper.BookMapper;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.specification.BookSpecification;


@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    /**
     * Trouve un livre par son ID et retourne le DTO détaillé
     * @param id L'identifiant du livre
     * @return BookDetailDto avec toutes les informations
     * @throws BookNotFoundException si le livre n'existe pas
     */
    public BookDetailDto findBookById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException(id));
        return BookMapper.toDetailDto(book);
    }

    /**
     * Recherche des livres avec filtres dynamiques et pagination
     * @param filters Les filtres de recherche (auteur, genre, année, etc.)
     * @param pageable Configuration de pagination (page, taille, tri)
     * @return Page de BookSummaryDto
     */
    public Page<BookSummaryDto> searchBooks(BookSearchFilters filters, Pageable pageable) {
        Specification<Book> spec = BookSpecification.withFilters(filters);
        Page<Book> books = bookRepository.findAll(spec, pageable);
        return books.map(BookMapper::toSummaryDto);
    }
}