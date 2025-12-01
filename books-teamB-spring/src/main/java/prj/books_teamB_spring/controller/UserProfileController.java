package prj.books_teamB_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import prj.books_teamB_spring.dto.ChangePasswordRequest;
import prj.books_teamB_spring.dto.UpdateProfileRequest;
import prj.books_teamB_spring.dto.UserDTO;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.service.ChatAuthService;
import prj.books_teamB_spring.service.UserProfileService;

import java.io.IOException;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class UserProfileController {

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private ChatAuthService authService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long userId) {
        User user = profileService.getUserProfile(userId);
        return ResponseEntity.ok(convertToUserDTO(user));
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<UserDTO> uploadAvatar(@PathVariable Long userId, @RequestParam("avatar") MultipartFile file) {
        System.out.println("=== AVATAR UPLOAD ENDPOINT ===");
        System.out.println("User ID: " + userId);
        System.out.println("File name: " + (file != null ? file.getOriginalFilename() : "null"));
        System.out.println("File size: " + (file != null ? file.getSize() : "null"));
        System.out.println("File type: " + (file != null ? file.getContentType() : "null"));
        System.out.println("File is empty: " + (file != null ? file.isEmpty() : "file is null"));
        
        try {
            System.out.println("Calling profileService.updateAvatar...");
            User user = profileService.updateAvatar(userId, file);
            System.out.println("Avatar updated successfully for user: " + user.getUsername());
            return ResponseEntity.ok(convertToUserDTO(user));
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        } catch (RuntimeException e) {
            System.out.println("RuntimeException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UpdateProfileRequest request) {
        Long userId = authService.getCurrentUserId();
        User user = profileService.updateProfile(userId, request);
        return ResponseEntity.ok(convertToUserDTO(user));
    }

    @PostMapping("/avatar")
    public ResponseEntity<UserDTO> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        try {
            Long userId = authService.getCurrentUserId();
            User user = profileService.updateAvatar(userId, file);
            return ResponseEntity.ok(convertToUserDTO(user));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<UserDTO> removeAvatar() {
        Long userId = authService.getCurrentUserId();
        User user = profileService.removeAvatar(userId);
        return ResponseEntity.ok(convertToUserDTO(user));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        Long userId = authService.getCurrentUserId();
        profileService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        // avatarData = avatarUrl maintenant (base64 stocké dans avatar_url)
        dto.setAvatarData(user.getAvatarUrl());
        return dto;
    }
}
