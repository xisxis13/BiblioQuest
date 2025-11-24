package prj.books_teamB_spring.controller;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Edition;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.EditionRepository;

@RestController
@RequestMapping("/api/editions")
@RequiredArgsConstructor
@Validated
public class EditionController {

    private final EditionRepository editionRepository;
    private final BookRepository bookRepository;

    /**
     * Génère un identifiant court (<= 20 caractères) pour une nouvelle édition.
     */
    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }

    /**
     * Crée une nouvelle édition liée à un livre.
     *
     * Le JSON attendu ressemble à :
     * {
     *   "isbn": "978...",
     *   "publish_date": "2025-01-01",
     *   "pages": 320,
     *   "cover": "url/vers/couverture.jpg",
     *   "book": { "id": 1 }
     * }
     */
    @PostMapping
    public ResponseEntity<Edition> createEdition(
            @RequestHeader(name = "X-Username", required = false) String username,
            @Valid @RequestBody Edition edition) {

        if (edition.getBook() == null || edition.getBook().getId() <= 0) {
            // On ne peut pas créer d'édition sans livre
            return ResponseEntity.badRequest().build();
        }

        // Vérifier que le livre existe réellement
        Book book = bookRepository.findById(edition.getBook().getId())
                .orElse(null);

        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Générer un id
        if (edition.getId() == null || edition.getId().isBlank()) {
            edition.setId(generateId());
        }

        // Forcer la relation vers le Book géré par JPA
        edition.setBook(book);

        Edition saved = editionRepository.save(edition);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Récupère toutes les éditions pour un livre donné.
     * Utile si plus tard tu veux voir l'historique des éditions pour un Book.
     */
    @GetMapping("/by-book/{bookId}")
    public List<Edition> getEditionsForBook(@PathVariable Long bookId) {
        return editionRepository.findByBook_Id(bookId);
    }

    /**
     * Récupère une édition par son identifiant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Edition> getEditionById(@PathVariable String id) {
        return editionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
