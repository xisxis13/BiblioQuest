package prj.books_teamB_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.repository.UserRepository;
import prj.books_teamB_spring.dto.UpdateProfileRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des utilisateurs
 * Responsabilités : authentification, création de compte, gestion du profil
 */
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    
    /**
     * Créer un nouveau utilisateur
     */
    public User createUser(String username, String email, String password) {
        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette adresse email est déjà utilisée");
        }
        
        // Vérifier si le nom d'utilisateur existe déjà
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce nom d'utilisateur est déjà pris");
        }
        
        // Validation des champs
        if (username == null || username.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom d'utilisateur est requis");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'adresse email est requise");
        }
        
        if (password == null || password.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le mot de passe doit contenir au moins 6 caractères");
        }
        
        // Créer nouveau user
        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setEmail(email.trim());
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(Role.USER);
        newUser.setLastActivityAt(LocalDateTime.now());
        
        return userRepository.save(newUser);
    }
    
    /**
     * Authentifier un utilisateur
     */
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Aucun compte associé à cette adresse email"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mot de passe incorrect");
        }
        
        // Mettre à jour la dernière activité
        user.setLastActivityAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * Mettre à jour le profil d'un utilisateur
     */
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            if (!request.getUsername().equals(user.getUsername())) {
                if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce nom d'utilisateur est déjà utilisé");
                }
                user.setUsername(request.getUsername().trim());
            }
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!request.getEmail().equals(user.getEmail())) {
                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette adresse email est déjà utilisée");
                }
                user.setEmail(request.getEmail().trim());
            }
        }

        user.setLastActivityAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * Changer le mot de passe d'un utilisateur
     */
    public User changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mot de passe actuel incorrect");
        }

        if (newPassword.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nouveau mot de passe doit contenir au moins 6 caractères");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastActivityAt(LocalDateTime.now());

        return userRepository.save(user);
    }
    
    /**
     * Trouver un utilisateur par email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Trouver un utilisateur par username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Trouver un utilisateur par ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Récupérer tous les utilisateurs
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Récupérer un utilisateur par ID (avec exception si non trouvé)
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
    }
    
    /**
     * Rechercher des utilisateurs par nom d'utilisateur ou email
     */
    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }
    
    /**
     * Mettre à jour un utilisateur
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}