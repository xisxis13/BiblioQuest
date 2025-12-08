package prj.books_teamB_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.dto.UserLoginDto;
import prj.books_teamB_spring.dto.UserRegisterDto;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userLoginDto) {
        var userOpt = userRepository.findByEmail(userLoginDto.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
        var user = userOpt.get();
        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDto userRegisterDto) {
        if (userRepository.findByEmail(userRegisterDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        if (userRepository.findByUsername(userRegisterDto.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already in use");
        }
        var user = new User(
            userRegisterDto.getUsername(),
            userRegisterDto.getEmail(),
            passwordEncoder.encode(userRegisterDto.getPassword()),
            Role.USER
        );
        userRepository.save(user);
        return ResponseEntity.ok("Registration successful");
    }
}