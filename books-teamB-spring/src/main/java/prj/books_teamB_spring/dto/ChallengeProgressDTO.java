package prj.books_teamB_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import prj.books_teamB_spring.model.UserBook;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeProgressDTO {
    private Long id;
    private String name;
    private Integer goal;
    private Integer readCount; 
    private LocalDate startDate;
    private LocalDate endDate;
    
    private List<UserBook> books; 

    public ChallengeProgressDTO(Long id, String name, Integer goal, LocalDate start, LocalDate end, Integer readCount, List<UserBook> books) {
        this.id = id;
        this.name = name;
        this.goal = goal;
        this.startDate = start;
        this.endDate = end;
        this.readCount = readCount;
        this.books = books;
    }
}