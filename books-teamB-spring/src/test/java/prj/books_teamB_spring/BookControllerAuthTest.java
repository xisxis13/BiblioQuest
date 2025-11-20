package prj.books_teamB_spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import prj.books_teamB_spring.model.Author;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Subject;
import prj.books_teamB_spring.repository.AuthorRepository;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.SubjectRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookControllerAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @BeforeEach
    void setUp() {
        // Nettoyer la base de données
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        subjectRepository.deleteAll();

        Author tolkien = new Author();
        tolkien.setFirstName("J.R.R.");
        tolkien.setLastName("Tolkien");
        tolkien = authorRepository.save(tolkien);

        Subject fantasy = new Subject();
        fantasy.setText("Fantasy");
        fantasy = subjectRepository.save(fantasy);

        Book book = new Book();
        book.setTitle("The Hobbit");
        book.setDesc_txt("A fantasy adventure");
        book.setCover("https://example.com/hobbit.jpg");
        book.setSubject(fantasy);
        book.setCreated_at(LocalDateTime.of(1937, 1, 1, 0, 0));
        book.setUpdated_at(LocalDateTime.now());

        Set<Author> authors = new HashSet<>();
        authors.add(tolkien);
        book.setAuthors(authors);

        bookRepository.save(book);
    }

    @Test
    @WithMockUser
    void searchBooks_withAuth_returns200() throws Exception {
        mockMvc.perform(get("/api/books/search?author=Tolkien"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser
    void getBookById_withAuth_returns200() throws Exception {
        Book book = bookRepository.findAll().iterator().next();

        mockMvc.perform(get("/api/books/" + book.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("The Hobbit"));
    }

    @Test
    @WithMockUser
    void getBookById_withInvalidId_returns404() throws Exception {
        mockMvc.perform(get("/api/books/99999"))
            .andExpect(status().isNotFound());
    }
}