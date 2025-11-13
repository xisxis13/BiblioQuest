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
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return userBookRepository.findByUser(user);
    }

    public UserBook addBookToLibrary(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Livre introuvable"));
        UserBook userBook = new UserBook(user, book, ReadingStatus.TO_READ);
        return userBookRepository.save(userBook);
    }

    public UserBook updateProgress(Long userId, Long bookId, int progress) {
        UserBook userBook = userBookRepository.findByUser(userRepository.findById(userId).orElseThrow())
                .stream().filter(ub -> ub.getBook().getId().equals(bookId))
                .findFirst().orElseThrow(() -> new RuntimeException("Livre non-trouvé dans la bibliothèque"));
        userBook.setProgress(progress);
        return userBookRepository.save(userBook);
    }

    public void deleteUserBook(Long userId, Long bookId) {
        UserBook userBook = userBookRepository.findByUser(userRepository.findById(userId).orElseThrow())
                .stream().filter(ub -> ub.getBook().getId().equals(bookId))
                .findFirst().orElseThrow(() -> new RuntimeException("Livre non-trouvé"));
        userBookRepository.delete(userBook);
    }
}
