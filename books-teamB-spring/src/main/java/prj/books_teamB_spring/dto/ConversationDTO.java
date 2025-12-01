package prj.books_teamB_spring.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long id;
    private String type;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private List<UserDTO> participants;
    private ChatMessageDTO lastMessage;
    private Long unreadCount;
}
