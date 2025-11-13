package prj.books_teamB_spring.dto;

import lombok.Data;

@Data
public class ReactionDTO {
    private Long sessionId;
    private String comment;
    private String emoji;
}
