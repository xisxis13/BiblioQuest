package prj.books_teamB_spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.model.ReadingSession;
import prj.books_teamB_spring.service.ReadingService;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReadingController {
    private final ReadingService readingService;

    @PostMapping("/reading-sessions")
    public ReadingSession createSession(@RequestParam Long userId, @RequestParam Long userBookId,
                                        @RequestParam Integer duration, @RequestParam Integer pagesRead) {
        return readingService.createSession(userId, userBookId, duration, pagesRead);
    }

    @GetMapping("/users/{id}/reading-sessions")
    public List<ReadingSession> getSessions(@PathVariable Long id) {
        return readingService.getSessions(id);
    }

    @PutMapping("/reading-sessions/{id}/share")
    public ReadingSession shareSession(@PathVariable Long id) {
        return readingService.shareSession(id);
    }

    @PostMapping("/reading-sessions/{id}/reaction")
    public ReadingSession addReaction(@PathVariable Long id, @RequestParam String comment, @RequestParam String mood) {
        return readingService.addReaction(id, comment, mood);
    }
}
