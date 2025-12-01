package prj.books_teamB_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.service.FollowService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follow")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class FollowController {
    
    @Autowired
    private FollowService followService;
    
    /**
     * Suivre un utilisateur
     */
    @PostMapping("/{followedId}")
    public ResponseEntity<Map<String, Object>> followUser(
            @PathVariable Long followedId,
            @RequestParam Long followerId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = followService.followUser(followerId, followedId);
            if (success) {
                response.put("success", true);
                response.put("message", "Utilisateur suivi avec succès");
                response.put("isFollowing", true);
            } else {
                response.put("success", false);
                response.put("message", "Vous suivez déjà cet utilisateur");
                response.put("isFollowing", true);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("isFollowing", false);
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Ne plus suivre un utilisateur
     */
    @DeleteMapping("/{followedId}")
    public ResponseEntity<Map<String, Object>> unfollowUser(
            @PathVariable Long followedId,
            @RequestParam Long followerId) {
        
        Map<String, Object> response = new HashMap<>();
        
        boolean success = followService.unfollowUser(followerId, followedId);
        if (success) {
            response.put("success", true);
            response.put("message", "Vous ne suivez plus cet utilisateur");
            response.put("isFollowing", false);
        } else {
            response.put("success", false);
            response.put("message", "Vous ne suiviez pas cet utilisateur");
            response.put("isFollowing", false);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Vérifier si un utilisateur suit un autre
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getFollowStatus(
            @RequestParam Long followerId,
            @RequestParam Long followedId) {
        
        Map<String, Object> response = new HashMap<>();
        boolean isFollowing = followService.isFollowing(followerId, followedId);
        
        response.put("isFollowing", isFollowing);
        response.put("followersCount", followService.countFollowers(followedId));
        response.put("followingCount", followService.countFollowing(followerId));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Récupérer les abonnés d'un utilisateur
     */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<User>> getFollowers(@PathVariable Long userId) {
        List<User> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }
    
    /**
     * Récupérer les utilisateurs suivis par un utilisateur
     */
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<User>> getFollowing(@PathVariable Long userId) {
        List<User> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }
}
