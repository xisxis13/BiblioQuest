package prj.books_teamB_spring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;

@Entity
@Data
@Table(name = "book_subject")
public class BookSubject {

    @EmbeddedId
    private BookSubjectId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "book", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subjectId")
    @JoinColumn(name = "subject", nullable = false)
    private Subject subject;

    public BookSubject() {}

    public BookSubject(Book book, Subject subject) {
        this.book = book;
        this.subject = subject;
        this.id = new BookSubjectId(book.getId(), subject.getId());
    }
}
