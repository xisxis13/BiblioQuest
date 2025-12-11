package prj.books_teamB_spring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import prj.books_teamB_spring.model.ReadingStatus;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.service.UserBookService;
import prj.books_teamB_spring.dto.UserBookDTO;

@RestController
@RequestMapping("/api/user/books")
public class UserBookController {

    private final UserBookService userBookService;

    public UserBookController(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    /**
     * Récupère tous les livres d'un user.
     */
    @GetMapping
    public List<UserBook> getAllBooks(@RequestParam Long userId) {
        return userBookService.getAllBooks(userId);
    }

    /**
     * Ajoute un livre pour un user.
     */
    @PostMapping
    public ResponseEntity<UserBook> addBook(@RequestBody UserBookDTO userBookDto) {
        UserBook created = userBookService.addBook(userBookDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Supprime un livre pour un user.
     */
    @DeleteMapping("/id")
    public ResponseEntity<Void> deleteBook(@RequestBody UserBookDTO userBookDto) {
        userBookService.deleteBook(userBookDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Met à jour le readingStatus d'un livre pour un user.
     */
    @PostMapping("/update")
    public ResponseEntity<UserBook> updateReadingStatus(@RequestBody UserBookDTO userBookDto,
                                                        @RequestParam ReadingStatus readingStatus) {
        UserBook updated = userBookService.updateReadingStatus(userBookDto, readingStatus);
        return ResponseEntity.ok(updated);
    }
}
