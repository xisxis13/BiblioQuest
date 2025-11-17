package prj.books_teamB_spring.dto;

import java.util.Set;

import lombok.Data;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private String descTxt;
    private String cover;
    private String isbn;
    private Integer totalPages;
    private Set<String> authors;
}
