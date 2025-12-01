package prj.books_teamB_spring.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private Long conversationId;
    private UserDTO sender;
    private String senderName; // Nom d'utilisateur pour WebSocket
    private String receiver; // Pour messages privés
    private String content;
    private String messageType;
    private MessageType type; // Enum pour WebSocket
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private LocalDateTime createdAt;
    private LocalDateTime timestamp; // Alias pour WebSocket
    private Boolean isEdited;
    private LocalDateTime editedAt;
    private Long replyToMessageId;

    // Enum pour les types de messages WebSocket
    public enum MessageType {
        CHAT, JOIN, LEAVE, PRIVATE
    }

    // Méthodes de commodité pour WebSocket
    public void setSenderUsername(String username) {
        this.senderName = username;
    }

    public String getSenderUsername() {
        return this.senderName;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        this.createdAt = timestamp;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp != null ? this.timestamp : this.createdAt;
    }
}
