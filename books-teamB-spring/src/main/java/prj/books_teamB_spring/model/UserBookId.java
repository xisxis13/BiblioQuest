package prj.books_teamB_spring.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;


@Embeddable
@AllArgsConstructor
@Data
public class UserBookId implements Serializable {

    private Long userId;
    private Long bookId;

    public UserBookId() {}
}