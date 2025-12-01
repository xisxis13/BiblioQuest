package prj.books_teamB_spring.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import prj.books_teamB_spring.dto.ChatMessage;

@Controller
public class ChatController {

    // Le client envoie sur /app/chat.send
    @MessageMapping("/chat.send")
    // Tous les abonnés à /topic/messages reçoivent la réponse
    @SendTo("/topic/messages")
    public ChatMessage send(ChatMessage message) {
        // ici tu peux ajouter de la logique métier, sauvegarde, etc.
        System.out.println("message: " + message.getContent());
        return message;
    }
}
