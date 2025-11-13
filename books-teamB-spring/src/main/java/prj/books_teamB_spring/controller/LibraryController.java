package prj.books_teamB_spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.service.LibraryService;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    @GetMapping
    public List<UserBook> getLibrary(@PathVariable Long userId) {
        return libraryService.getUserLibrary(userId);
    }

    @PostMapping
    public UserBook addBook(@PathVariable Long userId, @RequestParam Long bookId) {
        return libraryService.addBookToLibrary(userId, bookId);
    }

    @PutMapping("/{bookId}")
    public UserBook updateProgress(@PathVariable Long userId, @PathVariable Long bookId, @RequestParam int progress) {
        return libraryService.updateProgress(userId, bookId, progress);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long userId, @PathVariable Long bookId) {
        libraryService.deleteUserBook(userId, bookId);
        return ResponseEntity.noContent().build();
    }
}
