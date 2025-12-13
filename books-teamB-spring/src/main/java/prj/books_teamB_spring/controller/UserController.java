package prj.books_teamB_spring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import prj.books_teamB_spring.dto.UserDto;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.service.UserService;
import java.util.List;

/**
 * Contrôleur pour la gestion des profils utilisateur
 * Endpoints pour consulter/modifier le profil
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/users/me - Récupère le profil de l'utilisateur connecté
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(
            @PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(new UserDto(user));
        } catch (Exception e) {
            throw new RuntimeException("User not found");
        }
    }

    /**
     * GET /api/users - Récupère tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
}