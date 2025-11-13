package prj.books_teamB_spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.model.ReadingStatus;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.service.LibraryService;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class LibraryController {
    private final LibraryService libraryService;

    @GetMapping("/library")
    public List<UserBook> getLibrary(@PathVariable Long userId) {
        return libraryService.getUserLibrary(userId);
    }

    @PostMapping("/books")
    public UserBook addBook(@PathVariable Long userId, @RequestParam Long bookId) {
        if (libraryService.existsInLibrary(userId, bookId)) {
            throw new RuntimeException("Livre déjà dans la bibliothèque");
        }
        return libraryService.addBookToLibrary(userId, bookId);
    }

    @PutMapping("/books/{bookId}")
    public UserBook updateBook(@PathVariable Long userId, @PathVariable Long bookId, 
                              @RequestParam(required = false) ReadingStatus status,
                              @RequestParam(required = false) Integer progress,
                              @RequestParam(required = false) Integer rating,
                              @RequestParam(required = false) String personalNote) {
        return libraryService.updateUserBook(userId, bookId, status, progress, rating, personalNote);
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long userId, @PathVariable Long bookId) {
        libraryService.deleteUserBook(userId, bookId);
        return ResponseEntity.noContent().build();
    }
}
