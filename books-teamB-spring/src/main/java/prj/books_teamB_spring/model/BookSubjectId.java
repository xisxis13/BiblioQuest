package prj.books_teamB_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@Data
public class BookSubjectId implements Serializable {

    @Column(name = "book")
    private Long bookId;

    @Column(name = "subject")
    private Long subjectId;

    public BookSubjectId() {}
}

