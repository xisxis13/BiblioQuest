package prj.books_teamB_spring.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.ReadingStatus;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.UserBookRepository;
import prj.books_teamB_spring.repository.UserRepository;
import prj.books_teamB_spring.dto.UserBookDTO;

/**
 * Service pour gérer les collections de livres des utilisateurs.
 * Responsabilités : ajouter/supprimer des livres, lister les livres possédés,
 * mettre à jour le statut de lecture.
 */
@Service
@Transactional
public class UserBookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final UserBookRepository userBookRepository;

    public UserBookService(BookRepository bookRepository,
                           UserRepository userRepository,
                           UserBookRepository userBookRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.userBookRepository = userBookRepository;
    }

    /**
     * Récupère tous les UserBook d'un utilisateur.
     *
     * @param userId id de l'utilisateur
     * @return liste des UserBook de cet utilisateur
     */
    public List<UserBook> getAllBooks(Long userId) {
        return userBookRepository.findByUserId(userId);
    }

    /**
     * Ajoute un livre à la collection d'un utilisateur.
     * Par défaut, le statut de lecture est TO_READ (géré par le constructeur UserBook).
     *
     * @param userBookDto DTO contenant userId et bookId
     * @return le UserBook persistant
     */
    public UserBook addBook(UserBookDTO userBookDto) {
        User user = userRepository.findById(userBookDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Utilisateur introuvable avec l'id " + userBookDto.getUserId()
                ));

        Book book = bookRepository.findById(userBookDto.getBookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Livre introuvable avec l'id " + userBookDto.getBookId()
                ));
            

        UserBook userBook = new UserBook(user, book);
        return userBookRepository.save(userBook);
    }

    /**
     * Supprime un livre de la collection d'un utilisateur.
     *
     * @param userBookDto DTO contenant userId et bookId
     */
    public void deleteBook(UserBookDTO userBookDto) {
        User user = userRepository.findById(userBookDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Utilisateur introuvable avec l'id " + userBookDto.getUserId()
                ));

        Book book = bookRepository.findById(userBookDto.getBookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Livre introuvable avec l'id " + userBookDto.getBookId()
                ));

        UserBook userBook = userBookRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ce livre n'est pas présent dans la collection de cet utilisateur"
                ));

        userBookRepository.delete(userBook);
    }

    /**
     * Met à jour le statut de lecture d'un livre pour un utilisateur.
     *
     * @param userBookDto   DTO contenant userId et bookId
     * @param readingStatus nouveau statut de lecture
     * @return le UserBook mis à jour
     */
    public UserBook updateReadingStatus(UserBookDTO userBookDto, ReadingStatus readingStatus) {
        User user = userRepository.findById(userBookDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Utilisateur introuvable avec l'id " + userBookDto.getUserId()
                ));

        Book book = bookRepository.findById(userBookDto.getBookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Livre introuvable avec l'id " + userBookDto.getBookId()
                ));

        UserBook userBook = userBookRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ce livre n'est pas présent dans la collection de cet utilisateur"
                ));

        userBook.setStatus(readingStatus);
        return userBookRepository.save(userBook);
    }
}
