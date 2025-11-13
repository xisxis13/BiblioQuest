package prj.books_teamB_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.repository.BookRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public List<Book> searchBooks(String query) {
        return bookRepository.findByTitleContainingIgnoreCase(query);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Livre introuvable"));
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }
}
