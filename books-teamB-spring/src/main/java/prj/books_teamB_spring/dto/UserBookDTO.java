package prj.books_teamB_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBookDTO {
    private Long userId;
    private Long bookId;
}
