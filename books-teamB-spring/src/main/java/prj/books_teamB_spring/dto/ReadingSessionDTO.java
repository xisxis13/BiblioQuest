package prj.books_teamB_spring.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReadingSessionDTO {
    private Long id;
    private UserBookDTO userBookDTO;
    private Integer duration;
    private LocalDateTime sessionDate;
    private String sessionNotes;
    private Integer startPage;
    private Integer endPage;
}
