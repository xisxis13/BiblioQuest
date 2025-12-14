package prj.books_teamB_spring.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import prj.books_teamB_spring.dto.BookDTO;
import prj.books_teamB_spring.exception.GlobalExceptionHandler;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.service.BookService;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Book book1;
    private BookDTO bookDTO1;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Test Book 1");
        book1.setTotal_pages(100);

        bookDTO1 = new BookDTO(book1);
    }

    @Test
    void getAll_shouldReturnListOfBookDTOs() throws Exception {
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Test Book 2");
        book2.setTotal_pages(200);

        when(bookService.findAll()).thenReturn(List.of(book1, book2));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Test Book 1")))
                .andExpect(jsonPath("$[1].title", is("Test Book 2")));
    }

    @Test
    void getBook_shouldReturnBookDTO_whenFound() throws Exception {
        when(bookService.findById(1L)).thenReturn(book1);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Book 1")));
    }

    @Test
    void getBook_shouldReturnNotFound_whenBookDoesNotExist() throws Exception {
        when(bookService.findById(anyLong())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_shouldReturnNoContent() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void addBook_shouldReturnCreatedBookDTO() throws Exception {
        when(bookService.addBook(any(BookDTO.class))).thenReturn(book1);

        mockMvc.perform(post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDTO1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Test Book 1")));
    }

    @Test
    void updateBook_shouldReturnUpdatedBookDTO() throws Exception {
        // The service update method in the controller takes a BookDTO and returns a BookDTO
        when(bookService.update(any(BookDTO.class))).thenReturn(bookDTO1);

        mockMvc.perform(post("/api/books/update/1") // The endpoint in the controller is /update/{id} but the id is not used
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDTO1)))
                .andExpect(status().isCreated()) // Note: Your controller returns 201 CREATED on update
                .andExpect(jsonPath("$.title", is("Test Book 1")));
    }
}