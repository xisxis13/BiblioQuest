package prj.books_teamB_spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.model.ReadingSession;
import prj.books_teamB_spring.service.ReadingService;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
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

    @PutMapping("/reading-sessions/{id}")
    public ReadingSession updateSession(@PathVariable Long id,
                                       @RequestParam(required = false) Integer duration,
                                       @RequestParam(required = false) Integer pagesRead,
                                       @RequestParam(required = false) String notes,
                                       @RequestParam(required = false) String mood) {
        return readingService.updateSession(id, duration, pagesRead, notes, mood);
    }

    @PostMapping("/reading-sessions/{id}/reaction")
    public ReadingSession addReaction(@PathVariable Long id, @RequestParam String comment, @RequestParam String mood) {
        return readingService.addReaction(id, comment, mood);
    }

    @GetMapping("/users/{id}/reading-stats")
    public String getReadingStats(@PathVariable Long id) {
        Long totalTime = readingService.getTotalReadingTime(id);
        Long totalPages = readingService.getTotalPagesRead(id);
        Double avgSpeed = readingService.getAverageReadingSpeed(id);
        
        return String.format("Temps total: %d min, Pages: %d, Vitesse: %.1f pages/min", 
                           totalTime != null ? totalTime : 0, 
                           totalPages != null ? totalPages : 0, 
                           avgSpeed != null ? avgSpeed : 0.0);
    }
}
