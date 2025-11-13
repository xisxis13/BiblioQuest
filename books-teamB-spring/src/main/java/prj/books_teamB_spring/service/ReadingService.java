package prj.books_teamB_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prj.books_teamB_spring.model.*;
import prj.books_teamB_spring.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadingService {
    private final ReadingSessionRepository sessionRepository;
    private final UserBookRepository userBookRepository;
    private final UserRepository userRepository;

    public ReadingSession createSession(Long userId, Long userBookId, Integer duration, Integer pagesRead) {
        User user = userRepository.findById(userId).orElseThrow();
        UserBook userBook = userBookRepository.findById(userBookId).orElseThrow();
        ReadingSession session = new ReadingSession(user, userBook.getBook(), userBook, duration, pagesRead);
        return sessionRepository.save(session);
    }

    public List<ReadingSession> getSessions(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return sessionRepository.findByUser(user);
    }

    public ReadingSession shareSession(Long sessionId) {
        ReadingSession session = sessionRepository.findById(sessionId).orElseThrow();
        session.setIsShareable(true);
        return sessionRepository.save(session);
    }

    public ReadingSession addReaction(Long sessionId, String comment, String mood) {
        ReadingSession session = sessionRepository.findById(sessionId).orElseThrow();
        session.setSessionNotes(comment);
        session.setMood(mood);
        return sessionRepository.save(session);
    }
}
