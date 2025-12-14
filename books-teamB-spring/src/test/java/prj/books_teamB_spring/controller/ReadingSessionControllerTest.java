package prj.books_teamB_spring.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import prj.books_teamB_spring.exception.GlobalExceptionHandler;
import prj.books_teamB_spring.model.ReadingSession;
import prj.books_teamB_spring.model.UserBookId;
import prj.books_teamB_spring.service.ReadingSessionService;

@ExtendWith(MockitoExtension.class)
public class ReadingSessionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReadingSessionService readingSessionService;

    @InjectMocks
    private ReadingSessionController readingSessionController;

    private ReadingSession session1;
    private UserBookId userBookId1;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(readingSessionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userBookId1 = new UserBookId(1L, 1L);

        session1 = new ReadingSession();
        session1.setId(1L);
        session1.setUserId(1L);
        session1.setBookId(1L);
        session1.setDuration(60);
        session1.setStartPage(1);
        session1.setEndPage(50);
    }

    @Test
    void getSessions_shouldReturnListOfSessions() throws Exception {
        ReadingSession session2 = new ReadingSession();
        session2.setId(2L);
        session2.setUserId(1L);
        session2.setBookId(1L);
        session2.setDuration(30);
        session2.setStartPage(1);
        session2.setEndPage(20);

        when(readingSessionService.getSessions(1L)).thenReturn(List.of(session1, session2));

        mockMvc.perform(get("/api/reading-sessions/")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void updateSession_shouldReturnUpdatedSession() throws Exception {
        ReadingSession updatedSession = new ReadingSession();
        updatedSession.setId(1L);
        updatedSession.setUserId(1L);
        updatedSession.setBookId(1L);
        updatedSession.setDuration(75);
        updatedSession.setStartPage(1);
        updatedSession.setEndPage(60);
        updatedSession.setSessionNotes("Updated notes");

        when(readingSessionService.updateSession(anyLong(), anyInt(), anyInt(), anyString())).thenReturn(updatedSession);

        mockMvc.perform(put("/api/reading-sessions/1")
                .param("duration", "75")
                .param("pagesRead", "60")
                .param("notes", "Updated notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duration", is(75)))
                .andExpect(jsonPath("$.startPage", is(1)))
                .andExpect(jsonPath("$.endPage", is(60)))
                .andExpect(jsonPath("$.sessionNotes", is("Updated notes")));
    }
}
