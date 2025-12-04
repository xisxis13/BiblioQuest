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

import java.time.LocalDate;
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

import prj.books_teamB_spring.controller.ReviewController;
import prj.books_teamB_spring.dto.ReviewDto;
import prj.books_teamB_spring.service.ReviewService;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class ReviewControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ReviewService reviewService;

    // ============= Tests pour GET /api/books/{bookId}/reviews =============

    @Test
    void getBookReviews_returnsReviewsList() throws Exception {
        ReviewDto review1 = createReviewDto(1L, 1L, "Book Title", "alice", 5, "Amazing!", true);
        ReviewDto review2 = createReviewDto(2L, 1L, "Book Title", "bob", 4, "Good book", false);

        when(reviewService.getReviewsForBook(1L, "alice")).thenReturn(List.of(review1, review2));

        mvc.perform(get("/api/books/1/reviews")
                        .header("X-Username", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("Amazing!"))
                .andExpect(jsonPath("$[0].isOwnReview").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("bob"))
                .andExpect(jsonPath("$[1].isOwnReview").value(false));
    }

    @Test
    void getBookReviews_withEmptyList_returnsEmptyArray() throws Exception {
        when(reviewService.getReviewsForBook(1L, "alice")).thenReturn(Collections.emptyList());

        mvc.perform(get("/api/books/1/reviews")
                        .header("X-Username", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBookReviews_withoutUsername_returnsReviews() throws Exception {
        ReviewDto review = createReviewDto(1L, 1L, "Book Title", "alice", 5, "Amazing!", false);

        when(reviewService.getReviewsForBook(eq(1L), any())).thenReturn(List.of(review));

        mvc.perform(get("/api/books/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBookReviews_withNonExistentBook_returns404() throws Exception {
        when(reviewService.getReviewsForBook(99999L, "alice"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Livre introuvable avec l'ID: 99999"));

        mvc.perform(get("/api/books/99999/reviews")
                        .header("X-Username", "alice"))
                .andExpect(status().isNotFound());
    }

    // ============= Tests pour POST /api/books/{bookId}/reviews =============

    @Test
    void addOrUpdateReview_createsReview() throws Exception {
        ReviewDto createdReview = createReviewDto(1L, 1L, "Book Title", "alice", 5, "Excellent!", true);

        when(reviewService.addOrUpdateReview("alice", 1L, 5, "Excellent!")).thenReturn(createdReview);

        String requestBody = """
                {
                    "rating": 5,
                    "comment": "Excellent!"
                }
                """;

        mvc.perform(post("/api/books/1/reviews")
                        .header("X-Username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Excellent!"))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.isOwnReview").value(true));

        verify(reviewService).addOrUpdateReview("alice", 1L, 5, "Excellent!");
    }

    @Test
    void addOrUpdateReview_withOnlyRating_succeeds() throws Exception {
        ReviewDto createdReview = createReviewDto(1L, 1L, "Book Title", "alice", 4, null, true);

        when(reviewService.addOrUpdateReview("alice", 1L, 4, null)).thenReturn(createdReview);

        String requestBody = """
                {
                    "rating": 4
                }
                """;

        mvc.perform(post("/api/books/1/reviews")
                        .header("X-Username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    void addOrUpdateReview_withMissingUsername_returns400() throws Exception {
        String requestBody = """
                {
                    "rating": 5,
                    "comment": "Great!"
                }
                """;

        mvc.perform(post("/api/books/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addOrUpdateReview_withInvalidRating_returns400() throws Exception {
        when(reviewService.addOrUpdateReview("alice", 1L, 0, "Comment"))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "La note doit être entre 1 et 5"));

        String requestBody = """
                {
                    "rating": 0,
                    "comment": "Comment"
                }
                """;

        mvc.perform(post("/api/books/1/reviews")
                        .header("X-Username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addOrUpdateReview_withRatingTooHigh_returns400() throws Exception {
        when(reviewService.addOrUpdateReview("alice", 1L, 6, "Comment"))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "La note doit être entre 1 et 5"));

        String requestBody = """
                {
                    "rating": 6,
                    "comment": "Comment"
                }
                """;

        mvc.perform(post("/api/books/1/reviews")
                        .header("X-Username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addOrUpdateReview_withCommentTooLong_returns400() throws Exception {
        String longComment = "A".repeat(1001);
        
        when(reviewService.addOrUpdateReview("alice", 1L, 4, longComment))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le commentaire ne peut pas dépasser 1000 caractères"));

        String requestBody = String.format("""
                {
                    "rating": 4,
                    "comment": "%s"
                }
                """, longComment);

        mvc.perform(post("/api/books/1/reviews")
                        .header("X-Username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addOrUpdateReview_withUnknownUser_returns401() throws Exception {
        when(reviewService.addOrUpdateReview("unknown", 1L, 5, "Great!"))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));

        String requestBody = """
                {
                    "rating": 5,
                    "comment": "Great!"
                }
                """;

        mvc.perform(post("/api/books/1/reviews")
                        .header("X-Username", "unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addOrUpdateReview_withNonExistentBook_returns404() throws Exception {
        when(reviewService.addOrUpdateReview("alice", 99999L, 5, "Great!"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Livre introuvable avec l'ID: 99999"));

        String requestBody = """
                {
                    "rating": 5,
                    "comment": "Great!"
                }
                """;

        mvc.perform(post("/api/books/99999/reviews")
                        .header("X-Username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void addOrUpdateReview_updatesExistingReview() throws Exception {
        ReviewDto updatedReview = createReviewDto(1L, 1L, "Book Title", "alice", 4, "Changed my mind!", true);

        when(reviewService.addOrUpdateReview("alice", 1L, 4, "Changed my mind!")).thenReturn(updatedReview);

        String requestBody = """
                {
                    "rating": 4,
                    "comment": "Changed my mind!"
                }
                """;

        mvc.perform(post("/api/books/1/reviews")
                        .header("X-Username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Changed my mind!"));
    }

    // ============= Tests pour DELETE /api/books/{bookId}/reviews =============

    @Test
    void deleteReview_removesReview() throws Exception {
        doNothing().when(reviewService).deleteReview("alice", 1L);

        mvc.perform(delete("/api/books/1/reviews")
                        .header("X-Username", "alice"))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview("alice", 1L);
    }

    @Test
    void deleteReview_withMissingUsername_returns400() throws Exception {
        mvc.perform(delete("/api/books/1/reviews"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReview_withUnknownUser_returns401() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"))
                .when(reviewService).deleteReview("unknown", 1L);

        mvc.perform(delete("/api/books/1/reviews")
                        .header("X-Username", "unknown"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteReview_withNoExistingReview_returns404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun avis trouvé pour ce livre"))
                .when(reviewService).deleteReview("alice", 1L);

        mvc.perform(delete("/api/books/1/reviews")
                        .header("X-Username", "alice"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReview_withOtherUsersReview_returns403() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez supprimer que votre propre avis"))
                .when(reviewService).deleteReview("alice", 1L);

        mvc.perform(delete("/api/books/1/reviews")
                        .header("X-Username", "alice"))
                .andExpect(status().isForbidden());
    }

    // ============= Tests avec différents ratings valides =============

    @Test
    void addOrUpdateReview_withRating1_succeeds() throws Exception {
        ReviewDto review = createReviewDto(1L, 1L, "Book Title", "alice", 1, "Terrible", true);
        when(reviewService.addOrUpdateReview("alice", 1L, 1, "Terrible")).thenReturn(review);

        String requestBody = """
                {
                    "rating": 1,
                    "comment": "Terrible"
                }
                """;

        mvc.perform(post("/api/books/1/reviews")
                        .header("X-Username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(1));
    }

    @Test
    void addOrUpdateReview_withRating5_succeeds() throws Exception {
        ReviewDto review = createReviewDto(1L, 1L, "Book Title", "alice", 5, "Perfect", true);
        when(reviewService.addOrUpdateReview("alice", 1L, 5, "Perfect")).thenReturn(review);

        String requestBody = """
                {
                    "rating": 5,
                    "comment": "Perfect"
                }
                """;

        mvc.perform(post("/api/books/1/reviews")
                        .header("X-Username", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5));
    }

    // ============= Tests de vérification du contenu de la réponse =============

    @Test
    void getBookReviews_returnsCompleteReviewData() throws Exception {
        ReviewDto review = new ReviewDto();
        review.setId(1L);
        review.setBookId(42L);
        review.setBookTitle("The Great Book");
        review.setUsername("reviewer");
        review.setRating(4);
        review.setComment("Very good read!");
        review.setCreatedAt(LocalDate.of(2024, 1, 15));
        review.setIsOwnReview(false);

        when(reviewService.getReviewsForBook(42L, "alice")).thenReturn(List.of(review));

        mvc.perform(get("/api/books/42/reviews")
                        .header("X-Username", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookId").value(42))
                .andExpect(jsonPath("$[0].bookTitle").value("The Great Book"))
                .andExpect(jsonPath("$[0].username").value("reviewer"))
                .andExpect(jsonPath("$[0].rating").value(4))
                .andExpect(jsonPath("$[0].comment").value("Very good read!"))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].isOwnReview").value(false));
    }

    // ============= Helper methods =============

    private ReviewDto createReviewDto(Long id, Long bookId, String bookTitle, String username, 
                                       Integer rating, String comment, Boolean isOwnReview) {
        ReviewDto dto = new ReviewDto();
        dto.setId(id);
        dto.setBookId(bookId);
        dto.setBookTitle(bookTitle);
        dto.setUsername(username);
        dto.setRating(rating);
        dto.setComment(comment);
        dto.setCreatedAt(LocalDate.now());
        dto.setIsOwnReview(isOwnReview);
        return dto;
    }
}