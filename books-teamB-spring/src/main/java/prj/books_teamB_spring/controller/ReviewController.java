package prj.books_teamB_spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.dto.ReviewDto;
import prj.books_teamB_spring.dto.UserBookDTO;
import prj.books_teamB_spring.service.ReviewService;
import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
@CrossOrigin(
    origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000", "http://127.0.0.1:5173"},
    allowedHeaders = {"*"},
    exposedHeaders = {"*"},
    allowCredentials = "true",
    maxAge = 3600
)
public class ReviewController {

    private final ReviewService reviewService;
    
    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<List<ReviewDto>> getBookReviews(
            @PathVariable Long bookId
        ) {
        
        List<ReviewDto> reviews = reviewService.getBookReviews(bookId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/reviews/update")
    public ResponseEntity<String> addOrUpdateReview(
            ReviewDto reviewDTO
        ) {
            reviewService.addOrUpdateReview(reviewDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");
    }

    @DeleteMapping("/reviews/delete")
    public ResponseEntity<Void> deleteReview(
        UserBookDTO userBookDTO
    ) {
        reviewService.deleteReview(userBookDTO);
        return ResponseEntity.noContent().build();
    }

}