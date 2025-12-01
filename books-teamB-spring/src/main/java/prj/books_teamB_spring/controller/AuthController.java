package prj.books_teamB_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.authenticateUser(request.getUsername(), request.getPassword());
            
            // Mettre à jour la dernière activité
            user.setLastActivityAt(java.time.LocalDateTime.now());
            userService.updateUser(user);
            
            return ResponseEntity.ok(new LoginResponse(
                "dummy-jwt-token", 
                user.getId(),
                user.getUsername(), 
                user.getEmail(),
                user.getRole().toString(),
                user.getAvatarUrl()
            ));
        } catch (ResponseStatusException e) {
            // Gestion spécifique des erreurs d'authentification
            if (e.getStatusCode().value() == 401) {
                return ResponseEntity.status(401)
                    .body(new ErrorResponse("INVALID_CREDENTIALS", "Email ou mot de passe incorrect"));
            }
            return ResponseEntity.status(e.getStatusCode())
                .body(new ErrorResponse("AUTH_ERROR", e.getReason()));
        } catch (Exception e) {
            System.err.println("Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("LOGIN_ERROR", "Erreur interne du serveur"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User savedUser = userService.createUser(
                request.getUsername(), 
                request.getEmail(), 
                request.getPassword()
            );
            
            return ResponseEntity.ok(new LoginResponse(
                "dummy-jwt-token",
                savedUser.getId(),
                savedUser.getUsername(), 
                savedUser.getEmail(),
                savedUser.getRole().toString(),
                savedUser.getAvatarUrl()
            ));
        } catch (ResponseStatusException e) {
            // Gestion spécifique des erreurs d'authentification
            if (e.getStatusCode().value() == 401) {
                return ResponseEntity.status(401)
                    .body(new ErrorResponse("INVALID_CREDENTIALS", "Email ou mot de passe incorrect"));
            }
            return ResponseEntity.status(e.getStatusCode())
                .body(new ErrorResponse("AUTH_ERROR", e.getReason()));
        } catch (Exception e) {
            System.err.println("Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("REGISTER_ERROR", "Erreur interne du serveur"));
        }
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
        private String token;
        private Long id;
        private String username;
        private String email;
        private String role;
        private String avatarUrl;
        
        public LoginResponse(String token, Long id, String username, String email, String role, String avatarUrl) {
            this.token = token;
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
            this.avatarUrl = avatarUrl;
        }
        
        public String getToken() { return token; }
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getAvatarUrl() { return avatarUrl; }
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