package prj.books_teamB_spring.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReadingSessionDTO {
    private Long id;
    private Long userBookId;
    private Integer duration;
    private Integer pagesRead;
    private LocalDateTime sessionDate;
    private String sessionNotes;
    private String mood;
    private Boolean isShareable;
}
