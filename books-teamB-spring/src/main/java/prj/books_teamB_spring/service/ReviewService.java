package prj.books_teamB_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prj.books_teamB_spring.dto.ReviewDto;
import prj.books_teamB_spring.dto.UserBookDTO;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.model.UserBook;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.UserBookRepository;
import prj.books_teamB_spring.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

	private final UserBookRepository userBookRepository;
	private final BookRepository bookRepository;
	private final UserRepository userRepository;

	public List<ReviewDto> getBookReviews(Long bookId) {
		List<UserBook> userBooks = userBookRepository.findByBookId(bookId);
		List<ReviewDto> reviews = new ArrayList<>();

		for (UserBook userBook : userBooks) {
			UserBookDTO userBookDTO = new UserBookDTO(
					userBook.getId().getUserId(),
					userBook.getId().getBookId());
			User user = userRepository.findById(userBook.getId().getUserId()).orElseThrow();
			ReviewDto review = new ReviewDto(
					userBookDTO,
					user.getUsername(),
					userBook.getRating(),
					userBook.getReview(),
					userBook.getReviewCreatedAt());
			reviews.add(review);
		}
		return reviews;
	}

	public void addOrUpdateReview(ReviewDto review) {
		UserBookDTO userBookDTO = review.getUserBookDTO();

		Optional<UserBook> userBook = userBookRepository.findByUserAndBook(
				userBookDTO.getUserId(),
				userBookDTO.getBookId());

		if (userBook.isEmpty()) {
			userBookRepository.save(new UserBook(
					userBookDTO.getUserId(),
					userBookDTO.getBookId(),
					review.getComment(),
					review.getRating()));
		} else {
			UserBook ub = userBook.get();
			ub.setRating(review.getRating());
			ub.setReview(review.getComment());
			userBookRepository.save(ub);
		}
	}

	public void deleteReview(UserBookDTO userBookDTO) {
		UserBook userBook = userBookRepository.findByUserAndBook(
				userBookDTO.getUserId(),
				userBookDTO.getBookId()).orElseThrow();

		userBook.setRating(null);
		userBook.setReview(null);
		userBookRepository.save(userBook);
	}
}