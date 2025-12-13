package prj.books_teamB_spring.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import prj.books_teamB_spring.model.ReadingSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReadingSessionRepositoryTest {

    @Autowired
    private ReadingSessionRepository readingSessionRepository;

    private ReadingSession testSession;

    @BeforeEach
    void setUp() {
        readingSessionRepository.deleteAll();
        testSession = new ReadingSession();
        testSession.setUserId(1L);
        testSession.setBookId(1L);
        testSession.setDuration(30);
        testSession.setSessionDate(LocalDateTime.now());
        testSession.setStartPage(1);
        testSession.setEndPage(30);
        testSession = readingSessionRepository.save(testSession);
    }

    @Test
    void save_shouldPersistReadingSession() {
        ReadingSession newSession = new ReadingSession();
        newSession.setUserId(2L);
        newSession.setBookId(2L);
        newSession.setDuration(45);
        newSession.setSessionDate(LocalDateTime.now());

        ReadingSession saved = readingSessionRepository.save(newSession);

        assertThat(saved.getId()).isNotNull();
        assertThat(readingSessionRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void findById_shouldReturnSession_whenSessionExists() {
        Optional<ReadingSession> found = readingSessionRepository.findById(testSession.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getDuration()).isEqualTo(30);
    }

    @Test
    void findById_shouldReturnEmpty_whenSessionDoesNotExist() {
        Optional<ReadingSession> found = readingSessionRepository.findById(99999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findByUserId_shouldReturnUserSessions() {
        ReadingSession session2 = new ReadingSession();
        session2.setUserId(1L);
        session2.setBookId(2L);
        session2.setDuration(20);
        session2.setSessionDate(LocalDateTime.now());
        readingSessionRepository.save(session2);

        ReadingSession session3 = new ReadingSession();
        session3.setUserId(2L);
        session3.setBookId(1L);
        session3.setDuration(15);
        session3.setSessionDate(LocalDateTime.now());
        readingSessionRepository.save(session3);

        List<ReadingSession> userSessions = readingSessionRepository.findByUserId(1L);

        assertThat(userSessions).hasSize(2);
        assertThat(userSessions).allMatch(s -> s.getUserId().equals(1L));
    }

    @Test
    void findByUserId_shouldReturnEmpty_whenUserHasNoSessions() {
        List<ReadingSession> userSessions = readingSessionRepository.findByUserId(999L);

        assertThat(userSessions).isEmpty();
    }

    @Test
    void findByUserIdAndBookId_shouldReturnSpecificSessions() {
        ReadingSession session2 = new ReadingSession();
        session2.setUserId(1L);
        session2.setBookId(1L);
        session2.setDuration(25);
        session2.setSessionDate(LocalDateTime.now());
        readingSessionRepository.save(session2);

        ReadingSession session3 = new ReadingSession();
        session3.setUserId(1L);
        session3.setBookId(2L);
        session3.setDuration(40);
        session3.setSessionDate(LocalDateTime.now());
        readingSessionRepository.save(session3);

        List<ReadingSession> sessions = readingSessionRepository.findByUserIdAndBookId(1L, 1L);

        assertThat(sessions).hasSize(2);
        assertThat(sessions).allMatch(s -> s.getUserId().equals(1L) && s.getBookId().equals(1L));
    }

    @Test
    void findByUserIdAndBookId_shouldReturnEmpty_whenNoMatchingSessions() {
        List<ReadingSession> sessions = readingSessionRepository.findByUserIdAndBookId(999L, 999L);

        assertThat(sessions).isEmpty();
    }

    @Test
    void deleteById_shouldRemoveSession() {
        readingSessionRepository.deleteById(testSession.getId());

        assertThat(readingSessionRepository.findById(testSession.getId())).isEmpty();
    }
}
