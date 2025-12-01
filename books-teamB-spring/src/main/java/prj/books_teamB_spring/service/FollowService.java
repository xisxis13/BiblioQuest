package prj.books_teamB_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import prj.books_teamB_spring.model.Follow;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.FollowRepository;
import prj.books_teamB_spring.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FollowService {
    
    @Autowired
    private FollowRepository followRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Suivre un utilisateur
     */
    public boolean followUser(Long followerId, Long followedId) {
        // Vérifier que l'utilisateur ne se suit pas lui-même
        if (followerId.equals(followedId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un utilisateur ne peut pas se suivre lui-même");
        }
        
        // Vérifier que les utilisateurs existent
        User follower = userRepository.findById(followerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur suiveur non trouvé"));
        User followed = userRepository.findById(followedId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur à suivre non trouvé"));
        
        // Vérifier si l'abonnement existe déjà
        Optional<Follow> existingFollow = followRepository.findActiveFollow(followerId, followedId);
        if (existingFollow.isPresent()) {
            return false; // Déjà suivi
        }
        
        // Créer le nouvel abonnement
        Follow follow = new Follow(follower, followed);
        followRepository.save(follow);
        return true;
    }
    
    /**
     * Ne plus suivre un utilisateur
     */
    public boolean unfollowUser(Long followerId, Long followedId) {
        Optional<Follow> follow = followRepository.findActiveFollow(followerId, followedId);
        if (follow.isPresent()) {
            Follow followEntity = follow.get();
            followEntity.setIsActive(false);
            followRepository.save(followEntity);
            return true;
        }
        return false; // Pas d'abonnement trouvé
    }
    
    /**
     * Vérifier si un utilisateur suit un autre
     */
    public boolean isFollowing(Long followerId, Long followedId) {
        return followRepository.findActiveFollow(followerId, followedId).isPresent();
    }
    
    /**
     * Récupérer les abonnés d'un utilisateur
     */
    public List<User> getFollowers(Long userId) {
        return followRepository.findFollowersByUserId(userId);
    }
    
    /**
     * Récupérer les utilisateurs suivis par un utilisateur
     */
    public List<User> getFollowing(Long userId) {
        return followRepository.findFollowingByUserId(userId);
    }
    
    /**
     * Compter les abonnés d'un utilisateur
     */
    public Long countFollowers(Long userId) {
        return followRepository.countFollowersByUserId(userId);
    }
    
    /**
     * Compter les utilisateurs suivis par un utilisateur
     */
    public Long countFollowing(Long userId) {
        return followRepository.countFollowingByUserId(userId);
    }
}
