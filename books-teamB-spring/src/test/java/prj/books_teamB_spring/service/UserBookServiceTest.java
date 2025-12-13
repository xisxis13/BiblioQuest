package prj.books_teamB_spring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import prj.books_teamB_spring.dto.UserBookDTO;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.ReadingStatus;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.model.UserBookId;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.UserBookRepository;
import prj.books_teamB_spring.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserBookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBookRepository userBookRepository;

    @InjectMocks
    private UserBookService userBookService;

    private User testUser;
    private Book testBook;
    private UserBook testUserBook;
    private UserBookDTO testUserBookDTO;
    private UserBookId testUserBookId;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        testUser = new User("alice", "alice@example.com", "password123", Role.USER);
        testUser.setId(1L);

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");

        testUserBookId = new UserBookId(1L, 1L);
        
        testUserBook = new UserBook();
        testUserBook.setId(testUserBookId);
        testUserBook.setStatus(ReadingStatus.TO_READ);

        testUserBookDTO = new UserBookDTO(1L, 1L);
    }

    // ============= Tests pour findByUserId =============

    @Test
    void findByUserId_shouldReturnUserBooks() {
        UserBook userBook1 = new UserBook();
        userBook1.setId(new UserBookId(1L, 1L));
        
        UserBook userBook2 = new UserBook();
        userBook2.setId(new UserBookId(1L, 2L));

        when(userBookRepository.findByUserId(1L)).thenReturn(List.of(userBook1, userBook2));

        List<UserBook> result = userBookService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userBookRepository).findByUserId(1L);
    }

    @Test
    void findByUserId_shouldReturnEmptyList_whenNoBooks() {
        when(userBookRepository.findByUserId(999L)).thenReturn(List.of());

        List<UserBook> result = userBookService.findByUserId(999L);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(userBookRepository).findByUserId(999L);
    }

    // ============= Tests pour getAllBooks =============

    @Test
    void getAllBooks_shouldReturnAllUserBooks() {
        when(userBookRepository.findByUserId(1L)).thenReturn(List.of(testUserBook));

        List<UserBook> result = userBookService.getAllBooks(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userBookRepository).findByUserId(1L);
    }

    @Test
    void getAllBooks_shouldReturnEmptyList_whenNoBooks() {
        when(userBookRepository.findByUserId(1L)).thenReturn(List.of());

        List<UserBook> result = userBookService.getAllBooks(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userBookRepository).findByUserId(1L);
    }

    // ============= Tests pour addBook =============

    @Test
    void addBook_shouldAddBookSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        UserBook result = userBookService.addBook(testUserBookDTO);

        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(bookRepository).findById(1L);
        verify(userBookRepository).save(any(UserBook.class));
    }

    @Test
    void addBook_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserBookDTO dto = new UserBookDTO(999L, 1L);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.addBook(dto)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Utilisateur introuvable"));
        verify(userRepository).findById(999L);
        verify(bookRepository, never()).findById(anyLong());
        verify(userBookRepository, never()).save(any());
    }

    @Test
    void addBook_shouldThrowException_whenBookNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        UserBookDTO dto = new UserBookDTO(1L, 999L);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.addBook(dto)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Livre introuvable"));
        verify(userRepository).findById(1L);
        verify(bookRepository).findById(999L);
        verify(userBookRepository, never()).save(any());
    }

    @Test
    void addBook_shouldCreateUserBookWithDefaultStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        UserBook result = userBookService.addBook(testUserBookDTO);

        assertNotNull(result);
        verify(userBookRepository).save(any(UserBook.class));
    }

    // ============= Tests pour deleteBook =============

    @Test
    void deleteBook_shouldDeleteBookSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.of(testUserBook));

        userBookService.deleteBook(testUserBookDTO);

        verify(userRepository).findById(1L);
        verify(bookRepository).findById(1L);
        verify(userBookRepository).findByUserAndBook(1L, 1L);
        verify(userBookRepository).delete(testUserBook);
    }

    @Test
    void deleteBook_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserBookDTO dto = new UserBookDTO(999L, 1L);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.deleteBook(dto)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Utilisateur introuvable"));
        verify(userRepository).findById(999L);
        verify(userBookRepository, never()).delete(any());
    }

    @Test
    void deleteBook_shouldThrowException_whenBookNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        UserBookDTO dto = new UserBookDTO(1L, 999L);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.deleteBook(dto)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Livre introuvable"));
        verify(bookRepository).findById(999L);
        verify(userBookRepository, never()).delete(any());
    }

    @Test
    void deleteBook_shouldThrowException_whenUserBookNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.deleteBook(testUserBookDTO)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Ce livre n'est pas présent dans la collection"));
        verify(userBookRepository).findByUserAndBook(1L, 1L);
        verify(userBookRepository, never()).delete(any());
    }

    // ============= Tests pour updateReadingStatus =============

    @Test
    void updateReadingStatus_shouldUpdateStatusSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.of(testUserBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        UserBook result = userBookService.updateReadingStatus(testUserBookDTO, ReadingStatus.IN_PROGRESS);

        assertNotNull(result);
        assertEquals(ReadingStatus.IN_PROGRESS, testUserBook.getStatus());
        verify(userRepository).findById(1L);
        verify(bookRepository).findById(1L);
        verify(userBookRepository).findByUserAndBook(1L, 1L);
        verify(userBookRepository).save(testUserBook);
    }

    @Test
    void updateReadingStatus_shouldUpdateToCompleted() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.of(testUserBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        UserBook result = userBookService.updateReadingStatus(testUserBookDTO, ReadingStatus.COMPLETED);

        assertNotNull(result);
        assertEquals(ReadingStatus.COMPLETED, testUserBook.getStatus());
        verify(userBookRepository).save(testUserBook);
    }

    @Test
    void updateReadingStatus_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserBookDTO dto = new UserBookDTO(999L, 1L);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.updateReadingStatus(dto, ReadingStatus.IN_PROGRESS)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Utilisateur introuvable"));
        verify(userRepository).findById(999L);
        verify(userBookRepository, never()).save(any());
    }

    @Test
    void updateReadingStatus_shouldThrowException_whenBookNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        UserBookDTO dto = new UserBookDTO(1L, 999L);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.updateReadingStatus(dto, ReadingStatus.IN_PROGRESS)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Livre introuvable"));
        verify(bookRepository).findById(999L);
        verify(userBookRepository, never()).save(any());
    }

    @Test
    void updateReadingStatus_shouldThrowException_whenUserBookNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userBookService.updateReadingStatus(testUserBookDTO, ReadingStatus.IN_PROGRESS)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Ce livre n'est pas présent dans la collection"));
        verify(userBookRepository).findByUserAndBook(1L, 1L);
        verify(userBookRepository, never()).save(any());
    }

    @Test
    void updateReadingStatus_shouldChangeFromInProgressToCompleted() {
        testUserBook.setStatus(ReadingStatus.IN_PROGRESS);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.of(testUserBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        UserBook result = userBookService.updateReadingStatus(testUserBookDTO, ReadingStatus.COMPLETED);

        assertNotNull(result);
        assertEquals(ReadingStatus.COMPLETED, testUserBook.getStatus());
        verify(userBookRepository).save(testUserBook);
    }
}
