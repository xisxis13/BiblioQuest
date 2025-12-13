package prj.books_teamB_spring.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import prj.books_teamB_spring.model.Author;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    private Author testAuthor;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
        testAuthor = new Author();
        testAuthor.setFirstName("Victor");
        testAuthor.setLastName("Hugo");
        testAuthor = authorRepository.save(testAuthor);
    }

    @Test
    void save_shouldPersistAuthor() {
        Author newAuthor = new Author();
        newAuthor.setFirstName("Albert");
        newAuthor.setLastName("Camus");

        Author saved = authorRepository.save(newAuthor);

        assertThat(saved.getId()).isNotNull();
        assertThat(authorRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void findById_shouldReturnAuthor_whenAuthorExists() {
        Optional<Author> found = authorRepository.findById(testAuthor.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Victor");
        assertThat(found.get().getLastName()).isEqualTo("Hugo");
    }

    @Test
    void findById_shouldReturnEmpty_whenAuthorDoesNotExist() {
        Optional<Author> found = authorRepository.findById(99999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllAuthors() {
        Author author2 = new Author();
        author2.setFirstName("Émile");
        author2.setLastName("Zola");
        authorRepository.save(author2);

        Iterable<Author> authors = authorRepository.findAll();

        assertThat(authors).hasSize(2);
    }

    @Test
    void deleteById_shouldRemoveAuthor() {
        authorRepository.deleteById(testAuthor.getId());

        assertThat(authorRepository.findById(testAuthor.getId())).isEmpty();
    }

    @Test
    void count_shouldReturnCorrectNumber() {
        assertThat(authorRepository.count()).isEqualTo(1);

        Author anotherAuthor = new Author();
        anotherAuthor.setFirstName("Marcel");
        anotherAuthor.setLastName("Proust");
        authorRepository.save(anotherAuthor);

        assertThat(authorRepository.count()).isEqualTo(2);
    }
}
