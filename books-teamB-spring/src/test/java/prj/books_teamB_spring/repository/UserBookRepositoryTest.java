package prj.books_teamB_spring.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import prj.books_teamB_spring.model.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserBookRepositoryTest {

    @Autowired
    private UserBookRepository userBookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private User user1;
    private User user2;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        userBookRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        // Créer des utilisateurs
        user1 = userRepository.save(new User("user1", "user1@test.com", "pass", Role.USER));
        user2 = userRepository.save(new User("user2", "user2@test.com", "pass", Role.USER));

        // Créer des livres
        book1 = new Book();
        book1.setTitle("Book 1");
        book1 = bookRepository.save(book1);

        book2 = new Book();
        book2.setTitle("Book 2");
        book2 = bookRepository.save(book2);
    }

    @Test
    void save_shouldPersistUserBook() {
        UserBook userBook = new UserBook(user1, book1);

        UserBook saved = userBookRepository.save(userBook);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId().getUserId()).isEqualTo(user1.getId());
        assertThat(saved.getId().getBookId()).isEqualTo(book1.getId());
        assertThat(saved.getStatus()).isEqualTo(ReadingStatus.TO_READ);
    }

    @Test
    void findById_shouldReturnUserBook_whenExists() {
        UserBook userBook = new UserBook(user1, book1, ReadingStatus.IN_PROGRESS);
        userBook.setProgress(50);
        userBookRepository.save(userBook);

        Optional<UserBook> found = userBookRepository.findById(new UserBookId(user1.getId(), book1.getId()));

        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(ReadingStatus.IN_PROGRESS);
        assertThat(found.get().getProgress()).isEqualTo(50);
    }

    @Test
    void findById_shouldReturnEmpty_whenDoesNotExist() {
        Optional<UserBook> found = userBookRepository.findById(new UserBookId(999L, 999L));

        assertThat(found).isEmpty();
    }

    @Test
    void findByUserId_shouldReturnAllBooksForUser() {
        // user1 a 2 livres
        userBookRepository.save(new UserBook(user1, book1));
        userBookRepository.save(new UserBook(user1, book2));
        // user2 a 1 livre
        userBookRepository.save(new UserBook(user2, book1));

        List<UserBook> user1Books = userBookRepository.findByUserId(user1.getId());

        assertThat(user1Books).hasSize(2);
        assertThat(user1Books).allMatch(ub -> ub.getId().getUserId().equals(user1.getId()));
    }

    @Test
    void findByUserId_shouldReturnEmpty_whenUserHasNoBooks() {
        List<UserBook> userBooks = userBookRepository.findByUserId(999L);

        assertThat(userBooks).isEmpty();
    }

    @Test
    void findByUserAndBook_shouldReturnUserBook_whenExists() {
        UserBook userBook = new UserBook(user1, book1, ReadingStatus.COMPLETED);
        userBook.setRating(5);
        userBookRepository.save(userBook);

        Optional<UserBook> found = userBookRepository.findByUserAndBook(user1.getId(), book1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(ReadingStatus.COMPLETED);
        assertThat(found.get().getRating()).isEqualTo(5);
    }

    @Test
    void findByUserAndBook_shouldReturnEmpty_whenDoesNotExist() {
        Optional<UserBook> found = userBookRepository.findByUserAndBook(999L, 999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findByBookId_shouldReturnAllUsersForBook() {
        // book1 est lu par 2 utilisateurs
        userBookRepository.save(new UserBook(user1, book1));
        userBookRepository.save(new UserBook(user2, book1));
        // book2 est lu par 1 utilisateur
        userBookRepository.save(new UserBook(user1, book2));

        List<UserBook> book1Readers = userBookRepository.findByBookId(book1.getId());

        assertThat(book1Readers).hasSize(2);
        assertThat(book1Readers).allMatch(ub -> ub.getId().getBookId().equals(book1.getId()));
    }

    @Test
    void findByBookId_shouldReturnEmpty_whenNoUsersHaveBook() {
        List<UserBook> userBooks = userBookRepository.findByBookId(999L);

        assertThat(userBooks).isEmpty();
    }

    @Test
    void deleteById_shouldRemoveUserBook() {
        UserBook userBook = new UserBook(user1, book1);
        userBookRepository.save(userBook);

        userBookRepository.deleteById(new UserBookId(user1.getId(), book1.getId()));

        assertThat(userBookRepository.findById(new UserBookId(user1.getId(), book1.getId()))).isEmpty();
    }

    @Test
    void save_withReviewAndRating_shouldPersist() {
        UserBook userBook = new UserBook(user1.getId(), book1.getId(), "Great book!", 5);
        userBookRepository.save(userBook);

        Optional<UserBook> found = userBookRepository.findByUserAndBook(user1.getId(), book1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getReview()).isEqualTo("Great book!");
        assertThat(found.get().getRating()).isEqualTo(5);
        assertThat(found.get().getStatus()).isEqualTo(ReadingStatus.TO_READ);
    }

    @Test
    void update_shouldModifyExistingUserBook() {
        UserBook userBook = new UserBook(user1, book1);
        userBook = userBookRepository.save(userBook);

        userBook.setStatus(ReadingStatus.IN_PROGRESS);
        userBook.setProgress(75);
        userBookRepository.save(userBook);

        Optional<UserBook> updated = userBookRepository.findByUserAndBook(user1.getId(), book1.getId());

        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(ReadingStatus.IN_PROGRESS);
        assertThat(updated.get().getProgress()).isEqualTo(75);
    }
}
