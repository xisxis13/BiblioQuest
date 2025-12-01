package prj.books_teamB_spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
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
