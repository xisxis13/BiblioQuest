package prj.books_teamB_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.dto.ChatMessageDTO;
import prj.books_teamB_spring.service.WebSocketMessageService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatHistoryController {
    
    @Autowired
    private WebSocketMessageService messageService;
    
    /**
     * Récupérer les messages publics récents
     */
    @GetMapping("/messages/public")
    public ResponseEntity<List<ChatMessageDTO>> getPublicMessages(
            @RequestParam(defaultValue = "50") int limit) {
        try {
            List<ChatMessageDTO> messages = messageService.getRecentPublicMessages(limit);
            System.out.println("Messages publics récupérés: count=" + messages.size() + ", limit=" + limit);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des messages publics: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Récupérer les messages publics depuis une certaine date
     */
    @GetMapping("/messages/public/since")
    public ResponseEntity<List<ChatMessageDTO>> getPublicMessagesSince(
            @RequestParam String since) {
        try {
            LocalDateTime sinceDate = LocalDateTime.parse(since);
            List<ChatMessageDTO> messages = messageService.getPublicMessagesSince(sinceDate);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des messages depuis " + since + ": " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Récupérer les messages privés entre deux utilisateurs
     */
    @GetMapping("/messages/private")
    public ResponseEntity<List<ChatMessageDTO>> getPrivateMessages(
            @RequestParam String user1,
            @RequestParam String user2) {
        try {
            List<ChatMessageDTO> messages = messageService.getPrivateMessagesBetweenUsers(user1, user2);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des messages privés: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Récupérer les messages d'un utilisateur
     */
    @GetMapping("/messages/user/{username}")
    public ResponseEntity<List<ChatMessageDTO>> getUserMessages(
            @PathVariable String username,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            List<ChatMessageDTO> messages = messageService.getMessagesByUser(username, limit);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des messages de l'utilisateur " + username + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Compter les messages non lus pour un utilisateur
     */
    @GetMapping("/messages/unread/{username}")
    public ResponseEntity<Long> getUnreadMessagesCount(
            @PathVariable String username,
            @RequestParam String lastReadTime) {
        try {
            LocalDateTime lastRead = LocalDateTime.parse(lastReadTime);
            Long count = messageService.countUnreadMessagesForUser(username, lastRead);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des messages non lus: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Nettoyer les anciens messages (admin uniquement)
     */
    @PostMapping("/messages/cleanup")
    public ResponseEntity<String> cleanupOldMessages() {
        try {
            messageService.cleanOldMessages();
            return ResponseEntity.ok("Nettoyage des anciens messages effectué avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors du nettoyage des anciens messages: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erreur lors du nettoyage");
        }
    }
}
