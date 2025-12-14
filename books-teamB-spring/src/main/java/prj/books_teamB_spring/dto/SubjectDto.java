package prj.books_teamB_spring.dto;

import lombok.Data;
import prj.books_teamB_spring.model.Subject;

@Data
public class SubjectDto {
    private Long id;
    private String label;

    public SubjectDto(Subject subject){
        this.id = subject.getId();
        this.label = subject.getLabel();
    }
}
