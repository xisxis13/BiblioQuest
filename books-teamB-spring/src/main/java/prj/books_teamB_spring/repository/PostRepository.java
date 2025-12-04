package prj.books_teamB_spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import prj.books_teamB_spring.model.Post;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    Optional<Post> findById(Long id);
    List<Post> findAll();
    
}
