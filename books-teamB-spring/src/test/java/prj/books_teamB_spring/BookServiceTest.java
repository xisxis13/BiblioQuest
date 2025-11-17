package prj.books_teamB_spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import prj.books_teamB_spring.dto.BookDetailDto;
import prj.books_teamB_spring.dto.BookSearchFilters;
import prj.books_teamB_spring.dto.BookSummaryDto;
import prj.books_teamB_spring.exception.BookNotFoundException;
import prj.books_teamB_spring.model.Author;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Subject;
import prj.books_teamB_spring.repository.AuthorRepository;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.SubjectRepository;
import prj.books_teamB_spring.service.BookService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private Author tolkien;
    private Author herbert;
    private Subject fantasy;
    private Subject sciFi;

    @BeforeEach
    void setUp() {
        // Nettoyer la base
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        subjectRepository.deleteAll();

        // Créer des auteurs
        tolkien = new Author();
        tolkien.setFirstName("J.R.R.");
        tolkien.setLastName("Tolkien");
        tolkien = authorRepository.save(tolkien);

        herbert = new Author();
        herbert.setFirstName("Frank");
        herbert.setLastName("Herbert");
        herbert = authorRepository.save(herbert);

        // Créer des sujets
        fantasy = new Subject();
        fantasy.setText("Fantasy");
        fantasy = subjectRepository.save(fantasy);

        sciFi = new Subject();
        sciFi.setText("Science Fiction");
        sciFi = subjectRepository.save(sciFi);

        // Créer des livres
        createBook("The Lord of the Rings", tolkien, fantasy, 1954);
        createBook("The Hobbit", tolkien, fantasy, 1937);
        createBook("Dune", herbert, sciFi, 1965);
        createBook("Dune Messiah", herbert, sciFi, 1969);
        createBook("The Silmarillion", tolkien, fantasy, 1977);
    }

    private void createBook(String title, Author author, Subject subject, int year) {
        Book book = new Book();
        book.setTitle(title);
        book.setDesc_txt("Description de " + title);
        book.setCover("https://example.com/cover.jpg");
        book.setSubject(subject);
        book.setCreated_at(LocalDateTime.of(year, 1, 1, 0, 0));
        book.setUpdated_at(LocalDateTime.now());

        Set<Author> authors = new HashSet<>();
        authors.add(author);
        book.setAuthors(authors);

        bookRepository.save(book);
    }

    @Test
    void searchBooks_withAuthorFilter_returnsOnlyBooksFromThatAuthor() {
        // Arrange
        BookSearchFilters filters = new BookSearchFilters();
        filters.setAuthor("Tolkien");
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<BookSummaryDto> results = bookService.searchBooks(filters, pageable);

        // Assert
        assertThat(results.getContent()).hasSize(3);
        assertThat(results.getContent())
            .allMatch(dto -> dto.getAuthorsNames().stream()
                .anyMatch(name -> name.contains("Tolkien")));
    }

    @Test
    void searchBooks_withPagination_returnsCorrectPageSize() {
        // Arrange
        BookSearchFilters filters = new BookSearchFilters(); // Pas de filtre
        Pageable pageable = PageRequest.of(0, 2); // Page 0, 2 éléments

        // Act
        Page<BookSummaryDto> results = bookService.searchBooks(filters, pageable);

        // Assert
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getTotalElements()).isEqualTo(5);
        assertThat(results.getTotalPages()).isEqualTo(3);
        assertThat(results.getNumber()).isEqualTo(0);
    }

    @Test
    void searchBooks_withSecondPage_returnsCorrectElements() {
        // Arrange
        BookSearchFilters filters = new BookSearchFilters();
        Pageable pageable = PageRequest.of(1, 2); // Page 1, 2 éléments

        // Act
        Page<BookSummaryDto> results = bookService.searchBooks(filters, pageable);

        // Assert
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getNumber()).isEqualTo(1);
        assertThat(results.isFirst()).isFalse();
        assertThat(results.isLast()).isFalse();
    }

    @Test
    void searchBooks_withGenreFilter_returnsOnlyMatchingGenre() {
        // Arrange
        BookSearchFilters filters = new BookSearchFilters();
        filters.setSubject("Science Fiction");
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<BookSummaryDto> results = bookService.searchBooks(filters, pageable);

        // Assert
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getContent())
            .allMatch(dto -> dto.getSubject().getText().contains("Science Fiction"));
    }

    @Test
    void findBookById_withValidId_returnsBook() {
        // Arrange
        Book savedBook = bookRepository.findAll().iterator().next();

        // Act
        BookDetailDto foundBook = bookService.findBookById(savedBook.getId());

        // Assert
        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getId()).isEqualTo(savedBook.getId());
        assertThat(foundBook.getTitle()).isEqualTo(savedBook.getTitle());
    }

    @Test
    void findBookById_withInvalidId_throwsBookNotFoundException() {
        // Arrange
        Long invalidId = 99999L;

        // Act & Assert
        BookNotFoundException exception = assertThrows(
            BookNotFoundException.class,
            () -> bookService.findBookById(invalidId)
        );

        assertThat(exception.getMessage()).contains("Book not found with id: " + invalidId);
    }

    @Test
    void searchBooks_withMultipleFilters_returnsCorrectResults() {
        // Arrange
        BookSearchFilters filters = new BookSearchFilters();
        filters.setAuthor("Tolkien");
        filters.setSubject("Fantasy");
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<BookSummaryDto> results = bookService.searchBooks(filters, pageable);

        // Assert
        assertThat(results.getContent()).hasSize(3);
        assertThat(results.getContent())
            .allMatch(dto -> 
                dto.getAuthorsNames().stream().anyMatch(name -> name.contains("Tolkien")) &&
                dto.getSubject().getText().contains("Fantasy")
            );
    }
}