package prj.books_teamB_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prj.books_teamB_spring.model.Follow;
import prj.books_teamB_spring.model.User;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    
    /**
     * Vérifier si un utilisateur suit un autre
     */
    @Query("SELECT f FROM Follow f WHERE f.follower.id = :followerId AND f.followed.id = :followedId AND f.isActive = true")
    Optional<Follow> findActiveFollow(@Param("followerId") Long followerId, @Param("followedId") Long followedId);
    
    /**
     * Récupérer tous les abonnés d'un utilisateur
     */
    @Query("SELECT f.follower FROM Follow f WHERE f.followed.id = :userId AND f.isActive = true")
    List<User> findFollowersByUserId(@Param("userId") Long userId);
    
    /**
     * Récupérer tous les utilisateurs suivis par un utilisateur
     */
    @Query("SELECT f.followed FROM Follow f WHERE f.follower.id = :userId AND f.isActive = true")
    List<User> findFollowingByUserId(@Param("userId") Long userId);
    
    /**
     * Compter le nombre d'abonnés d'un utilisateur
     */
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followed.id = :userId AND f.isActive = true")
    Long countFollowersByUserId(@Param("userId") Long userId);
    
    /**
     * Compter le nombre d'utilisateurs suivis par un utilisateur
     */
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId AND f.isActive = true")
    Long countFollowingByUserId(@Param("userId") Long userId);
}
