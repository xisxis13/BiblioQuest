package prj.books_teamB_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prj.books_teamB_spring.dto.ChatMessageDTO;
import prj.books_teamB_spring.model.WebSocketMessage;
import prj.books_teamB_spring.repository.WebSocketMessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WebSocketMessageService {
    
    @Autowired
    private WebSocketMessageRepository messageRepository;
    
    /**
     * Sauvegarder un message WebSocket
     */
    public WebSocketMessage saveMessage(ChatMessageDTO messageDTO) {
        WebSocketMessage message = new WebSocketMessage();
        message.setSenderName(messageDTO.getSenderName());
        message.setReceiverName(messageDTO.getReceiver());
        message.setContent(messageDTO.getContent());
        
        // Convertir le type enum
        if (messageDTO.getType() != null) {
            try {
                message.setType(WebSocketMessage.MessageType.valueOf(messageDTO.getType().toString()));
            } catch (IllegalArgumentException e) {
                message.setType(WebSocketMessage.MessageType.CHAT);
            }
        } else {
            message.setType(WebSocketMessage.MessageType.CHAT);
        }
        
        // Définir la room et le destinataire
        if (messageDTO.getType() == ChatMessageDTO.MessageType.PRIVATE) {
            message.setRoom("private");
            message.setReceiverName(messageDTO.getReceiver()); // Ajouter le destinataire
        } else {
            message.setRoom("public");
            message.setReceiverName(null); // Pas de destinataire pour les messages publics
        }
        
        WebSocketMessage saved = messageRepository.save(message);
        System.out.println("WebSocketMessage persiste: id=" + saved.getId() + ", type=" + saved.getType() + ", room=" + saved.getRoom());
        return saved;
    }
    
    /**
     * Récupérer les messages publics récents
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getRecentPublicMessages(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<WebSocketMessage> messages = messageRepository.findRecentPublicMessages(pageable);
        
        // Inverser l'ordre pour avoir les plus anciens en premier
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les messages privés entre deux utilisateurs
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getPrivateMessagesBetweenUsers(String user1, String user2) {
        List<WebSocketMessage> messages = messageRepository.findPrivateMessagesBetweenUsers(user1, user2);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les messages depuis une certaine date
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getPublicMessagesSince(LocalDateTime since) {
        List<WebSocketMessage> messages = messageRepository.findPublicMessagesSince(since);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Compter les messages non lus pour un utilisateur
     */
    @Transactional(readOnly = true)
    public Long countUnreadMessagesForUser(String username, LocalDateTime lastReadTime) {
        return messageRepository.countUnreadMessagesForUser(username, lastReadTime);
    }
    
    /**
     * Nettoyer les anciens messages (plus de 30 jours)
     */
    public void cleanOldMessages() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        messageRepository.softDeleteOldMessages(cutoffDate);
    }
    
    /**
     * Convertir WebSocketMessage en ChatMessageDTO
     */
    private ChatMessageDTO convertToDTO(WebSocketMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSenderName(message.getSenderName());
        dto.setReceiver(message.getReceiverName());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getCreatedAt());
        
        // Convertir le type enum
        try {
            dto.setType(ChatMessageDTO.MessageType.valueOf(message.getType().toString()));
        } catch (IllegalArgumentException e) {
            dto.setType(ChatMessageDTO.MessageType.CHAT);
        }
        
        return dto;
    }
    
    /**
     * Récupérer les messages récents d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getMessagesByUser(String username, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<WebSocketMessage> messages = messageRepository.findMessagesByUser(username, pageable);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
