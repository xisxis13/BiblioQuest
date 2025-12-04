package prj.books_teamB_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import prj.books_teamB_spring.dto.ReviewDto;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.model.ReadingStatus;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.UserBookRepository;
import prj.books_teamB_spring.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service simplifié pour gérer les avis sur les livres
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final UserBookRepository userBookRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * Récupère tous les avis d'un livre
     */
    public List<ReviewDto> getReviewsForBook(Long bookId, String currentUsername) {
        // Vérifier que le livre existe
        if (!bookRepository.existsById(bookId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Livre introuvable avec l'ID: " + bookId);
        }

        // Récupérer tous les UserBook avec rating (méthode simple)
        List<UserBook> allUserBooks = userBookRepository.findAll();
        List<UserBook> userBooksWithReviews = allUserBooks.stream()
                .filter(ub -> ub.getBook().getId() == bookId.longValue())
                .filter(ub -> ub.getRating() != null)
                .collect(Collectors.toList());

        // Convertir en DTOs
        return userBooksWithReviews.stream()
                .map(ub -> convertToDto(ub, currentUsername))
                .collect(Collectors.toList());
    }

    /**
     * Ajoute ou met à jour l'avis d'un utilisateur sur un livre
     */
    public ReviewDto addOrUpdateReview(String username, Long bookId, Integer rating, String comment) {
        // Validations
        if (rating == null || rating < 1 || rating > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La note doit être entre 1 et 5");
        }

        if (comment != null && comment.length() > 1000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Le commentaire ne peut pas dépasser 1000 caractères");
        }

        // Récupérer l'utilisateur
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));

        // Récupérer le livre
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Livre introuvable avec l'ID: " + bookId));

        // Chercher si l'utilisateur a déjà ce livre (méthode simple)
        List<UserBook> userBooks = userBookRepository.findByUserId(Long.valueOf(user.getId()));
        UserBook userBook = userBooks.stream()
                .filter(ub -> ub.getBook().getId() == bookId.longValue())
                .findFirst()
                .orElse(null);

        // Si pas encore dans la bibliothèque, créer une entrée
        if (userBook == null) {
            userBook = new UserBook();
            userBook.setUser(user);
            userBook.setBook(book);
            userBook.setStatus(ReadingStatus.COMPLETED);
            // Définir le progrès : utiliser totalPages si disponible, sinon 0
            Integer totalPages = book.getTotalPages();
            userBook.setProgress(totalPages != null ? totalPages : 0);
        }

        // Mettre à jour la note et le commentaire
        userBook.setRating(rating);
        userBook.setPersonalNote(comment);

        // Sauvegarder
        UserBook saved = userBookRepository.save(userBook);

        return convertToDto(saved, username);
    }

    /**
     * Supprime l'avis d'un utilisateur sur un livre
     */
    public void deleteReview(String username, Long bookId) {
        // Récupérer l'utilisateur
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));

        // Chercher l'avis (méthode simple)
        List<UserBook> userBooks = userBookRepository.findByUserId(Long.valueOf(user.getId()));
        UserBook userBook = userBooks.stream()
                .filter(ub -> ub.getBook().getId() == bookId.longValue())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aucun avis trouvé pour ce livre"));

        // Vérifier que c'est bien l'auteur de l'avis
        if (userBook.getUser().getId() != user.getId()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Vous ne pouvez supprimer que votre propre avis");
        }

        // Supprimer seulement la note et le commentaire
        userBook.setRating(null);
        userBook.setPersonalNote(null);
        userBookRepository.save(userBook);
    }

    /**
     * Convertit un UserBook en ReviewDto
     */
    private ReviewDto convertToDto(UserBook userBook, String currentUsername) {
        ReviewDto dto = new ReviewDto();
        dto.setId(userBook.getId());
        dto.setBookId(Long.valueOf(userBook.getBook().getId()));
        dto.setBookTitle(userBook.getBook().getTitle());
        dto.setUsername(userBook.getUser().getUsername());
        dto.setRating(userBook.getRating());
        dto.setComment(userBook.getPersonalNote());
        
        // Date de création de l'avis
        dto.setCreatedAt(userBook.getEndDate() != null ? userBook.getEndDate() : LocalDate.now());
        
        // Marquer si c'est l'avis de l'utilisateur connecté
        dto.setIsOwnReview(currentUsername != null && 
                          currentUsername.equals(userBook.getUser().getUsername()));
        
        return dto;
    }
}