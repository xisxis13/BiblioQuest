package prj.books_teamB_spring.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserBookDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String status;
    private Integer progress;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer rating;
    private String personalNote;
}
