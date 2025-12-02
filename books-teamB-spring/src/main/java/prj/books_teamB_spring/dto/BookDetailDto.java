package prj.books_teamB_spring.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookDetailDto {
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
}