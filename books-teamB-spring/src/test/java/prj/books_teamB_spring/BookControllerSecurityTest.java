package prj.books_teamB_spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void searchBooks_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/books/search"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getBookById_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/books/200"))
            .andExpect(status().isUnauthorized());
    }
}
