package prj.books_teamB_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import prj.books_teamB_spring.dto.UpdateProfileRequest;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@Transactional
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            if (!request.getUsername().equals(user.getUsername())) {
                if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                    throw new RuntimeException("Ce nom d'utilisateur est déjà utilisé");
                }
                user.setUsername(request.getUsername().trim());
            }
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!request.getEmail().equals(user.getEmail())) {
                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                    throw new RuntimeException("Cette adresse email est déjà utilisée");
                }
                user.setEmail(request.getEmail().trim());
            }
        }

        user.setLastActivityAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateAvatar(Long userId, MultipartFile avatarFile) throws IOException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (avatarFile.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        if (avatarFile.getSize() > 5242880) { // 5MB
            throw new IllegalArgumentException("Le fichier est trop volumineux (max 5MB)");
        }

        String contentType = avatarFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier doit être une image");
        }

        // Convertir l'image en base64 et stocker dans avatar_url
        byte[] imageBytes = avatarFile.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String avatarData = "data:" + contentType + ";base64," + base64Image;

        user.setAvatarUrl(avatarData); // Stocker le base64 dans avatar_url
        user.setLastActivityAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User removeAvatar(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setAvatarUrl(null);
        user.setLastActivityAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Mot de passe actuel incorrect");
        }

        if (newPassword.length() < 6) {
            throw new RuntimeException("Le nouveau mot de passe doit contenir au moins 6 caractères");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastActivityAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
