package prj.books_teamB_spring.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReadingChallengeDto {
    private String name; // nom challenge
    private int goal; 
    private LocalDate startDate; 
    private LocalDate endDate;   
}