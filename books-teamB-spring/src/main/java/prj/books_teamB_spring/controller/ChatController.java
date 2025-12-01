package prj.books_teamB_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prj.books_teamB_spring.dto.ChatMessageDTO;
import prj.books_teamB_spring.dto.ConversationDTO;
import prj.books_teamB_spring.dto.SendMessageRequest;
import prj.books_teamB_spring.dto.UserDTO;
import prj.books_teamB_spring.entity.*;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.service.ChatService;
import prj.books_teamB_spring.service.ChatAuthService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    @Autowired
    private ChatService chatService;
    
    @Autowired
    private ChatAuthService authService;

    // Obtenir toutes les conversations de l'utilisateur connecté
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getUserConversations() {
        Long userId = authService.getCurrentUserId();
        List<Conversation> conversations = chatService.getUserConversations(userId);
        
        List<ConversationDTO> conversationDTOs = conversations.stream()
            .map(this::convertToConversationDTO)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(conversationDTOs);
    }

    // Créer ou obtenir une conversation directe avec un utilisateur
    @PostMapping("/conversations/direct/{otherUserId}")
    public ResponseEntity<ConversationDTO> getOrCreateDirectConversation(
            @PathVariable Long otherUserId) {
        Long userId = authService.getCurrentUserId();
        Conversation conversation = chatService.getOrCreateDirectConversation(userId, otherUserId);
        
        return ResponseEntity.ok(convertToConversationDTO(conversation));
    }

    // Obtenir les messages d'une conversation
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Page<ChatMessageDTO>> getConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = authService.getCurrentUserId();
        Page<Message> messages = chatService.getConversationMessages(conversationId, userId, page, size);
        
        Page<ChatMessageDTO> messageDTOs = messages.map(this::convertToMessageDTO);
        return ResponseEntity.ok(messageDTOs);
    }

    // Envoyer un message
    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody SendMessageRequest request) {
        Long userId = authService.getCurrentUserId();
        Message message = chatService.sendMessage(conversationId, userId, request.getContent());
        
        return ResponseEntity.ok(convertToMessageDTO(message));
    }

    // Marquer les messages comme lus
    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long conversationId) {
        Long userId = authService.getCurrentUserId();
        chatService.markMessagesAsRead(conversationId, userId);
        
        return ResponseEntity.ok().build();
    }

    // Obtenir le nombre de messages non lus
    @GetMapping("/conversations/{conversationId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable Long conversationId) {
        Long userId = authService.getCurrentUserId();
        Long count = chatService.getUnreadMessageCount(conversationId, userId);
        
        return ResponseEntity.ok(count);
    }

    // Obtenir le nombre total de messages non lus
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getTotalUnreadCount() {
        Long userId = authService.getCurrentUserId();
        Long count = chatService.getTotalUnreadMessageCount(userId);
        
        return ResponseEntity.ok(count);
    }

    // Rechercher des utilisateurs
    @GetMapping("/users/search")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam String query) {
        Long userId = authService.getCurrentUserId();
        List<User> users = chatService.searchUsers(query, userId);
        
        List<UserDTO> userDTOs = users.stream()
            .map(this::convertToUserDTO)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(userDTOs);
    }

    // Mettre à jour le statut en ligne
    @PostMapping("/status/online")
    public ResponseEntity<Void> setOnlineStatus(
            @RequestParam boolean isOnline) {
        Long userId = authService.getCurrentUserId();
        chatService.updateUserOnlineStatus(userId, isOnline);
        
        return ResponseEntity.ok().build();
    }

    // Obtenir les utilisateurs en ligne
    @GetMapping("/users/online")
    public ResponseEntity<List<UserDTO>> getOnlineUsers() {
        List<UserOnlineStatus> onlineStatuses = chatService.getOnlineUsers();
        
        List<UserDTO> userDTOs = onlineStatuses.stream()
            .map(status -> convertToUserDTO(status.getUser()))
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(userDTOs);
    }

    // Méthodes utilitaires
    private ConversationDTO convertToConversationDTO(Conversation conversation) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setType(conversation.getType().toString());
        dto.setName(conversation.getName());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setLastMessageAt(conversation.getLastMessageAt());
        
        // Ajouter les participants (pour les conversations directes, prendre l'autre utilisateur)
        if (conversation.getParticipants() != null && !conversation.getParticipants().isEmpty()) {
            List<UserDTO> participants = conversation.getParticipants().stream()
                .filter(p -> p.getIsActive())
                .map(p -> convertToUserDTO(p.getUser()))
                .collect(Collectors.toList());
            dto.setParticipants(participants);
        }
        
        return dto;
    }

    private ChatMessageDTO convertToMessageDTO(Message message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setSender(convertToUserDTO(message.getSender()));
        dto.setSenderName(message.getSender().getUsername()); // Pour WebSocket
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType().toString());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setIsEdited(message.getIsEdited());
        dto.setEditedAt(message.getEditedAt());
        
        return dto;
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
