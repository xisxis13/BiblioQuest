package prj.books_teamB_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import prj.books_teamB_spring.dto.ChatMessageDTO;
import prj.books_teamB_spring.dto.UserStatusDTO;
import prj.books_teamB_spring.service.WebSocketMessageService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private WebSocketMessageService messageService;
    
    // Stockage en mémoire des utilisateurs connectés (à remplacer par Redis en production)
    private final Map<String, UserStatusDTO> connectedUsers = new ConcurrentHashMap<>();

    /**
     * Gestion des messages de chat public
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessage) {
        System.out.println("Message reçu: " + chatMessage.getContent() + " de " + chatMessage.getSenderName());
        
        // Ajouter timestamp si pas présent
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now());
        }
        
        // Définir le type si pas présent
        if (chatMessage.getType() == null) {
            chatMessage.setType(ChatMessageDTO.MessageType.CHAT);
        }
        
        // Sauvegarder le message en base de données
        try {
            messageService.saveMessage(chatMessage);
            System.out.println("Message sauvegardé en base de données");
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du message: " + e.getMessage());
        }
        
        System.out.println("Message envoyé: " + chatMessage.getContent() + " de " + chatMessage.getSenderName());
        return chatMessage;
    }

    /**
     * Gestion de l'ajout d'un utilisateur au chat
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessageDTO addUser(@Payload ChatMessageDTO chatMessage, 
                                  SimpMessageHeaderAccessor headerAccessor) {
        
        String username = chatMessage.getSenderName();
        System.out.println("Utilisateur connecté: " + username);
        
        // Ajouter le nom d'utilisateur à la session WebSocket
        headerAccessor.getSessionAttributes().put("username", username);
        
        // Ajouter à la liste des utilisateurs connectés
        UserStatusDTO userStatus = new UserStatusDTO();
        userStatus.setUsername(username);
        userStatus.setOnline(true);
        userStatus.setLastSeen(LocalDateTime.now());
        connectedUsers.put(username, userStatus);
        
        // Notifier les autres utilisateurs
        chatMessage.setType(ChatMessageDTO.MessageType.JOIN);
        chatMessage.setTimestamp(LocalDateTime.now());
        
        // Envoyer la liste des utilisateurs connectés
        broadcastUserList();
        
        return chatMessage;
    }

    /**
     * Envoi de message privé
     */
    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessageDTO chatMessage) {
        System.out.println("Message privé reçu de " + chatMessage.getSenderName() + " vers " + chatMessage.getReceiver());
        
        // Ajouter timestamp si pas présent
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now());
        }
        
        // Définir le type comme privé
        chatMessage.setType(ChatMessageDTO.MessageType.PRIVATE);
        
        // Sauvegarder le message privé en base de données
        try {
            messageService.saveMessage(chatMessage);
            System.out.println("Message privé sauvegardé en base de données");
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du message privé: " + e.getMessage());
        }
        
        // Envoyer le message au destinataire spécifique
        messagingTemplate.convertAndSendToUser(
            chatMessage.getReceiver(), 
            "/queue/private", 
            chatMessage
        );
        
        System.out.println("Message privé envoyé à " + chatMessage.getReceiver());
        
        // Envoyer une copie à l'expéditeur pour confirmation
        messagingTemplate.convertAndSendToUser(
            chatMessage.getSenderName(), 
            "/queue/private", 
            chatMessage
        );
    }

    /**
     * Diffuser la liste des utilisateurs connectés
     */
    private void broadcastUserList() {
        messagingTemplate.convertAndSend("/topic/users", connectedUsers.values());
    }

    /**
     * Gestion de la déconnexion (appelée automatiquement)
     */
    public void handleUserDisconnect(String username) {
        if (username != null) {
            System.out.println("Utilisateur déconnecté: " + username);
            
            // Retirer de la liste des connectés
            UserStatusDTO user = connectedUsers.remove(username);
            
            if (user != null) {
                // Créer un message de déconnexion
                ChatMessageDTO leaveMessage = new ChatMessageDTO();
                leaveMessage.setType(ChatMessageDTO.MessageType.LEAVE);
                leaveMessage.setSenderName(username);
                leaveMessage.setTimestamp(LocalDateTime.now());
                
                // Notifier les autres utilisateurs
                messagingTemplate.convertAndSend("/topic/public", leaveMessage);
                broadcastUserList();
            }
        }
    }
}
