package prj.books_teamB_spring.controller;

import static org.mockito.ArgumentMatchers.any;
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

import java.time.LocalDateTime;
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

import prj.books_teamB_spring.dto.ReviewDto;
import prj.books_teamB_spring.dto.UserBookDTO;
import prj.books_teamB_spring.service.ReviewService;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private ReviewDto reviewDto1;
    private ReviewDto reviewDto2;
    private UserBookDTO userBookDTO;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
                .build();

        userBookDTO = new UserBookDTO(1L, 100L);

        reviewDto1 = new ReviewDto();
        reviewDto1.setUserBookDTO(userBookDTO);
        reviewDto1.setUsername("john_doe");
        reviewDto1.setRating(5);
        reviewDto1.setComment("Excellent book!");
        reviewDto1.setCreatedAt(LocalDateTime.now());

        reviewDto2 = new ReviewDto();
        reviewDto2.setUserBookDTO(new UserBookDTO(2L, 100L));
        reviewDto2.setUsername("jane_smith");
        reviewDto2.setRating(4);
        reviewDto2.setComment("Very good read.");
        reviewDto2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getBookReviews_shouldReturnListOfReviews() throws Exception {
        when(reviewService.getBookReviews(100L)).thenReturn(List.of(reviewDto1, reviewDto2));

        mockMvc.perform(get("/api/books/100/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("john_doe")))
                .andExpect(jsonPath("$[0].rating", is(5)))
                .andExpect(jsonPath("$[0].comment", is("Excellent book!")))
                .andExpect(jsonPath("$[1].username", is("jane_smith")))
                .andExpect(jsonPath("$[1].rating", is(4)));

        verify(reviewService).getBookReviews(100L);
    }

    @Test
    void getBookReviews_shouldReturnEmptyList() throws Exception {
        when(reviewService.getBookReviews(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/books/999/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(reviewService).getBookReviews(999L);
    }

    @Test
    void addOrUpdateReview_shouldReturnCreated() throws Exception {
        doNothing().when(reviewService).addOrUpdateReview(any(ReviewDto.class));

        String jsonRequest = "{\"userBookDTO\":{\"userId\":1,\"bookId\":100},\"username\":\"john_doe\",\"rating\":5,\"comment\":\"Excellent book!\"}";

        mockMvc.perform(post("/api/books/reviews/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated());

        verify(reviewService).addOrUpdateReview(any(ReviewDto.class));
    }

    @Test
    void addOrUpdateReview_shouldUpdateExistingReview() throws Exception {
        doNothing().when(reviewService).addOrUpdateReview(any(ReviewDto.class));

        String jsonRequest = "{\"userBookDTO\":{\"userId\":1,\"bookId\":100},\"username\":\"john_doe\",\"rating\":3,\"comment\":\"Changed my mind.\"}";

        mockMvc.perform(post("/api/books/reviews/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated());

        verify(reviewService).addOrUpdateReview(any(ReviewDto.class));
    }

    @Test
    void deleteReview_shouldReturnNoContent() throws Exception {
        doNothing().when(reviewService).deleteReview(any(UserBookDTO.class));

        String jsonRequest = "{\"userId\":1,\"bookId\":100}";

        mockMvc.perform(delete("/api/books/reviews/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(any(UserBookDTO.class));
    }
}