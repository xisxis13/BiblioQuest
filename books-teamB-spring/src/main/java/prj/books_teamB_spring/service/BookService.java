package prj.books_teamB_spring.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import prj.books_teamB_spring.dto.BookDetailDto;
import prj.books_teamB_spring.dto.BookSearchFilters;
import prj.books_teamB_spring.dto.BookSummaryDto;
import prj.books_teamB_spring.dto.AuthorDto;
import prj.books_teamB_spring.dto.SubjectDto;
import prj.books_teamB_spring.exception.BookNotFoundException;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.UserRepository;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    private User requireUser(String username) {
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing X-Username header");
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
    }

    public Book addBookForUser(String username, Book book) {
        User user = requireUser(username);

        book.setId(null);
        Book saved = bookRepository.save(book);

        user.addBook(saved);

        userRepository.save(user);
        return saved;
    }

    public List<Book> listBooksForUser(String username) {
        User user = requireUser(username);
        return user.getMyBooks().stream().collect(Collectors.toList());
    }

    public Book getBookForUser(String username, Long id) {
        User user = requireUser(username);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if (!user.hasBook(book)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this book");
        }
        return book;
    }

    public void deleteBookForUser(String username, Long id) {
        User user = requireUser(username);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if (!user.hasBook(book)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this book");
        }

        user.removeBook(book);

        userRepository.save(user);
    }

    public Page<BookSummaryDto> searchBooks(BookSearchFilters filters, Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(this::convertToSummaryDto);
    }

    public BookDetailDto findBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return convertToDetailDto(book);
    }

    private BookSummaryDto convertToSummaryDto(Book book) {
        BookSummaryDto dto = new BookSummaryDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setCover(book.getCover());

        if (book.getSubject() != null) {
            SubjectDto subjectDto = new SubjectDto();
            subjectDto.setId(book.getSubject().getId());
            subjectDto.setText(book.getSubject().getText());
            dto.setSubject(subjectDto);
        }

        List<String> authorNames = book.getAuthors().stream()
                .map(author -> author.getFirstName() + " " + author.getLastName())
                .collect(Collectors.toList());
        dto.setAuthorsNames(authorNames);
        return dto;
    }

    private BookDetailDto convertToDetailDto(Book book) {
        BookDetailDto dto = new BookDetailDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setDesc_txt(book.getDesc_txt());
        dto.setCover(book.getCover());
        dto.setSerie_id(book.getSerie_id());
        dto.setSerie_pos(book.getSerie_pos());
        dto.setCreated_at(book.getCreated_at());
        dto.setUpdated_at(book.getUpdated_at());

        if (book.getSubject() != null) {
            SubjectDto subjectDto = new SubjectDto();
            subjectDto.setId(book.getSubject().getId());
            subjectDto.setText(book.getSubject().getText());
            dto.setSubject(subjectDto);
        }

        List<AuthorDto> authorDtos = book.getAuthors().stream()
                .map(author -> {
                    AuthorDto authorDto = new AuthorDto();
                    authorDto.setId(author.getId());
                    authorDto.setFirstName(author.getFirstName());
                    authorDto.setLastName(author.getLastName());
                    return authorDto;
                })
                .collect(Collectors.toList());
        dto.setAuthors(authorDtos);
        return dto;
    }
}