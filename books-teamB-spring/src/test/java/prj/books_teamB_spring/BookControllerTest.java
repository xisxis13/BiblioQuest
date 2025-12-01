package prj.books_teamB_spring;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import prj.books_teamB_spring.controller.CatalogController;
import prj.books_teamB_spring.dto.AuthorDto;
import prj.books_teamB_spring.dto.BookDetailDto;
import prj.books_teamB_spring.dto.BookSummaryDto;
import prj.books_teamB_spring.dto.SubjectDto;
import prj.books_teamB_spring.service.BookService;

/**
 * Tests pour BookController - Catalogue public
 * Teste les endpoints /api/catalog/search et /api/catalog/{id}
 */
@WebMvcTest(controllers = CatalogController.class)
@WithMockUser
class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @Test
    void searchBooks_withoutFilters_returnsAllBooks() throws Exception {
        // Arrange
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(1L);
        subjectDto.setText("Fantasy");

        BookSummaryDto dto1 = new BookSummaryDto();
        dto1.setId(1L);
        dto1.setTitle("The Hobbit");
        dto1.setCover("https://example.com/hobbit.jpg");
        dto1.setSubject(subjectDto);
        dto1.setAuthorsNames(List.of("J.R.R. Tolkien"));

        BookSummaryDto dto2 = new BookSummaryDto();
        dto2.setId(2L);
        dto2.setTitle("The Lord of the Rings");
        dto2.setCover("https://example.com/lotr.jpg");
        dto2.setSubject(subjectDto);
        dto2.setAuthorsNames(List.of("J.R.R. Tolkien"));

        Page<BookSummaryDto> page = new PageImpl<>(List.of(dto1, dto2), PageRequest.of(0, 20), 2);
        when(bookService.searchBooks(any(), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mvc.perform(get("/api/catalog/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("The Hobbit"))
                .andExpect(jsonPath("$.content[1].title").value("The Lord of the Rings"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void searchBooks_withAuthorFilter_returnsFilteredBooks() throws Exception {
        // Arrange
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(1L);
        subjectDto.setText("Fantasy");

        BookSummaryDto dto = new BookSummaryDto();
        dto.setId(1L);
        dto.setTitle("The Hobbit");
        dto.setCover("https://example.com/hobbit.jpg");
        dto.setSubject(subjectDto);
        dto.setAuthorsNames(List.of("J.R.R. Tolkien"));

        Page<BookSummaryDto> page = new PageImpl<>(Collections.singletonList(dto));
        when(bookService.searchBooks(any(), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mvc.perform(get("/api/catalog/search")
                .param("author", "Tolkien"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("The Hobbit"))
                .andExpect(jsonPath("$.content[0].authorsNames[0]").value("J.R.R. Tolkien"));
    }

    @Test
    void searchBooks_withSubjectFilter_returnsFilteredBooks() throws Exception {
        // Arrange
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(2L);
        subjectDto.setText("Science Fiction");

        BookSummaryDto dto = new BookSummaryDto();
        dto.setId(3L);
        dto.setTitle("Dune");
        dto.setCover("https://example.com/dune.jpg");
        dto.setSubject(subjectDto);
        dto.setAuthorsNames(List.of("Frank Herbert"));

        Page<BookSummaryDto> page = new PageImpl<>(Collections.singletonList(dto));
        when(bookService.searchBooks(any(), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mvc.perform(get("/api/catalog/search")
                .param("subject", "Science Fiction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Dune"))
                .andExpect(jsonPath("$.content[0].subject.text").value("Science Fiction"));
    }

    @Test
    void searchBooks_withTitleFilter_returnsFilteredBooks() throws Exception {
        // Arrange
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(1L);
        subjectDto.setText("Fantasy");

        BookSummaryDto dto = new BookSummaryDto();
        dto.setId(1L);
        dto.setTitle("The Hobbit");
        dto.setCover("https://example.com/hobbit.jpg");
        dto.setSubject(subjectDto);
        dto.setAuthorsNames(List.of("J.R.R. Tolkien"));

        Page<BookSummaryDto> page = new PageImpl<>(Collections.singletonList(dto));
        when(bookService.searchBooks(any(), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mvc.perform(get("/api/catalog/search")
                .param("title", "Hobbit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("The Hobbit"));
    }

    @Test
    void searchBooks_withYearFilter_returnsFilteredBooks() throws Exception {
        // Arrange
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(1L);
        subjectDto.setText("Fantasy");

        BookSummaryDto dto = new BookSummaryDto();
        dto.setId(1L);
        dto.setTitle("The Hobbit");
        dto.setCover("https://example.com/hobbit.jpg");
        dto.setSubject(subjectDto);
        dto.setAuthorsNames(List.of("J.R.R. Tolkien"));

        Page<BookSummaryDto> page = new PageImpl<>(Collections.singletonList(dto));
        when(bookService.searchBooks(any(), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mvc.perform(get("/api/catalog/search")
                .param("year", "1937"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("The Hobbit"));
    }

    @Test
    void searchBooks_withPagination_returnsCorrectPage() throws Exception {
        // Arrange
        List<BookSummaryDto> content = new ArrayList<>();
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(1L);
        subjectDto.setText("Fantasy");

        for (int i = 0; i < 10; i++) {
            BookSummaryDto dto = new BookSummaryDto();
            dto.setId((long) i);
            dto.setTitle("Book " + i);
            dto.setCover("https://example.com/book" + i + ".jpg");
            dto.setSubject(subjectDto);
            dto.setAuthorsNames(List.of("Author " + i));
            content.add(dto);
        }

        Page<BookSummaryDto> page = new PageImpl<>(content, PageRequest.of(0, 10), 50);
        when(bookService.searchBooks(any(), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mvc.perform(get("/api/catalog/search")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(50))
                .andExpect(jsonPath("$.totalPages").value(5))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void searchBooks_withMultipleFilters_returnsCombinedResults() throws Exception {
        // Arrange
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(1L);
        subjectDto.setText("Fantasy");

        BookSummaryDto dto = new BookSummaryDto();
        dto.setId(1L);
        dto.setTitle("The Hobbit");
        dto.setCover("https://example.com/hobbit.jpg");
        dto.setSubject(subjectDto);
        dto.setAuthorsNames(List.of("J.R.R. Tolkien"));

        Page<BookSummaryDto> page = new PageImpl<>(Collections.singletonList(dto));
        when(bookService.searchBooks(any(), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mvc.perform(get("/api/catalog/search")
                .param("author", "Tolkien")
                .param("subject", "Fantasy")
                .param("year", "1937"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("The Hobbit"))
                .andExpect(jsonPath("$.content[0].authorsNames[0]").value("J.R.R. Tolkien"))
                .andExpect(jsonPath("$.content[0].subject.text").value("Fantasy"));
    }

    @Test
    void searchBooks_emptyResults_returnsEmptyPage() throws Exception {
        // Arrange
        Page<BookSummaryDto> emptyPage = new PageImpl<>(Collections.emptyList());
        when(bookService.searchBooks(any(), any(Pageable.class))).thenReturn(emptyPage);

        // Act & Assert
        mvc.perform(get("/api/catalog/search")
                .param("author", "NonExistentAuthor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getBookById_withValidId_returnsBookDetails() throws Exception {
        // Arrange
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(1L);
        subjectDto.setText("Fantasy");

        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(1L);
        authorDto.setFirstName("J.R.R.");
        authorDto.setLastName("Tolkien");

        BookDetailDto dto = new BookDetailDto();
        dto.setId(5L);
        dto.setTitle("The Hobbit");
        dto.setDesc_txt("A fantasy adventure");
        dto.setCover("https://example.com/hobbit.jpg");
        dto.setSubject(subjectDto);
        dto.setAuthors(List.of(authorDto));
        dto.setSerie_id(14);
        dto.setSerie_pos(1);
        dto.setCreated_at(LocalDateTime.of(1937, 9, 21, 0, 0));
        dto.setUpdated_at(LocalDateTime.now());

        when(bookService.findBookById(5L)).thenReturn(dto);

        // Act & Assert
        mvc.perform(get("/api/catalog/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("The Hobbit"))
                .andExpect(jsonPath("$.desc_txt").value("A fantasy adventure"))
                .andExpect(jsonPath("$.cover").value("https://example.com/hobbit.jpg"))
                .andExpect(jsonPath("$.subject.text").value("Fantasy"))
                .andExpect(jsonPath("$.authors[0].firstName").value("J.R.R."))
                .andExpect(jsonPath("$.authors[0].lastName").value("Tolkien"))
                .andExpect(jsonPath("$.serie_id").value("LOTR"))
                .andExpect(jsonPath("$.serie_pos").value("1"));
    }

    @Test
    void getBookById_withInvalidId_returns404() throws Exception {
        // Arrange
        when(bookService.findBookById(99999L))
                .thenThrow(new prj.books_teamB_spring.exception.BookNotFoundException(99999L));

        // Act & Assert
        mvc.perform(get("/api/catalog/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchBooks_withDefaultPagination_usesDefaultSize() throws Exception {
        // Arrange
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(1L);
        subjectDto.setText("Fantasy");

        BookSummaryDto dto = new BookSummaryDto();
        dto.setId(1L);
        dto.setTitle("Test Book");
        dto.setCover("https://example.com/test.jpg");
        dto.setSubject(subjectDto);
        dto.setAuthorsNames(List.of("Test Author"));

        Page<BookSummaryDto> page = new PageImpl<>(
            Collections.singletonList(dto), 
            PageRequest.of(0, 20), // Taille par défaut = 20
            1
        );
        when(bookService.searchBooks(any(), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mvc.perform(get("/api/catalog/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(20)); // Vérifie la taille par défaut
    }
}