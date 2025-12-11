package prj.books_teamB_spring.mapper;

import prj.books_teamB_spring.dto.AuthorDto;
import prj.books_teamB_spring.dto.BookDto;
import prj.books_teamB_spring.dto.BookSummaryDto;
import prj.books_teamB_spring.dto.SubjectDto;
import prj.books_teamB_spring.model.Author;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Subject;

import java.util.List;
import java.util.stream.Collectors;

public class BookMapper {

    /**
     * Convertit un Book en BookSummaryDto (pour la liste/catalogue)
     */
    public static BookSummaryDto toSummaryDto(Book book) {
        if (book == null) {
            return null;
        }

        BookSummaryDto dto = new BookSummaryDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setCover(book.getCover());

        if (book.getSubject() != null) {
            dto.setSubject(toSubjectDto(book.getSubject()));
        }

        List<String> authorNames = book.getAuthors().stream()
            .map(author -> author.getFirstName() + " " + author.getLastName())
            .collect(Collectors.toList());
        dto.setAuthorsNames(authorNames);

        return dto;
    }

    /**
     * Convertit un Book en BookDetailDto (pour la page de détail)
     */
    public static BookDto toDetailDto(Book book) {
        if (book == null) {
            return null;
        }

        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setDesc_txt(book.getDesc_txt());
        dto.setCover(book.getCover());
        dto.setSerie_id(book.getSerie_id());
        dto.setSerie_pos(book.getSerie_pos());
        dto.setCreated_at(book.getCreated_at());
        dto.setUpdated_at(book.getUpdated_at());

        if (book.getSubject() != null) {
            dto.setSubject(toSubjectDto(book.getSubject()));
        }

        List<AuthorDto> authors = book.getAuthors().stream()
            .map(BookMapper::toAuthorDto)
            .collect(Collectors.toList());
        dto.setAuthors(authors);

        return dto;
    }

    /**
     * Convertit un Subject en SubjectDto
     */
    public static SubjectDto toSubjectDto(Subject subject) {
        if (subject == null) {
            return null;
        }

        SubjectDto dto = new SubjectDto();
        dto.setId(subject.getId());
        dto.setText(subject.getText());
        return dto;
    }

    /**
     * Convertit un Author en AuthorDto
     */
    public static AuthorDto toAuthorDto(Author author) {
        if (author == null) {
            return null;
        }

        AuthorDto dto = new AuthorDto();
        dto.setId(author.getId());
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        return dto;
    }
}
