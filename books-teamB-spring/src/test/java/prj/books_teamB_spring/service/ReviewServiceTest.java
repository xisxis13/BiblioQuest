package prj.books_teamB_spring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import prj.books_teamB_spring.dto.ReviewDto;
import prj.books_teamB_spring.dto.UserBookDTO;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.model.UserBookId;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.UserBookRepository;
import prj.books_teamB_spring.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private UserBookRepository userBookRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User testUser;
    private User anotherUser;
    private UserBook testUserBook;
    private UserBook anotherUserBook;
    private UserBookId testUserBookId;

    @BeforeEach
    void setUp() {
        testUser = new User("alice", "alice@example.com", "password123", Role.USER);
        testUser.setId(1L);

        anotherUser = new User("bob", "bob@example.com", "password456", Role.USER);
        anotherUser.setId(2L);

        testUserBookId = new UserBookId(1L, 1L);
        
        testUserBook = new UserBook();
        testUserBook.setId(testUserBookId);
        testUserBook.setRating(4);
        testUserBook.setReview("Great book!");
        testUserBook.setReviewCreatedAt(LocalDateTime.now());

        anotherUserBook = new UserBook();
        UserBookId anotherId = new UserBookId(2L, 1L);
        anotherUserBook.setId(anotherId);
        anotherUserBook.setRating(5);
        anotherUserBook.setReview("Excellent!");
        anotherUserBook.setReviewCreatedAt(LocalDateTime.now());
    }

    // ============= Tests pour getBookReviews =============

    @Test
    void getBookReviews_shouldReturnEmptyList_whenNoReviews() {
        when(userBookRepository.findByBookId(1L)).thenReturn(List.of());

        List<ReviewDto> reviews = reviewService.getBookReviews(1L);

        assertNotNull(reviews);
        assertEquals(0, reviews.size());
        verify(userBookRepository).findByBookId(1L);
    }

    @Test
    void getBookReviews_shouldReturnReviews_whenReviewsExist() {
        when(userBookRepository.findByBookId(1L)).thenReturn(List.of(testUserBook));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        List<ReviewDto> reviews = reviewService.getBookReviews(1L);

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        assertEquals("alice", reviews.get(0).getUsername());
        assertEquals(4, reviews.get(0).getRating());
        assertEquals("Great book!", reviews.get(0).getComment());
        verify(userBookRepository).findByBookId(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void getBookReviews_shouldReturnMultipleReviews() {
        when(userBookRepository.findByBookId(1L)).thenReturn(List.of(testUserBook, anotherUserBook));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));

        List<ReviewDto> reviews = reviewService.getBookReviews(1L);

        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        verify(userBookRepository).findByBookId(1L);
        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
    }

    @Test
    void getBookReviews_shouldThrowException_whenUserNotFound() {
        when(userBookRepository.findByBookId(1L)).thenReturn(List.of(testUserBook));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> reviewService.getBookReviews(1L));
        verify(userBookRepository).findByBookId(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void getBookReviews_shouldMapAllFieldsCorrectly() {
        LocalDateTime createdAt = LocalDateTime.now();
        testUserBook.setReviewCreatedAt(createdAt);
        
        when(userBookRepository.findByBookId(1L)).thenReturn(List.of(testUserBook));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        List<ReviewDto> reviews = reviewService.getBookReviews(1L);

        ReviewDto review = reviews.get(0);
        assertEquals(1L, review.getUserBookDTO().getUserId());
        assertEquals(1L, review.getUserBookDTO().getBookId());
        assertEquals("alice", review.getUsername());
        assertEquals(4, review.getRating());
        assertEquals("Great book!", review.getComment());
        assertEquals(createdAt, review.getCreatedAt());
    }

    // ============= Tests pour addOrUpdateReview =============

    @Test
    void addOrUpdateReview_shouldCreateNewReview_whenUserBookNotExists() {
        UserBookDTO userBookDTO = new UserBookDTO(1L, 1L);
        ReviewDto reviewDto = new ReviewDto(userBookDTO, "alice", 5, "Amazing!", LocalDateTime.now());

        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.empty());
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        reviewService.addOrUpdateReview(reviewDto);

        verify(userBookRepository).findByUserAndBook(1L, 1L);
        verify(userBookRepository).save(any(UserBook.class));
    }

    @Test
    void addOrUpdateReview_shouldUpdateExistingReview_whenUserBookExists() {
        UserBookDTO userBookDTO = new UserBookDTO(1L, 1L);
        ReviewDto reviewDto = new ReviewDto(userBookDTO, "alice", 5, "Updated review!", LocalDateTime.now());

        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.of(testUserBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        reviewService.addOrUpdateReview(reviewDto);

        assertEquals(5, testUserBook.getRating());
        assertEquals("Updated review!", testUserBook.getReview());
        verify(userBookRepository).findByUserAndBook(1L, 1L);
        verify(userBookRepository).save(testUserBook);
    }

    @Test
    void addOrUpdateReview_shouldUpdateOnlyRating_whenNewRatingProvided() {
        UserBookDTO userBookDTO = new UserBookDTO(1L, 1L);
        ReviewDto reviewDto = new ReviewDto(userBookDTO, "alice", 3, "Changed rating", LocalDateTime.now());

        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.of(testUserBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        reviewService.addOrUpdateReview(reviewDto);

        assertEquals(3, testUserBook.getRating());
        verify(userBookRepository).save(testUserBook);
    }

    @Test
    void addOrUpdateReview_shouldHandleNullComment() {
        UserBookDTO userBookDTO = new UserBookDTO(1L, 1L);
        ReviewDto reviewDto = new ReviewDto(userBookDTO, "alice", 4, null, LocalDateTime.now());

        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.empty());
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        reviewService.addOrUpdateReview(reviewDto);

        verify(userBookRepository).findByUserAndBook(1L, 1L);
        verify(userBookRepository).save(any(UserBook.class));
    }

    @Test
    void addOrUpdateReview_shouldCreateWithCorrectParameters() {
        UserBookDTO userBookDTO = new UserBookDTO(1L, 2L);
        ReviewDto reviewDto = new ReviewDto(userBookDTO, "alice", 5, "Excellent!", LocalDateTime.now());

        when(userBookRepository.findByUserAndBook(1L, 2L)).thenReturn(Optional.empty());
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        reviewService.addOrUpdateReview(reviewDto);

        verify(userBookRepository).findByUserAndBook(1L, 2L);
        verify(userBookRepository).save(any(UserBook.class));
    }

    // ============= Tests pour deleteReview =============

    @Test
    void deleteReview_shouldRemoveRatingAndReview_whenUserBookExists() {
        UserBookDTO userBookDTO = new UserBookDTO(1L, 1L);

        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.of(testUserBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        reviewService.deleteReview(userBookDTO);

        assertNull(testUserBook.getRating());
        assertNull(testUserBook.getReview());
        verify(userBookRepository).findByUserAndBook(1L, 1L);
        verify(userBookRepository).save(testUserBook);
    }

    @Test
    void deleteReview_shouldThrowException_whenUserBookNotFound() {
        UserBookDTO userBookDTO = new UserBookDTO(999L, 999L);

        when(userBookRepository.findByUserAndBook(999L, 999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> reviewService.deleteReview(userBookDTO));
        assertNotNull(exception);
        verify(userBookRepository).findByUserAndBook(999L, 999L);
        verify(userBookRepository, never()).save(any());
    }

    @Test
    void deleteReview_shouldOnlyRemoveReviewFields_andNotUserBook() {
        UserBookDTO userBookDTO = new UserBookDTO(1L, 1L);
        
        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.of(testUserBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        reviewService.deleteReview(userBookDTO);

        // UserBook still exists, just rating and review are null
        assertNull(testUserBook.getRating());
        assertNull(testUserBook.getReview());
        assertNotNull(testUserBook.getId());
        verify(userBookRepository).save(testUserBook);
    }

    @Test
    void deleteReview_shouldSetBothFieldsToNull() {
        testUserBook.setRating(5);
        testUserBook.setReview("Some review");
        
        UserBookDTO userBookDTO = new UserBookDTO(1L, 1L);

        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.of(testUserBook));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(testUserBook);

        reviewService.deleteReview(userBookDTO);

        assertNull(testUserBook.getRating());
        assertNull(testUserBook.getReview());
        verify(userBookRepository).save(testUserBook);
    }
}
