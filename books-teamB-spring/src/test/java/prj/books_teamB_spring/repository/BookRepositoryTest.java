package prj.books_teamB_spring.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import prj.books_teamB_spring.model.Book;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book testBook;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setIsbn("978-3-16-148410-0");
        testBook.setTotal_pages(300);
        testBook = bookRepository.save(testBook);
    }

    @Test
    void save_shouldPersistBook() {
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setIsbn("978-0-13-468599-1");

        Book saved = bookRepository.save(newBook);

        assertThat(saved.getId()).isNotNull();
        assertThat(bookRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void findById_shouldReturnBook_whenBookExists() {
        Optional<Book> found = bookRepository.findById(testBook.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Book");
    }

    @Test
    void findById_shouldReturnEmpty_whenBookDoesNotExist() {
        Optional<Book> found = bookRepository.findById(99999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_withPagination_shouldReturnPagedResults() {
        // Ajouter plus de livres
        for (int i = 0; i < 5; i++) {
            Book book = new Book();
            book.setTitle("Book " + i);
            bookRepository.save(book);
        }

        Page<Book> page = bookRepository.findAll(PageRequest.of(0, 3));

        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(6); // testBook + 5 nouveaux
    }

    @Test
    void deleteById_shouldRemoveBook() {
        bookRepository.deleteById(testBook.getId());

        assertThat(bookRepository.findById(testBook.getId())).isEmpty();
    }

    @Test
    void count_shouldReturnCorrectNumber() {
        assertThat(bookRepository.count()).isEqualTo(1);

        Book anotherBook = new Book();
        anotherBook.setTitle("Another Book");
        bookRepository.save(anotherBook);

        assertThat(bookRepository.count()).isEqualTo(2);
    }
}
