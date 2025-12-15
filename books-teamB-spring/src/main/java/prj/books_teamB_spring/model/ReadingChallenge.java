package prj.books_teamB_spring.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;


@Entity
@Data
public class ReadingChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;
    private String name; 
    private Integer goal; 
    private LocalDate startDate;
    private LocalDate endDate;

    //annee en cours, si on specifie pas periode
    public void setDefaultDates() {
        int currentYear = LocalDate.now().getYear();
        this.startDate = LocalDate.of(currentYear, 1, 1);
        this.endDate = LocalDate.of(currentYear, 12, 31);
    }
}
