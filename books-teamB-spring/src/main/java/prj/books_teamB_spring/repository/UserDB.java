package prj.books_teamB_spring.repository;

import org.springframework.data.repository.CrudRepository;
import prj.books_teamB_spring.model.User;

public interface UserDB extends CrudRepository<User, Long> {
    
}
