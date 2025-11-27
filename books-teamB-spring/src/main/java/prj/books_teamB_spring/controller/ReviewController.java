package prj.books_teamB_spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.dto.ReviewDto;
import prj.books_teamB_spring.service.ReviewService;

import java.util.List;

/**
 * Contrôleur pour gérer les avis sur les livres
 * Version avec vraie logique métier
 */
@RestController
@RequestMapping("/api/books")
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

    /**
     * GET /api/books/{bookId}/reviews
     * Récupère tous les avis d'un livre
     */
    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<List<ReviewDto>> getBookReviews(
            @PathVariable Long bookId,
            @RequestHeader(value = "X-Username", required = false) String username) {
        
        List<ReviewDto> reviews = reviewService.getReviewsForBook(bookId, username);
        return ResponseEntity.ok(reviews);
    }

    /**
     * POST /api/books/{bookId}/reviews
     * Ajoute ou met à jour l'avis de l'utilisateur sur un livre
     */
    @PostMapping("/{bookId}/reviews")
    public ResponseEntity<ReviewDto> addOrUpdateReview(
            @PathVariable Long bookId,
            @RequestHeader("X-Username") String username,
            @RequestBody ReviewRequest request) {
        
        ReviewDto review = reviewService.addOrUpdateReview(
                username, 
                bookId, 
                request.getRating(), 
                request.getComment()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    /**
     * DELETE /api/books/{bookId}/reviews
     * Supprime l'avis de l'utilisateur sur un livre
     */
    @DeleteMapping("/{bookId}/reviews")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long bookId,
            @RequestHeader("X-Username") String username) {
        
        reviewService.deleteReview(username, bookId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Classe interne pour la requête d'ajout/modification d'avis
     */
    @lombok.Data
    public static class ReviewRequest {
        private Integer rating;
        private String comment;
    }
}