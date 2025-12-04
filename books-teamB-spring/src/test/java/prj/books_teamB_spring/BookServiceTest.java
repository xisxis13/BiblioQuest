package prj.books_teamB_spring;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.UserRepository;
import prj.books_teamB_spring.service.BookService;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    BookService bookService;

    private User alice;

    @BeforeEach
    void setUp() {
        alice = new User(
            "alice",
            "alice@example.com",
            "pwd",
            Role.USER
        );
        alice.setUserBooks(new HashSet<>());
    }

    @Test
    void addBookForUser_attachesBookToUser() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        Book in = new Book();
        in.setTitle("My Book");

        Book saved = new Book();
        saved.setId(10L);
        saved.setTitle(in.getTitle());

        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        Book result = bookService.addBookForUser("alice", in);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertTrue(alice.getMyBooks().contains(result));
        verify(userRepository).save(alice);
    }

    @Test
    void getBookForUser_forbiddenWhenNotOwner() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        Book b = new Book();
        b.setId(5L);
        when(bookRepository.findById(5L)).thenReturn(Optional.of(b));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                bookService.getBookForUser("alice", 5L)
        );
        assertEquals(403, ex.getStatusCode().value());
    }

    @Test
    void deleteBookForUser_removesFromUser() {
        Book owned = new Book(); owned.setId(11L);
        alice.addBook(owned);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(bookRepository.findById(11L)).thenReturn(Optional.of(owned));

        bookService.deleteBookForUser("alice", 11L);

        assertFalse(alice.getMyBooks().contains(owned));
        verify(userRepository).save(alice);
    }
}
