package prj.books_teamB_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour exposer les avis publics sur un livre
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private UserBookDTO userBookDTO;
    private Integer rating;
    private String personalNote;

}