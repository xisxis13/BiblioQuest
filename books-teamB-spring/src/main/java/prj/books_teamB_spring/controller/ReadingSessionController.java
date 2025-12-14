package prj.books_teamB_spring.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prj.books_teamB_spring.dto.ReadingSessionDTO;
import prj.books_teamB_spring.model.ReadingSession;
import prj.books_teamB_spring.service.ReadingSessionService;

@RestController
@RequestMapping("/api/reading-sessions")
@RequiredArgsConstructor
public class ReadingSessionController {

    private final ReadingSessionService readingService;

    @PostMapping("/")
    public ReadingSession addSession(@RequestBody ReadingSessionDTO readingSessionDTO) {
        System.out.println("readingSessionDTO : " + readingSessionDTO);
        return readingService.addSession(readingSessionDTO);
    }

    @GetMapping("")
    public List<ReadingSession> getSessions(@RequestParam Long userId) {
        System.out.println("HOLA : " + userId);
        return readingService.getSessions(userId);
    }

    @PutMapping("/{id}")
    public ReadingSession updateSession(@PathVariable Long id,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) Integer pagesRead,
            @RequestParam(required = false) String notes) {
        return readingService.updateSession(id, duration, pagesRead, notes);
    }
}
