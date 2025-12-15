package prj.books_teamB_spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.dto.ChallengeProgressDTO;
import prj.books_teamB_spring.dto.ReadingChallengeDto;
import prj.books_teamB_spring.model.ReadingChallenge;
import prj.books_teamB_spring.service.ChallengeService;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ReadingChallengeController {

    private final ChallengeService challengeService;

    // Creer
    @PostMapping("/{userId}")
    public ResponseEntity<ReadingChallenge> createChallenge(
            @PathVariable Long userId,
            @RequestBody ReadingChallengeDto dto) {
        ReadingChallenge created = challengeService.createChallenge(userId, dto);
        return ResponseEntity.ok(created);
    }

    // liste defis
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChallengeProgressDTO>> getUserChallenges(@PathVariable Long userId) {
        return ResponseEntity.ok(challengeService.getUserChallenges(userId));
    }

    // detail defi
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeProgressDTO> getChallenge(@PathVariable Long id) {
        return ResponseEntity.ok(challengeService.getProgress(id));
    }
}

