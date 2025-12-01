package prj.books_teamB_spring.service;

import java.time.LocalDateTime;
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
import prj.books_teamB_spring.repository.AuthorRepository;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.SubjectRepository;
import prj.books_teamB_spring.repository.UserRepository;
import prj.books_teamB_spring.model.Author;
import prj.books_teamB_spring.model.Subject;
import prj.books_teamB_spring.repository.AuthorRepository;
import prj.books_teamB_spring.repository.SubjectRepository;


@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final SubjectRepository subjectRepository;

    public BookService(BookRepository bookRepository,
                       UserRepository userRepository,
                       AuthorRepository authorRepository,
                       SubjectRepository subjectRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.subjectRepository = subjectRepository;
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

        /**
     * Met à jour les informations d'un livre du catalogue public
     */
    public BookDetailDto updateBook(Long id, BookDetailDto updatedBookDto) {
        // Récupération du livre existant
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        // === Champs simples ===
        book.setTitle(updatedBookDto.getTitle());
        book.setDesc_txt(updatedBookDto.getDesc_txt());
        book.setCover(updatedBookDto.getCover());
        book.setSerie_id(updatedBookDto.getSerie_id());
        book.setSerie_pos(updatedBookDto.getSerie_pos());

        // === Subject (genre / catégorie) ===
        SubjectDto subjectDto = updatedBookDto.getSubject();
        if (subjectDto != null && subjectDto.getText() != null && !subjectDto.getText().isBlank()) {
            Subject subject;

            if (subjectDto.getId() != null) {
                // On essaie de retrouver le subject existant
                subject = subjectRepository.findById(subjectDto.getId())
                        .orElseGet(Subject::new);
            } else {
                subject = new Subject();
            }

            subject.setText(subjectDto.getText());
            subject = subjectRepository.save(subject);
            book.setSubject(subject);
        } else {
            // Si aucun subject fourni → on enlève le sujet
            book.setSubject(null);
        }

        // === Auteurs ===
        // On vide la liste actuelle et on la remplit à partir du DTO
        book.getAuthors().clear();

        List<AuthorDto> authorDtos = updatedBookDto.getAuthors();
        if (authorDtos != null) {
            for (AuthorDto authorDto : authorDtos) {
                boolean hasFirstName = authorDto.getFirstName() != null && !authorDto.getFirstName().isBlank();
                boolean hasLastName = authorDto.getLastName() != null && !authorDto.getLastName().isBlank();

                // On ignore les lignes vides dans le formulaire
                if (!hasFirstName && !hasLastName) {
                    continue;
                }

                Author author;
                if (authorDto.getId() != null) {
                    // On essaie de récupérer l'auteur existant
                    author = authorRepository.findById(authorDto.getId())
                            .orElseGet(Author::new);
                } else {
                    author = new Author();
                }

                author.setFirstName(authorDto.getFirstName());
                author.setLastName(authorDto.getLastName());
                author = authorRepository.save(author);

                book.getAuthors().add(author);
            }
        }

        // Sauvegarde finale du livre
        Book saved = bookRepository.save(book);

        // On renvoie un DTO détaillé, comme pour findBookById
        return convertToDetailDto(saved);
    }

    public BookDetailDto createBook(BookDetailDto newBookDto) {
        Book book = new Book();
        book.setTitle(newBookDto.getTitle());
        book.setDesc_txt(newBookDto.getDesc_txt());
        book.setCover(newBookDto.getCover());
        book.setSerie_id(newBookDto.getSerie_id());
        book.setSerie_pos(newBookDto.getSerie_pos());
        book.setCreated_at(LocalDateTime.now());
        book.setUpdated_at(LocalDateTime.now());

        // === Subject ===
        SubjectDto subjectDto = newBookDto.getSubject();
        if (subjectDto != null && subjectDto.getText() != null && !subjectDto.getText().isBlank()) {
            Subject subject = new Subject();
            subject.setText(subjectDto.getText());
            subject = subjectRepository.save(subject);
            book.setSubject(subject);
        }

        // === Auteurs ===
        if (newBookDto.getAuthors() != null) {
            for (AuthorDto authorDto : newBookDto.getAuthors()) {
                if ((authorDto.getFirstName() == null || authorDto.getFirstName().isBlank()) &&
                    (authorDto.getLastName() == null || authorDto.getLastName().isBlank())) {
                    continue;
                }
                Author author = new Author();
                author.setFirstName(authorDto.getFirstName());
                author.setLastName(authorDto.getLastName());
                author = authorRepository.save(author);
                book.getAuthors().add(author);
            }
        }

        Book saved = bookRepository.save(book);
        return convertToDetailDto(saved);
    }

    // === MÉTHODES DE CONVERSION DTO ===

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
