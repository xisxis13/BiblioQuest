package prj.books_teamB_spring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prj.books_teamB_spring.dto.AuthorDto;
import prj.books_teamB_spring.dto.BookDTO;
import prj.books_teamB_spring.exception.BookNotFoundException;
import prj.books_teamB_spring.model.Author;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Subject;
import prj.books_teamB_spring.repository.AuthorRepository;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.SubjectRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private Author testAuthor;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setDesc_txt("Test Description");
        testBook.setCover("test-cover.jpg");
        testBook.setIsbn("978-3-16-148410-0");
        testBook.setTotal_pages(250);
        testBook.setAuthors(new HashSet<>());
        testBook.setCreated_at(LocalDateTime.now());
        testBook.setUpdated_at(LocalDateTime.now());

        testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setFirstName("John");
        testAuthor.setLastName("Doe");
    }

    @Test
    void findById_shouldReturnBook_whenBookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        Book result = bookService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowBookNotFoundException_whenBookDoesNotExist() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(BookNotFoundException.class, () -> bookService.findById(999L));
        assertNotNull(exception);
        verify(bookRepository).findById(999L);
    }

    @Test
    void findAll_shouldReturnAllBooks() {
        List<Book> books = List.of(testBook, new Book());
        when(bookRepository.findAll()).thenReturn(books);

        Iterable<Book> result = bookService.findAll();

        assertNotNull(result);
        List<Book> bookList = new ArrayList<>();
        result.forEach(bookList::add);
        assertEquals(2, bookList.size());
        verify(bookRepository).findAll();
    }

    @Test
    void deleteBook_shouldDeleteBook_whenBookExists() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_shouldThrowBookNotFoundException_whenBookDoesNotExist() {
        when(bookRepository.existsById(999L)).thenReturn(false);

        Exception exception = assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(999L));
        assertNotNull(exception);
        verify(bookRepository).existsById(999L);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteAuthor_shouldRemoveAuthor_whenBothExist() {
        testBook.getAuthors().add(testAuthor);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.deleteAuthor(1L, 1L);

        assertNotNull(result);
        verify(bookRepository).findById(1L);
        verify(authorRepository).findById(1L);
        verify(bookRepository).save(testBook);
    }

    @Test
    void deleteAuthor_shouldThrowException_whenAuthorNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> bookService.deleteAuthor(1L, 999L));
        assertNotNull(exception);
        verify(authorRepository).findById(999L);
    }

    @Test
    void addAuthor_shouldAddNewAuthor_whenBookExists() {
        AuthorDto authorDto = new AuthorDto(null, "Jane", "Smith");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(authorRepository.save(any(Author.class))).thenReturn(testAuthor);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.addAuthor(1L, authorDto);

        assertNotNull(result);
        verify(bookRepository).findById(1L);
        verify(authorRepository).save(any(Author.class));
        verify(bookRepository).save(testBook);
    }

    @Test
    void addAuthor_shouldAddExistingAuthor_whenAuthorIdProvided() {
        AuthorDto authorDto = new AuthorDto(1L, "John", "Doe");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(testAuthor);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.addAuthor(1L, authorDto);

        assertNotNull(result);
        verify(authorRepository).findById(1L);
        verify(bookRepository).save(testBook);
    }

    @Test
    void addAuthor_shouldThrowException_whenFirstNameAndLastNameEmpty() {
        AuthorDto authorDto = new AuthorDto(null, "", "");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> bookService.addAuthor(1L, authorDto));
        assertNotNull(exception);
        verify(bookRepository).findById(1L);
        verify(authorRepository, never()).save(any());
    }

    @Test
    void addAuthor_shouldThrowBookNotFoundException_whenBookDoesNotExist() {
        AuthorDto authorDto = new AuthorDto(null, "Jane", "Smith");

        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(BookNotFoundException.class, () -> bookService.addAuthor(999L, authorDto));
        assertNotNull(exception);
        verify(bookRepository).findById(999L);
    }

    @Test
    void addAuthor_shouldAcceptOnlyFirstName() {
        AuthorDto authorDto = new AuthorDto(null, "Madonna", null);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(authorRepository.save(any(Author.class))).thenReturn(testAuthor);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.addAuthor(1L, authorDto);

        assertNotNull(result);
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void addAuthor_shouldAcceptOnlyLastName() {
        AuthorDto authorDto = new AuthorDto(null, null, "Voltaire");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(authorRepository.save(any(Author.class))).thenReturn(testAuthor);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.addAuthor(1L, authorDto);

        assertNotNull(result);
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void addBook_shouldCreateBookWithAuthors() {
        Book book = new Book();
        book.setTitle("New Book");
        book.setDesc_txt("Description");
        book.setCover("cover.jpg");
        book.setIsbn("978-1234567890");
        book.setTotal_pages(300);

        BookDTO bookDTO = new BookDTO(book);
        
        AuthorDto authorDto = new AuthorDto(null, "Alice", "Wonder");
        bookDTO.setAuthors(List.of(authorDto));

        when(authorRepository.save(any(Author.class))).thenReturn(testAuthor);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.addBook(bookDTO);

        assertNotNull(result);
        verify(authorRepository).save(any(Author.class));
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void addBook_shouldSkipEmptyAuthors() {
        Book book = new Book();
        book.setTitle("New Book");

        BookDTO bookDTO = new BookDTO(book);

        AuthorDto emptyAuthor = new AuthorDto(null, "", "");
        bookDTO.setAuthors(List.of(emptyAuthor));

        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.addBook(bookDTO);

        assertNotNull(result);
        verify(bookRepository).save(any(Book.class));
        verify(authorRepository, never()).save(any());
    }

    @Test
    void addBook_shouldCreateBookWithoutAuthors() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("New Book");
        bookDTO.setDesc_txt("Description");

        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.addBook(bookDTO);

        assertNotNull(result);
        verify(bookRepository).save(any(Book.class));
        verify(authorRepository, never()).save(any());
    }

    @Test
    void getSubjects_shouldReturnAllSubjects() {
        Subject subject1 = new Subject();
        subject1.setId(1);
        subject1.setLabel("Fiction");

        Subject subject2 = new Subject();
        subject2.setId(2);
        subject2.setLabel("Science");

        when(subjectRepository.findAll()).thenReturn(List.of(subject1, subject2));

        List<Subject> result = bookService.getSubjects();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(subjectRepository).findAll();
    }

    @Test
    void update_shouldUpdateBook_whenBookExists() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(1L);
        bookDTO.setTitle("Updated Title");
        bookDTO.setDesc_txt("Updated Description");
        bookDTO.setCover("updated-cover.jpg");
        bookDTO.setIsbn("978-9999999999");
        bookDTO.setTotalPages(400);
        bookDTO.setSerie_id(5);
        bookDTO.setSerie_pos(2);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        BookDTO result = bookService.update(bookDTO);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(testBook);
    }

    @Test
    void update_shouldThrowException_whenBookNotFound() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(999L);

        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(BookNotFoundException.class, () -> bookService.update(bookDTO));
        assertNotNull(exception);
        verify(bookRepository).findById(999L);
        verify(bookRepository, never()).save(any());
    }
}
