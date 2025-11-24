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
        // Ignore any client-provided id: ensure a new entity is created
        book.setId(0L);
        Book saved = bookRepository.save(book);
        user.getMyBooks().add(saved);
        userRepository.save(user);
        return saved;
    }

    public Book updateBookForUser(String username, Long id, Book dto) {
        User user = requireUser(username);

        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if (!user.getMyBooks().contains(existing)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this book");
        }

        // 4. Mettre à jour les champs simples
        existing.setTitle(dto.getTitle());
        existing.setDesc_txt(dto.getDesc_txt());
        existing.setCover(dto.getCover());
        existing.setSerie_id(dto.getSerie_id());
        existing.setSerie_pos(dto.getSerie_pos());

        // Mettre à jour le sujet
        if (dto.getSubject() != null) {
            existing.setSubject(dto.getSubject());
        } else {
            existing.setSubject(null);
        }

        // Mettre à jour les auteurs
        if (dto.getAuthors() != null) {
            existing.getAuthors().clear();
            existing.getAuthors().addAll(dto.getAuthors());
        }

        return bookRepository.save(existing);
    }


    public List<Book> listBooksForUser(String username) {
        User user = requireUser(username);
        Set<Book> books = user.getMyBooks();
        return books.stream().collect(Collectors.toList());
    }

    public Book getBookForUser(String username, Long id) {
        User user = requireUser(username);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        if (!user.getMyBooks().contains(book)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this book");
        }
        return book;
    }

    public void deleteBookForUser(String username, Long id) {
        User user = requireUser(username);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        if (!user.getMyBooks().contains(book)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this book");
        }
        user.getMyBooks().remove(book);
        userRepository.save(user);
    }

    // === MÉTHODES POUR LE CATALOGUE PUBLIC ===

    /**
     * Recherche de livres dans le catalogue public avec filtres et pagination
     */
    public Page<BookSummaryDto> searchBooks(BookSearchFilters filters, Pageable pageable) {
        // Pour l'instant, on retourne tous les livres sans filtrage
        // TODO: Implémenter le filtrage avec Specifications JPA
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(this::convertToSummaryDto);
    }

    /**
     * Récupère les détails d'un livre par son ID
     */
    public BookDetailDto findBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return convertToDetailDto(book);
    }

    // === MÉTHODES DE CONVERSION DTO ===

    private BookSummaryDto convertToSummaryDto(Book book) {
        BookSummaryDto dto = new BookSummaryDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setCover(book.getCover());
        
        // Conversion du subject
        if (book.getSubject() != null) {
            SubjectDto subjectDto = new SubjectDto();
            subjectDto.setId(book.getSubject().getId());
            subjectDto.setText(book.getSubject().getText());
            dto.setSubject(subjectDto);
        }
        
        // Conversion des auteurs en liste de noms
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
        
        // Conversion du subject
        if (book.getSubject() != null) {
            SubjectDto subjectDto = new SubjectDto();
            subjectDto.setId(book.getSubject().getId());
            subjectDto.setText(book.getSubject().getText());
            dto.setSubject(subjectDto);
        }
        
        // Conversion des auteurs
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
