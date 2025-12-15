package prj.books_teamB_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prj.books_teamB_spring.dto.ReadingChallengeDto;
import prj.books_teamB_spring.dto.ChallengeProgressDTO;
import prj.books_teamB_spring.model.ReadingChallenge;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.repository.ReadingChallengeRepository;
import prj.books_teamB_spring.repository.UserBookRepository;
import prj.books_teamB_spring.repository.UserRepository;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeService {

    private final ReadingChallengeRepository challengeRepository;
    private final UserBookRepository userBookRepository;
    private final UserRepository userRepository;

    public ReadingChallenge createChallenge(Long userId, ReadingChallengeDto dto) {
        // pas 2 challenge meme temps
        List<ReadingChallenge> existingChallenges = challengeRepository.findByUserId(userId);
        
        boolean hasOverlap = existingChallenges.stream().anyMatch(c -> 
            (dto.getStartDate().isBefore(c.getEndDate()) && dto.getEndDate().isAfter(c.getStartDate()))
        );

        if (hasOverlap) {
            throw new IllegalArgumentException("pas 2 challenge meme temps");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("utilisateur pas trouve"));

        ReadingChallenge challenge = new ReadingChallenge();
        challenge.setUser(user);
        challenge.setName(dto.getName());
        challenge.setGoal(dto.getGoal());
        challenge.setStartDate(dto.getStartDate());
        challenge.setEndDate(dto.getEndDate());

        return challengeRepository.save(challenge);
    }

    public ChallengeProgressDTO getProgress(Long challengeId) {
        ReadingChallenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new RuntimeException("defi pas trouve"));
        
        // compte statut Completed entre dates
        List<UserBook> booksRead = userBookRepository.findCompletedBooksBetween(
            challenge.getUser().getId(), 
            challenge.getStartDate(), 
            challenge.getEndDate()
        );
        
        return new ChallengeProgressDTO(
            challenge.getId(),
            challenge.getName(),
            challenge.getGoal(),
            challenge.getStartDate(),
            challenge.getEndDate(),
            booksRead.size(),
            booksRead
        );
    }

    public List<ChallengeProgressDTO> getUserChallenges(Long userId) {
        List<ReadingChallenge> challenges = challengeRepository.findByUserId(userId);
        
        return challenges.stream().map(challenge -> {
            
            List<UserBook> booksRead = userBookRepository.findCompletedBooksBetween(
                userId, 
                challenge.getStartDate(), 
                challenge.getEndDate()
            );
            
            return new ChallengeProgressDTO(
                challenge.getId(),
                challenge.getName(),
                challenge.getGoal(),
                challenge.getStartDate(),
                challenge.getEndDate(),
                booksRead.size(),
                booksRead
            );
        }).toList();
    }
}