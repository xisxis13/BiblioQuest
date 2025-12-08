package prj.books_teamB_spring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.service.UserService;

/**
 * Contrôleur pour la gestion des profils utilisateur
 * Endpoints pour consulter/modifier le profil
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http:ajouter la méthode getById\n" + //
        "//localhost:3000", "http://localhost:5173"})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/users/me - Récupère le profil de l'utilisateur connecté
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("X-Username") String username) {
        User user = userService.requireUser(username);
        return ResponseEntity.ok(user);
    }
}