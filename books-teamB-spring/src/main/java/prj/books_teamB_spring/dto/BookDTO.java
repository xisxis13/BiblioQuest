package prj.books_teamB_spring.dto;

import lombok.Data;
import prj.books_teamB_spring.model.Author;
import prj.books_teamB_spring.model.Book;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private String cover;
    private String desc_txt;  // Correspond à Book.desc_txt

    private SubjectDto subject;  // DTO du sujet, pas javax.security.auth.Subject

    private Integer serie_id;
    private Integer serie_pos;

    private List<AuthorDto> authors;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    private Integer total_pages;
    private String isbn;

    public BookDTO(Book book){
        this.id = book.getId();
        this.title = book.getTitle();
        this.cover = book.getCover();
        this.desc_txt = book.getDesc_txt();

        List<AuthorDto> authors= new ArrayList<>();
        Set<Author> bookAuthors = book.getAuthors();

        for(Author bookAuthor: bookAuthors){
            AuthorDto author = new AuthorDto(bookAuthor.getId(), bookAuthor.getFirstName(), bookAuthor.getLastName());
            authors.add(author);
        }
        
        this.authors = authors;
        this.created_at = book.getCreated_at();
        this.updated_at = book.getUpdated_at();
        this.total_pages = book.getTotal_pages();
        this.isbn = book.getIsbn();
    }
}