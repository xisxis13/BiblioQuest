package prj.books_teamB_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prj.books_teamB_spring.model.ReadingChallenge;

import java.util.List;

@Repository
public interface ReadingChallengeRepository extends JpaRepository<ReadingChallenge, Long> {
    //defis d utilisateur
    List<ReadingChallenge> findByUserId(Long userId);
}