package prj.books_teamB_spring;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import prj.books_teamB_spring.model.Author;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Subject;
import prj.books_teamB_spring.repository.AuthorRepository;
import prj.books_teamB_spring.repository.BookRepository;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.defer-datasource-initialization=true")
// On nettoie tout entre chaque test pour éviter les effets de bord
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void fixIds() {
        
        try {
            jdbcTemplate.execute("ALTER SEQUENCE author_seq RESTART WITH 100");
            jdbcTemplate.execute("ALTER SEQUENCE book_seq RESTART WITH 1000");
            jdbcTemplate.execute("ALTER SEQUENCE subject_seq RESTART WITH 100");
        } catch (Exception e) {
        }
        try {
            jdbcTemplate.execute("ALTER TABLE author ALTER COLUMN id RESTART WITH 100");
            jdbcTemplate.execute("ALTER TABLE book ALTER COLUMN id RESTART WITH 1000");
            jdbcTemplate.execute("ALTER TABLE subject ALTER COLUMN id RESTART WITH 100");
        } catch (Exception e) {
        }
    }

    @Test
    void testSaveBookWithRelations() {
        Author author = new Author();
        author.setFirstName("Victor");
        author.setLastName("Hugo");
        Author savedAuthor = authorRepository.save(author);

        Subject subject = new Subject();
        subject.setText("Drame");
        Subject savedSubject = entityManager.persistFlushFind(subject); 

        Book book = new Book();
        book.setTitle("Les Misérables");
        book.setDesc_txt("Une histoire poignante..."); 
        book.setCreated_at(LocalDateTime.now());
        book.setUpdated_at(LocalDateTime.now());
        
        book.setSubject(savedSubject);

        Set<Author> authors = new HashSet<>();
        authors.add(savedAuthor);
        book.setAuthors(authors);

        Book savedBook = bookRepository.save(book);
        assertThat(savedBook.getId()).isGreaterThan(0);
        
        assertThat(savedBook.getSubject()).isNotNull();
        assertThat(savedBook.getSubject().getText()).isEqualTo("Drame");

        assertThat(savedBook.getAuthors()).hasSize(1);
        assertThat(savedBook.getAuthors().iterator().next().getLastName()).isEqualTo("Hugo");
    }

    
}
