package prj.books_teamB_spring.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.UserRepository;

/**
 * Service pour la gestion des utilisateurs
 * Responsabilités : authentification, création de compte, gestion du profil
 */
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Récupère un utilisateur par son username et vérifie qu'il existe
     * @param username Le nom d'utilisateur
     * @return L'utilisateur trouvé
     * @throws ResponseStatusException si le username est vide ou l'utilisateur n'existe pas
     */
    public User requireUser(String username) {
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing X-Username header");
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    
    // Ajoutez ici d'autres méthodes de gestion utilisateur si nécessaire :
    // - createUser(User user)
    // - updateProfile(String username, UserUpdateDto dto)
    // - changePassword(String username, String newPassword)
    // etc.
}