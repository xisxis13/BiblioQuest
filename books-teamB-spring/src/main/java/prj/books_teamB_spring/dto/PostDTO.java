package prj.books_teamB_spring.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostDTO {
    private Long userId;
    private String username;
    private String content;
    private Long bookId;
    private LocalDateTime createdAt;
}