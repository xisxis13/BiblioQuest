package prj.books_teamB_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Connexion par email seulement
        User user = userRepository.findByEmail(request.getUsername()).orElse(null);
        
        if (user != null && user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.ok(new LoginResponse(user.getUsername(), user.getRole().toString()));
        }
        
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(400).body("Username already exists");
        }
        
        // Créer nouveau user
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setRole(prj.books_teamB_spring.model.Role.USER);
        
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
}