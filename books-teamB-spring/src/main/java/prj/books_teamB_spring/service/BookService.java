package prj.books_teamB_spring.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import prj.books_teamB_spring.dto.BookDTO;
import prj.books_teamB_spring.dto.AuthorDto;
import prj.books_teamB_spring.exception.BookNotFoundException;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Author;
import prj.books_teamB_spring.model.Subject;
import prj.books_teamB_spring.repository.AuthorRepository;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.SubjectRepository;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final SubjectRepository subjectRepository;

    public BookService(BookRepository bookRepository,
            AuthorRepository authorRepository,
            SubjectRepository subjectRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.subjectRepository = subjectRepository;
    }

    public Book findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return book;
    }

    public Iterable<Book> findAll() {
        return this.bookRepository.findAll();
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }

    public Book addAuthor(Long bookId, AuthorDto authorDto) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        boolean hasFirstName = authorDto.getFirstName() != null && !authorDto.getFirstName().isBlank();
        boolean hasLastName = authorDto.getLastName() != null && !authorDto.getLastName().isBlank();

        if (!hasFirstName && !hasLastName) {
            throw new IllegalArgumentException("Author must have at least first name or last name");
        }

        Author author;
        if (authorDto.getId() != null) {
            author = authorRepository.findById(authorDto.getId())
                    .orElseGet(Author::new);
        } else {
            author = new Author();
        }

        author.setFirstName(authorDto.getFirstName());
        author.setLastName(authorDto.getLastName());
        author = authorRepository.save(author);

        book.getAuthors().add(author);
        Book saved = bookRepository.save(book);
        return saved;
    }

    public Book deleteAuthor(Long bookId, Long authorId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));

        book.getAuthors().remove(author);
        Book saved = bookRepository.save(book);
        return saved;
    }

    public Book addBook(BookDTO newBookDTO) {
        Book book = new Book();
        book.setTitle(newBookDTO.getTitle());
        book.setDesc_txt(newBookDTO.getDesc_txt());
        book.setCover(newBookDTO.getCover());
        book.setSerie_id(newBookDTO.getSerie_id());
        book.setSerie_pos(newBookDTO.getSerie_pos());
        book.setTotal_pages(newBookDTO.getTotalPages());
        book.setIsbn(newBookDTO.getIsbn());
        book.setCreated_at(LocalDateTime.now());
        book.setUpdated_at(LocalDateTime.now());

        if (newBookDTO.getAuthors() != null) {
            for (AuthorDto authorDto : newBookDTO.getAuthors()) {
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
        return saved;
    }

    public List<Subject> getSubjects() {
        Iterable<Subject> subjecIterable = this.subjectRepository.findAll();
        List<Subject> subjects = new ArrayList<>();

        for (Subject subject : subjecIterable) {
            subjects.add(subject);
        }
        return subjects;
    }

    public BookDTO update(BookDTO bookDTO){
        Book book = this.findById(bookDTO.getId());
        book.setTitle(bookDTO.getTitle());
        book.setDesc_txt(bookDTO.getDesc_txt());
        book.setCover(bookDTO.getCover());
        book.setSerie_id(bookDTO.getSerie_id());
        book.setSerie_pos(bookDTO.getSerie_pos());
        book.setIsbn(bookDTO.getIsbn());
        book.setTotal_pages(bookDTO.getTotalPages());

        bookRepository.save(book);
        return bookDTO;
    }
}
