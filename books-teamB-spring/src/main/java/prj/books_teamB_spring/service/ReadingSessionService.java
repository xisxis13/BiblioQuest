package prj.books_teamB_spring.service;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import prj.books_teamB_spring.model.ReadingSession;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.model.UserBookId;
import prj.books_teamB_spring.repository.ReadingSessionRepository;
import prj.books_teamB_spring.repository.UserBookRepository;

@Service
@RequiredArgsConstructor
public class ReadingSessionService {
    private final ReadingSessionRepository sessionRepository;
    private final UserBookRepository userBookRepository;

    public ReadingSession addSession(UserBookId userBookId, Integer duration, Integer pagesRead) {
        UserBook userBook = userBookRepository.findByUserAndBook(
            userBookId.getUserId(),
            userBookId.getBookId()
        ).orElseThrow();
        
        ReadingSession session = new ReadingSession(  userBook, duration, pagesRead);
        return sessionRepository.save(session);
    }

    public List<ReadingSession> getSessions(Long userId) {
        return sessionRepository.findByUserId(userId);
    }

    public ReadingSession updateSession(Long sessionId, Integer duration, Integer pagesRead, String notes) {
        ReadingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session introuvable"));
        if (duration != null) session.setDuration(duration);
        if (notes != null) session.setSessionNotes(notes);
        return sessionRepository.save(session);
    }

}
