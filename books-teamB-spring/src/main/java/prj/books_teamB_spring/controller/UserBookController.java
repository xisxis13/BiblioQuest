package prj.books_teamB_spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.ReadingStatus;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.service.BookService;
import prj.books_teamB_spring.service.UserBookService;
import prj.books_teamB_spring.dto.BookDTO;
import prj.books_teamB_spring.dto.UserBookDTO;

@RestController
@RequestMapping("/api/user/books")
public class UserBookController {

    private final UserBookService userBookService;
    private final BookService bookService;

    public UserBookController(UserBookService userBookService, BookService bookService) {
        this.userBookService = userBookService;
        this.bookService = bookService;
    }

    /**
     * Récupère tous les livres d'un user.
     */
    @GetMapping
    public List<BookDTO> getAllBooks(@RequestParam Long userId) {
        List<BookDTO> bookDTOs = new ArrayList<>();
        List<UserBook> userBooks = userBookService.getAllBooks(userId);

        for (UserBook userBook: userBooks){
            Long bookId = userBook.getId().getBookId();
            Book book = this.bookService.findById(bookId);
            BookDTO bookDTO = new BookDTO(book);
            bookDTO.setReadingStatusId(userBook.getStatus().getId());
            bookDTOs.add(bookDTO);
        }
        return bookDTOs;
    }

    /**
     * Ajoute un livre pour un user.
     */
    @PostMapping("")
    public ResponseEntity<UserBook> addBook(@RequestBody UserBookDTO userBookDto) {
        try{
            UserBook created = userBookService.addBook(userBookDto);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch(Exception e){
            System.out.println("error on userBookAdd " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.CREATED);
        }
        
    }

    /**
     * Supprime un livre pour un user.
     */
    @DeleteMapping("/id")
    public ResponseEntity<Void> deleteBook(@RequestBody UserBookDTO userBookDto) {
        userBookService.deleteBook(userBookDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Met à jour le readingStatus d'un livre pour un user.
     */
    @PostMapping("/update")
    public ResponseEntity<UserBook> updateReadingStatus(@RequestBody UserBookDTO userBookDto,
                                                        @RequestParam Integer readingStatusId) {
        UserBook updated = userBookService.updateReadingStatus(userBookDto, ReadingStatus.fromId(readingStatusId));
        return ResponseEntity.ok(updated);
    }
}
