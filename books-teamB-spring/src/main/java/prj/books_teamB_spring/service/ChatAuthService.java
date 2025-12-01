package prj.books_teamB_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.UserRepository;

@Service
public class ChatAuthService {

    @Autowired
    private UserRepository userRepository;

    // Obtenir l'utilisateur connecté depuis le contexte de sécurité
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    // Obtenir l'ID de l'utilisateur connecté
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    // Vérifier si l'utilisateur est authentifié
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !authentication.getName().equals("anonymousUser");
    }
}
