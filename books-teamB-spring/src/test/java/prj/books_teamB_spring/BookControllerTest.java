package prj.books_teamB_spring;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.service.BookService;

@WebMvcTest(controllers = prj.books_teamB_spring.controller.BookController.class)
class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookService bookService;

    @Test
    void postCreatesBook() throws Exception {
        Book in = new Book();
        in.setTitle("Posted");

        Book saved = new Book();
        saved.setId(42L);
        saved.setTitle("Posted");

        when(bookService.addBookForUser("alice", in)).thenReturn(saved);

        mvc.perform(post("/api/books")
                .header("X-Username", "alice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void listReturnsBooks() throws Exception {
        Book b = new Book(); b.setId(9L); b.setTitle("T");
        when(bookService.listBooksForUser("alice")).thenReturn(Collections.singletonList(b));

        mvc.perform(get("/api/books").header("X-Username", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(9));
    }
}
