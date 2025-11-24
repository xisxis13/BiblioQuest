package prj.books_teamB_spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import prj.books_teamB_spring.model.Edition;

public interface EditionRepository extends JpaRepository<Edition, String> {

    // Récupère toutes les éditions liées à un livre donné
    List<Edition> findByBook_Id(Long bookId);
}
