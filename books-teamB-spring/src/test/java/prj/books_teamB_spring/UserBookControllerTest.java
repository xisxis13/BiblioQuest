package prj.books_teamB_spring;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import prj.books_teamB_spring.controller.UserBookController;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Subject;
import prj.books_teamB_spring.service.UserBookService;

@WebMvcTest(controllers = UserBookController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class UserBookControllerTest {

        @Autowired
        MockMvc mvc;

        @Autowired
        ObjectMapper mapper;

        @MockBean
        UserBookService userBookService;

        @Test
        void addBookToCollection_createsBook() throws Exception {
                Book in = new Book();
                in.setTitle("Posted Book");
                in.setDesc_txt("A new book");

                Book saved = new Book();
                saved.setId(42L);
                saved.setTitle("Posted Book");
                saved.setDesc_txt("A new book");

                when(userBookService.addBookForUser(eq("alice"), any(Book.class))).thenReturn(saved);

                mvc.perform(post("/api/users/books")
                                .header("X-Username", "alice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(in)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(42))
                                .andExpect(jsonPath("$.title").value("Posted Book"))
                                .andExpect(jsonPath("$.desc_txt").value("A new book"));

                verify(userBookService).addBookForUser(eq("alice"), any(Book.class));
        }

        @Test
        void listMyBooks_returnsUserBooks() throws Exception {
                Subject subject = new Subject();
                subject.setId(1L);
                subject.setText("Fantasy");

                Book book1 = new Book();
                book1.setId(9L);
                book1.setTitle("My Book 1");
                book1.setSubject(subject);

                Book book2 = new Book();
                book2.setId(10L);
                book2.setTitle("My Book 2");
                book2.setSubject(subject);

                when(userBookService.listBooksForUser("alice")).thenReturn(List.of(book1, book2));

                mvc.perform(get("/api/users/books")
                                .header("X-Username", "alice"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].id").value(9))
                                .andExpect(jsonPath("$[0].title").value("My Book 1"))
                                .andExpect(jsonPath("$[1].id").value(10))
                                .andExpect(jsonPath("$[1].title").value("My Book 2"));
        }

        @Test
        void listMyBooks_withEmptyCollection_returnsEmptyList() throws Exception {
                when(userBookService.listBooksForUser("alice")).thenReturn(Collections.emptyList());

                mvc.perform(get("/api/users/books")
                                .header("X-Username", "alice"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void getMyBook_returnsBookWhenOwned() throws Exception {
                Book book = new Book();
                book.setId(5L);
                book.setTitle("Owned Book");
                book.setDesc_txt("Description of owned book");

                when(userBookService.getBookForUser("alice", 5L)).thenReturn(book);

                mvc.perform(get("/api/users/books/5")
                                .header("X-Username", "alice"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(5))
                                .andExpect(jsonPath("$.title").value("Owned Book"))
                                .andExpect(jsonPath("$.desc_txt").value("Description of owned book"));
        }

        @Test
        void getMyBook_withNonOwnedBook_returns403() throws Exception {
                when(userBookService.getBookForUser("alice", 999L))
                                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this book"));

                mvc.perform(get("/api/users/books/999")
                                .header("X-Username", "alice"))
                                .andExpect(status().isForbidden());
        }

        @Test
        void getMyBook_withNonExistentBook_returns404() throws Exception {
                when(userBookService.getBookForUser("alice", 888L))
                                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

                mvc.perform(get("/api/users/books/888")
                                .header("X-Username", "alice"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void removeBookFromCollection_deletesBook() throws Exception {
                doNothing().when(userBookService).deleteBookForUser("alice", 3L);

                mvc.perform(delete("/api/users/books/3")
                                .header("X-Username", "alice"))
                                .andExpect(status().isNoContent());

                verify(userBookService).deleteBookForUser("alice", 3L);
        }

        @Test
        void removeBookFromCollection_withNonOwnedBook_returns403() throws Exception {
                doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this book"))
                                .when(userBookService).deleteBookForUser("alice", 777L);

                mvc.perform(delete("/api/users/books/777")
                                .header("X-Username", "alice"))
                                .andExpect(status().isForbidden());
        }

        @Test
        void removeBookFromCollection_withNonExistentBook_returns404() throws Exception {
                doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"))
                                .when(userBookService).deleteBookForUser("alice", 666L);

                mvc.perform(delete("/api/users/books/666")
                                .header("X-Username", "alice"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void addBookToCollection_withInvalidUser_returns401() throws Exception {
                Book book = new Book();
                book.setTitle("Test");

                when(userBookService.addBookForUser(eq("unknown"), any(Book.class)))
                                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));

                mvc.perform(post("/api/users/books")
                                .header("X-Username", "unknown")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(book)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void listMyBooks_withInvalidUser_returns401() throws Exception {
                when(userBookService.listBooksForUser("unknown"))
                                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));

                mvc.perform(get("/api/users/books")
                                .header("X-Username", "unknown"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void addBookToCollection_withMissingUsername_returns400() throws Exception {
                Book book = new Book();
                book.setTitle("Test");

                mvc.perform(post("/api/users/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(book)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void addBookToCollection_withCompleteBook_savesAllFields() throws Exception {
                Subject subject = new Subject();
                subject.setId(1L);
                subject.setText("Science Fiction");

                Book in = new Book();
                in.setTitle("Complete Book");
                in.setDesc_txt("A complete description");
                in.setCover("https://example.com/cover.jpg");
                in.setSubject(subject);

                Book saved = new Book();
                saved.setId(100L);
                saved.setTitle("Complete Book");
                saved.setDesc_txt("A complete description");
                saved.setCover("https://example.com/cover.jpg");
                saved.setSubject(subject);

                when(userBookService.addBookForUser(eq("alice"), any(Book.class))).thenReturn(saved);

                mvc.perform(post("/api/users/books")
                                .header("X-Username", "alice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(in)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(100))
                                .andExpect(jsonPath("$.title").value("Complete Book"))
                                .andExpect(jsonPath("$.desc_txt").value("A complete description"))
                                .andExpect(jsonPath("$.cover").value("https://example.com/cover.jpg"))
                                .andExpect(jsonPath("$.subject.text").value("Science Fiction"));
        }
}