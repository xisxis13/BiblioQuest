package prj.books_teamB_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prj.books_teamB_spring.model.*;
import prj.books_teamB_spring.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LibraryService {

    private final UserBookRepository userBookRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public List<UserBook> getUserLibrary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userBookRepository.findByUser(user);
    }

    public UserBook addBookToLibrary(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        UserBook userBook = new UserBook(user, book);
        return userBookRepository.save(userBook);
    }

    public UserBook updateProgress(Long userId, Long bookId, int progress) {
        UserBook userBook = userBookRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new RuntimeException("Book not found in library"));
        userBook.setProgress(progress);
        return userBookRepository.save(userBook);
    }

    public UserBook updateUserBook(Long userId, Long bookId, ReadingStatus status, Integer progress, Integer rating, String personalNote) {
        UserBook userBook = userBookRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new RuntimeException("Book not found in library"));
        
        if (status != null) userBook.setStatus(status);
        if (progress != null) userBook.setProgress(progress);
        if (rating != null) userBook.setRating(rating);
        if (personalNote != null) userBook.setPersonalNote(personalNote);
        
        return userBookRepository.save(userBook);
    }

    public void deleteUserBook(Long userId, Long bookId) {
        UserBook userBook = userBookRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        userBookRepository.delete(userBook);
    }

    public boolean existsInLibrary(Long userId, Long bookId) {
        return userBookRepository.existsByUserIdAndBookId(userId, bookId);
    }
}