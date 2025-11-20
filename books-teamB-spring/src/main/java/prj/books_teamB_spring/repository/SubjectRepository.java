package prj.books_teamB_spring.repository;

import org.springframework.data.repository.CrudRepository;
import prj.books_teamB_spring.model.Subject;

public interface SubjectRepository extends CrudRepository<Subject, Long> {
    
}