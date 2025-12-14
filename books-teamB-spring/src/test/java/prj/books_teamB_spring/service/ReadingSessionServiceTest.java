package prj.books_teamB_spring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import prj.books_teamB_spring.dto.ReadingSessionDTO;
import prj.books_teamB_spring.dto.UserBookDTO;
import prj.books_teamB_spring.model.ReadingSession;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.model.UserBookId;
import prj.books_teamB_spring.repository.ReadingSessionRepository;
import prj.books_teamB_spring.repository.UserBookRepository;

@ExtendWith(MockitoExtension.class)
class ReadingSessionServiceTest {

    @Mock
    private ReadingSessionRepository sessionRepository;

    @Mock
    private UserBookRepository userBookRepository;

    @InjectMocks
    private ReadingSessionService readingSessionService;

    private UserBook testUserBook;
    private UserBookId testUserBookId;
    private ReadingSession testSession;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        testUserBookId = new UserBookId(1L, 1L);
        
        testUserBook = new UserBook();
        testUserBook.setId(testUserBookId);

        testSession = new ReadingSession();
        testSession.setId(1L);
        testSession.setUserId(1L);
        testSession.setBookId(1L);
        testSession.setDuration(30);
        testSession.setStartPage(10);
        testSession.setEndPage(35);
        testSession.setSessionNotes("Test notes");
    }

    @Test
    void addSession_shouldCreateSession_whenUserBookExists() {
        when(userBookRepository.findByUserAndBook(1L, 1L)).thenReturn(Optional.of(testUserBook));
        when(sessionRepository.save(any(ReadingSession.class))).thenReturn(testSession);

        ReadingSessionDTO sessionDTO = new ReadingSessionDTO();
        UserBookDTO userBookDTO = new UserBookDTO(testSession.getUserId(), testSession.getUserId());
        sessionDTO.setUserBookDTO(userBookDTO);
        sessionDTO.setDuration(testSession.getDuration());
        sessionDTO.setStartPage(testSession.getStartPage());
        sessionDTO.setEndPage(testSession.getEndPage());
        sessionDTO.setSessionNotes(testSession.getSessionNotes());

        ReadingSession result = readingSessionService.addSession(sessionDTO);

        assertNotNull(result);
        verify(userBookRepository).findByUserAndBook(1L, 1L);
        verify(sessionRepository).save(any(ReadingSession.class));
    }

    @Test
    void addSession_shouldThrowException_whenUserBookNotFound() {
        when(userBookRepository.findByUserAndBook(999L, 999L)).thenReturn(Optional.empty());

        UserBookId invalidId = new UserBookId(999L, 999L);
        
        ReadingSessionDTO invalidSessionDTO = new ReadingSessionDTO();
        UserBookDTO userBookDTO = new UserBookDTO(invalidId.getUserId(), invalidId.getUserId());
        invalidSessionDTO.setUserBookDTO(userBookDTO);
        invalidSessionDTO.setDuration(30);
        invalidSessionDTO.setStartPage(10);
        invalidSessionDTO.setEndPage(25);
        
        Exception exception = assertThrows(RuntimeException.class, () -> readingSessionService.addSession(invalidSessionDTO));
        assertNotNull(exception);
        verify(userBookRepository).findByUserAndBook(999L, 999L);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void getSessions_shouldReturnUserSessions() {
        ReadingSession session1 = new ReadingSession();
        session1.setId(1L);
        
        ReadingSession session2 = new ReadingSession();
        session2.setId(2L);

        when(sessionRepository.findByUserId(1L)).thenReturn(List.of(session1, session2));

        List<ReadingSession> result = readingSessionService.getSessions(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(sessionRepository).findByUserId(1L);
    }

    @Test
    void getSessions_shouldReturnEmptyList_whenNoSessions() {
        when(sessionRepository.findByUserId(999L)).thenReturn(List.of());

        List<ReadingSession> result = readingSessionService.getSessions(999L);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(sessionRepository).findByUserId(999L);
    }

    @Test
    void updateSession_shouldUpdateDuration_whenSessionExists() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(ReadingSession.class))).thenReturn(testSession);

        ReadingSession result = readingSessionService.updateSession(1L, 45, null, null);

        assertNotNull(result);
        assertEquals(45, testSession.getDuration());
        verify(sessionRepository).findById(1L);
        verify(sessionRepository).save(testSession);
    }

    @Test
    void updateSession_shouldUpdateNotes_whenSessionExists() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(ReadingSession.class))).thenReturn(testSession);

        ReadingSession result = readingSessionService.updateSession(1L, null, null, "Updated notes");

        assertNotNull(result);
        assertEquals("Updated notes", testSession.getSessionNotes());
        verify(sessionRepository).findById(1L);
        verify(sessionRepository).save(testSession);
    }

    @Test
    void updateSession_shouldUpdateMultipleFields_whenSessionExists() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(ReadingSession.class))).thenReturn(testSession);

        ReadingSession result = readingSessionService.updateSession(1L, 60, null, "New notes");

        assertNotNull(result);
        assertEquals(60, testSession.getDuration());
        assertEquals("New notes", testSession.getSessionNotes());
        verify(sessionRepository).findById(1L);
        verify(sessionRepository).save(testSession);
    }

    @Test
    void updateSession_shouldThrowException_whenSessionNotFound() {
        when(sessionRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, 
            () -> readingSessionService.updateSession(999L, 30, null, null));

        assertNotNull(exception);
        assertEquals("Session introuvable", exception.getMessage());
        verify(sessionRepository).findById(999L);
        verify(sessionRepository, never()).save(any());
    }
}
