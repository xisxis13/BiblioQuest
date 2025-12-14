package prj.books_teamB_spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import prj.books_teamB_spring.dto.UserBookDTO;
import prj.books_teamB_spring.model.ReadingStatus;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.model.UserBookId;
import prj.books_teamB_spring.service.UserBookService;

@ExtendWith(MockitoExtension.class)
class UserBookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserBookService userBookService;

    @InjectMocks
    private UserBookController userBookController;

    private UserBook userBook1;
    private UserBook userBook2;
    private UserBookDTO userBookDTO;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userBookController)
                .build();

        UserBookId userBookId1 = new UserBookId(1L, 100L);
        UserBookId userBookId2 = new UserBookId(1L, 101L);

        userBook1 = new UserBook();
        userBook1.setId(userBookId1);
        userBook1.setStatus(ReadingStatus.IN_PROGRESS);
        userBook1.setProgress(50);
        userBook1.setStartDate(LocalDate.now().minusDays(10));

        userBook2 = new UserBook();
        userBook2.setId(userBookId2);
        userBook2.setStatus(ReadingStatus.TO_READ);
        userBook2.setProgress(0);

        userBookDTO = new UserBookDTO(1L, 100L);
    }

    @Test
    void getAllBooks_shouldReturnListOfBooks() throws Exception {
        when(userBookService.getAllBooks(1L)).thenReturn(List.of(userBook1, userBook2));

        mockMvc.perform(get("/api/user/books")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$[0].progress", is(50)))
                .andExpect(jsonPath("$[1].status", is("TO_READ")))
                .andExpect(jsonPath("$[1].progress", is(0)));

        verify(userBookService).getAllBooks(1L);
    }

    @Test
    void getAllBooks_shouldReturnEmptyList() throws Exception {
        when(userBookService.getAllBooks(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/user/books")
                .param("userId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userBookService).getAllBooks(999L);
    }

    @Test
    void addBook_shouldReturnCreated() throws Exception {
        when(userBookService.addBook(any(UserBookDTO.class))).thenReturn(userBook1);

        String jsonRequest = "{\"userId\":1,\"bookId\":100}";

        mockMvc.perform(post("/api/user/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.progress", is(50)));

        verify(userBookService).addBook(any(UserBookDTO.class));
    }

    @Test
    void deleteBook_shouldReturnNoContent() throws Exception {
        doNothing().when(userBookService).deleteBook(any(UserBookDTO.class));

        String jsonRequest = "{\"userId\":1,\"bookId\":100}";

        mockMvc.perform(delete("/api/user/books/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isNoContent());

        verify(userBookService).deleteBook(any(UserBookDTO.class));
    }

    @Test
    void updateReadingStatus_shouldReturnUpdatedBook() throws Exception {
        UserBook updatedBook = new UserBook();
        updatedBook.setId(new UserBookId(1L, 100L));
        updatedBook.setStatus(ReadingStatus.COMPLETED);
        updatedBook.setProgress(100);
        updatedBook.setEndDate(LocalDate.now());

        when(userBookService.updateReadingStatus(any(UserBookDTO.class), eq(ReadingStatus.COMPLETED)))
                .thenReturn(updatedBook);

        String jsonRequest = "{\"userId\":1,\"bookId\":100}";

        mockMvc.perform(post("/api/user/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .param("readingStatus", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.progress", is(100)));

        verify(userBookService).updateReadingStatus(any(UserBookDTO.class), eq(ReadingStatus.COMPLETED));
    }

    @Test
    void updateReadingStatus_shouldChangeFromToReadToInProgress() throws Exception {
        UserBook updatedBook = new UserBook();
        updatedBook.setId(new UserBookId(1L, 101L));
        updatedBook.setStatus(ReadingStatus.IN_PROGRESS);
        updatedBook.setProgress(0);
        updatedBook.setStartDate(LocalDate.now());

        when(userBookService.updateReadingStatus(any(UserBookDTO.class), eq(ReadingStatus.IN_PROGRESS)))
                .thenReturn(updatedBook);

        String jsonRequest = "{\"userId\":1,\"bookId\":101}";

        mockMvc.perform(post("/api/user/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .param("readingStatus", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));

        verify(userBookService).updateReadingStatus(any(UserBookDTO.class), eq(ReadingStatus.IN_PROGRESS));
    }
}