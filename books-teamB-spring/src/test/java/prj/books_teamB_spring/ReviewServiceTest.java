package prj.books_teamB_spring;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import prj.books_teamB_spring.dto.ReviewDto;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.ReadingStatus;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.Subject;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.SubjectRepository;
import prj.books_teamB_spring.repository.UserBookRepository;
import prj.books_teamB_spring.repository.UserRepository;
import prj.books_teamB_spring.service.ReviewService;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})

public class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserBookRepository userBookRepository;

    private User testUser;
    private User anotherUser;
    private Book testBook;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
        userBookRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
        subjectRepository.deleteAll();

        testSubject = new Subject();
        testSubject.setText("Fiction");
        testSubject = subjectRepository.save(testSubject);

        testUser = new User("alice", "alice@example.com", "password123", Role.USER);
        testUser = userRepository.save(testUser);

        anotherUser = new User("bob", "bob@example.com", "password456", Role.USER);
        anotherUser = userRepository.save(anotherUser);

        testBook = createAndSaveBook("Test Book");
    }

    // ============= Tests pour getReviewsForBook =============

    @Test
    void getReviewsForBook_withNoReviews_returnsEmptyList() {
        List<ReviewDto> reviews = reviewService.getReviewsForBook(testBook.getId(), "alice");

        assertThat(reviews).isEmpty();
    }

    @Test
    void getReviewsForBook_withOneReview_returnsReview() {
        // Créer un UserBook avec rating
        UserBook userBook = new UserBook(testUser, testBook, ReadingStatus.COMPLETED);
        userBook.setRating(4);
        userBook.setPersonalNote("Great book!");
        userBookRepository.save(userBook);

        List<ReviewDto> reviews = reviewService.getReviewsForBook(testBook.getId(), "alice");

        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getRating()).isEqualTo(4);
        assertThat(reviews.get(0).getComment()).isEqualTo("Great book!");
        assertThat(reviews.get(0).getUsername()).isEqualTo("alice");
        assertThat(reviews.get(0).getIsOwnReview()).isTrue();
    }

    @Test
    void getReviewsForBook_withMultipleReviews_returnsAllReviews() {
        // Review de alice
        UserBook aliceBook = new UserBook(testUser, testBook, ReadingStatus.COMPLETED);
        aliceBook.setRating(5);
        aliceBook.setPersonalNote("Amazing!");
        userBookRepository.save(aliceBook);

        // Review de bob
        UserBook bobBook = new UserBook(anotherUser, testBook, ReadingStatus.COMPLETED);
        bobBook.setRating(3);
        bobBook.setPersonalNote("OK book");
        userBookRepository.save(bobBook);

        List<ReviewDto> reviews = reviewService.getReviewsForBook(testBook.getId(), "alice");

        assertThat(reviews).hasSize(2);
        assertThat(reviews).extracting(ReviewDto::getUsername)
                .containsExactlyInAnyOrder("alice", "bob");
    }

    @Test
    void getReviewsForBook_marksOwnReviewCorrectly() {
        // Review de alice
        UserBook aliceBook = new UserBook(testUser, testBook, ReadingStatus.COMPLETED);
        aliceBook.setRating(5);
        userBookRepository.save(aliceBook);

        // Review de bob
        UserBook bobBook = new UserBook(anotherUser, testBook, ReadingStatus.COMPLETED);
        bobBook.setRating(3);
        userBookRepository.save(bobBook);

        // Vue depuis alice
        List<ReviewDto> aliceView = reviewService.getReviewsForBook(testBook.getId(), "alice");
        ReviewDto aliceReview = aliceView.stream()
                .filter(r -> r.getUsername().equals("alice"))
                .findFirst().orElseThrow();
        ReviewDto bobReviewFromAlice = aliceView.stream()
                .filter(r -> r.getUsername().equals("bob"))
                .findFirst().orElseThrow();

        assertThat(aliceReview.getIsOwnReview()).isTrue();
        assertThat(bobReviewFromAlice.getIsOwnReview()).isFalse();

        // Vue depuis bob
        List<ReviewDto> bobView = reviewService.getReviewsForBook(testBook.getId(), "bob");
        ReviewDto bobReview = bobView.stream()
                .filter(r -> r.getUsername().equals("bob"))
                .findFirst().orElseThrow();

        assertThat(bobReview.getIsOwnReview()).isTrue();
    }

    @Test
    void getReviewsForBook_withNullUsername_setsIsOwnReviewToFalse() {
        UserBook userBook = new UserBook(testUser, testBook, ReadingStatus.COMPLETED);
        userBook.setRating(4);
        userBookRepository.save(userBook);

        List<ReviewDto> reviews = reviewService.getReviewsForBook(testBook.getId(), null);

        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getIsOwnReview()).isFalse();
    }

    @Test
    void getReviewsForBook_withNonExistentBook_throwsNotFound() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reviewService.getReviewsForBook(99999L, "alice"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).contains("Livre introuvable");
    }

    @Test
    void getReviewsForBook_excludesUserBooksWithoutRating() {
        // UserBook avec rating
        UserBook withRating = new UserBook(testUser, testBook, ReadingStatus.COMPLETED);
        withRating.setRating(4);
        userBookRepository.save(withRating);

        // UserBook sans rating (juste dans la bibliothèque)
        Book anotherBook = createAndSaveBook("Another Book");
        UserBook withoutRating = new UserBook(anotherUser, testBook, ReadingStatus.IN_PROGRESS);
        // Pas de rating
        userBookRepository.save(withoutRating);

        List<ReviewDto> reviews = reviewService.getReviewsForBook(testBook.getId(), "alice");

        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getUsername()).isEqualTo("alice");
    }

    // ============= Tests pour addOrUpdateReview =============

    @Test
    void addOrUpdateReview_withValidData_createsReview() {
        ReviewDto review = reviewService.addOrUpdateReview("alice", testBook.getId(), 5, "Excellent book!");

        assertThat(review).isNotNull();
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getComment()).isEqualTo("Excellent book!");
        assertThat(review.getUsername()).isEqualTo("alice");
        assertThat(review.getBookId()).isEqualTo(testBook.getId());
        assertThat(review.getIsOwnReview()).isTrue();
    }

    @Test
    void addOrUpdateReview_withExistingReview_updatesReview() {
        // Créer une première review
        reviewService.addOrUpdateReview("alice", testBook.getId(), 3, "OK");

        // Mettre à jour
        ReviewDto updated = reviewService.addOrUpdateReview("alice", testBook.getId(), 5, "Actually amazing!");

        assertThat(updated.getRating()).isEqualTo(5);
        assertThat(updated.getComment()).isEqualTo("Actually amazing!");

        // Vérifier qu'il n'y a qu'une seule review
        List<ReviewDto> reviews = reviewService.getReviewsForBook(testBook.getId(), "alice");
        assertThat(reviews).hasSize(1);
    }

    @Test
    void addOrUpdateReview_withNullRating_throwsBadRequest() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reviewService.addOrUpdateReview("alice", testBook.getId(), null, "Comment"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("note doit être entre 1 et 5");
    }

    @Test
    void addOrUpdateReview_withRatingLessThan1_throwsBadRequest() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reviewService.addOrUpdateReview("alice", testBook.getId(), 0, "Comment"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("note doit être entre 1 et 5");
    }

    @Test
    void addOrUpdateReview_withRatingGreaterThan5_throwsBadRequest() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reviewService.addOrUpdateReview("alice", testBook.getId(), 6, "Comment"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("note doit être entre 1 et 5");
    }

    @Test
    void addOrUpdateReview_withCommentTooLong_throwsBadRequest() {
        String longComment = "A".repeat(1001);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reviewService.addOrUpdateReview("alice", testBook.getId(), 4, longComment));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("commentaire ne peut pas dépasser 1000");
    }

    @Test
    void addOrUpdateReview_withMaxLengthComment_succeeds() {
        String maxComment = "A".repeat(1000);

        ReviewDto review = reviewService.addOrUpdateReview("alice", testBook.getId(), 4, maxComment);

        assertThat(review.getComment()).hasSize(1000);
    }

    @Test
    void addOrUpdateReview_withNullComment_succeeds() {
        ReviewDto review = reviewService.addOrUpdateReview("alice", testBook.getId(), 4, null);

        assertThat(review.getRating()).isEqualTo(4);
        assertThat(review.getComment()).isNull();
    }

    @Test
    void addOrUpdateReview_withEmptyComment_succeeds() {
        ReviewDto review = reviewService.addOrUpdateReview("alice", testBook.getId(), 4, "");

        assertThat(review.getRating()).isEqualTo(4);
        assertThat(review.getComment()).isEmpty();
    }

    @Test
    void addOrUpdateReview_withUnknownUser_throwsUnauthorized() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reviewService.addOrUpdateReview("unknown", testBook.getId(), 4, "Comment"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).contains("Utilisateur introuvable");
    }

    @Test
    void addOrUpdateReview_withNonExistentBook_throwsNotFound() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reviewService.addOrUpdateReview("alice", 99999L, 4, "Comment"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).contains("Livre introuvable");
    }

    @Test
    void addOrUpdateReview_createsUserBookIfNotExists() {
        // Vérifier que l'utilisateur n'a pas encore ce livre
        assertThat(userBookRepository.findByUserIdAndBookId(testUser.getId(), testBook.getId())).isEmpty();

        // Ajouter une review (devrait créer le UserBook)
        ReviewDto review = reviewService.addOrUpdateReview("alice", testBook.getId(), 5, "Great!");

        assertThat(review).isNotNull();

        // Vérifier que le UserBook a été créé
        UserBook userBook = userBookRepository.findByUserIdAndBookId(testUser.getId(), testBook.getId())
                .orElseThrow();
        assertThat(userBook.getStatus()).isEqualTo(ReadingStatus.COMPLETED);
        assertThat(userBook.getRating()).isEqualTo(5);
    }

    @Test
    void addOrUpdateReview_withBoundaryRating1_succeeds() {
        ReviewDto review = reviewService.addOrUpdateReview("alice", testBook.getId(), 1, "Poor");

        assertThat(review.getRating()).isEqualTo(1);
    }

    @Test
    void addOrUpdateReview_withBoundaryRating5_succeeds() {
        ReviewDto review = reviewService.addOrUpdateReview("alice", testBook.getId(), 5, "Excellent");

        assertThat(review.getRating()).isEqualTo(5);
    }

    // ============= Tests pour deleteReview =============

    @Test
    void deleteReview_withExistingReview_removesRatingAndComment() {
        // Créer une review
        UserBook userBook = new UserBook(testUser, testBook, ReadingStatus.COMPLETED);
        userBook.setRating(4);
        userBook.setPersonalNote("Great book!");
        userBookRepository.save(userBook);

        // Supprimer la review
        reviewService.deleteReview("alice", testBook.getId());

        // Vérifier que rating et note sont null mais UserBook existe toujours
        UserBook updated = userBookRepository.findByUserIdAndBookId(testUser.getId(), testBook.getId())
                .orElseThrow();
        assertThat(updated.getRating()).isNull();
        assertThat(updated.getPersonalNote()).isNull();
        assertThat(updated.getStatus()).isEqualTo(ReadingStatus.COMPLETED); // Status préservé
    }

    @Test
    void deleteReview_withUnknownUser_throwsUnauthorized() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reviewService.deleteReview("unknown", testBook.getId()));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).contains("Utilisateur introuvable");
    }

    @Test
    void deleteReview_withNoExistingUserBook_throwsNotFound() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reviewService.deleteReview("alice", testBook.getId()));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).contains("Aucun avis trouvé");
    }

    @Test
    void deleteReview_doesNotAffectOtherUsersReviews() {
        // Review d'Alice
        UserBook aliceBook = new UserBook(testUser, testBook, ReadingStatus.COMPLETED);
        aliceBook.setRating(4);
        aliceBook.setPersonalNote("Alice's review");
        userBookRepository.save(aliceBook);

        // Review de Bob
        UserBook bobBook = new UserBook(anotherUser, testBook, ReadingStatus.COMPLETED);
        bobBook.setRating(5);
        bobBook.setPersonalNote("Bob's review");
        userBookRepository.save(bobBook);

        // Supprimer la review d'Alice
        reviewService.deleteReview("alice", testBook.getId());

        // Vérifier que la review de Bob est intacte
        UserBook bobBookAfter = userBookRepository.findByUserIdAndBookId(anotherUser.getId(), testBook.getId())
                .orElseThrow();
        assertThat(bobBookAfter.getRating()).isEqualTo(5);
        assertThat(bobBookAfter.getPersonalNote()).isEqualTo("Bob's review");
    }

    // ============= Tests de conversion DTO =============

    @Test
    void reviewDto_containsCorrectBookInfo() {
        UserBook userBook = new UserBook(testUser, testBook, ReadingStatus.COMPLETED);
        userBook.setRating(4);
        userBookRepository.save(userBook);

        List<ReviewDto> reviews = reviewService.getReviewsForBook(testBook.getId(), "alice");

        assertThat(reviews).hasSize(1);
        ReviewDto dto = reviews.get(0);
        assertThat(dto.getBookId()).isEqualTo(testBook.getId());
        assertThat(dto.getBookTitle()).isEqualTo("Test Book");
    }

    @Test
    void reviewDto_hasCreatedAtDate() {
        UserBook userBook = new UserBook(testUser, testBook, ReadingStatus.COMPLETED);
        userBook.setRating(4);
        userBookRepository.save(userBook);

        List<ReviewDto> reviews = reviewService.getReviewsForBook(testBook.getId(), "alice");

        assertThat(reviews.get(0).getCreatedAt()).isNotNull();
    }

    // ============= Helper methods =============

    private Book createAndSaveBook(String title) {
        Book book = new Book();
        book.setTitle(title);
        book.setDesc_txt("Description de " + title);
        book.setSubject(testSubject);
        book.setCover("https://example.com/cover.jpg");
        book.setTotalPages(300);
        book.setCreated_at(LocalDateTime.now());
        book.setUpdated_at(LocalDateTime.now());
        return bookRepository.save(book);
    }
}