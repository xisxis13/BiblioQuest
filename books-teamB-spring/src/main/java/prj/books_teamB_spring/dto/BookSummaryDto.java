package prj.books_teamB_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import prj.books_teamB_spring.model.Subject;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSummaryDto {
    private Long id;
    private String title;
    private String cover;       // URL de l'image
    private SubjectDto subject;       // Juste le nom du sujet (ex: "Fantasy")
    private List<String> authorsNames; // Liste simple des noms ("Tolkien", "Herbert")
}