package prj.books_teamB_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Vérifier si l'email existe
        User user = userRepository.findByEmail(request.getUsername()).orElse(null);
        
        if (user == null) {
            return ResponseEntity.status(401).body(new ErrorResponse("EMAIL_NOT_FOUND", "Aucun compte associé à cette adresse email"));
        }
        
        // Vérifier le mot de passe
        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(401).body(new ErrorResponse("INVALID_PASSWORD", "Mot de passe incorrect"));
        }
        
        return ResponseEntity.ok(new LoginResponse(user.getUsername(), user.getRole().toString()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(400).body(new ErrorResponse("EMAIL_EXISTS", "Cette adresse email est déjà utilisée"));
        }
        
        // Vérifier si le nom d'utilisateur existe déjà
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(400).body(new ErrorResponse("USERNAME_EXISTS", "Ce nom d'utilisateur est déjà pris"));
        }
        
        // Validation des champs
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.status(400).body(new ErrorResponse("INVALID_USERNAME", "Le nom d'utilisateur est requis"));
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.status(400).body(new ErrorResponse("INVALID_EMAIL", "L'adresse email est requise"));
        }
        
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return ResponseEntity.status(400).body(new ErrorResponse("INVALID_PASSWORD", "Le mot de passe doit contenir au moins 6 caractères"));
        }
        
        // Créer nouveau user
        User newUser = new User(
            request.username,
            request.email,
            request.password,
            Role.USER
        );
        
        User savedUser = userRepository.save(newUser);
        return ResponseEntity.ok(new LoginResponse(savedUser.getUsername(), savedUser.getRole().toString()));
    }

    static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    static class LoginResponse {
        private String username;
        private String role;
        
        public LoginResponse(String username, String role) {
            this.username = username;
            this.role = role;
        }
        
        public String getUsername() { return username; }
        public String getRole() { return role; }
    }

    static class ErrorResponse {
        private String code;
        private String message;
        
        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
        
        public String getCode() { return code; }
        public String getMessage() { return message; }
    }
}