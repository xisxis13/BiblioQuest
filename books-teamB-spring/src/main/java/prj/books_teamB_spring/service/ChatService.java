package prj.books_teamB_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prj.books_teamB_spring.entity.*;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatService {

    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private ConversationParticipantRepository participantRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserOnlineStatusRepository onlineStatusRepository;

    // Créer ou obtenir une conversation directe entre deux utilisateurs
    public Conversation getOrCreateDirectConversation(Long userId1, Long userId2) {
        // Vérifier si une conversation existe déjà
        Optional<Conversation> existingConversation = 
            conversationRepository.findDirectConversationBetweenUsers(userId1, userId2);
        
        if (existingConversation.isPresent()) {
            return existingConversation.get();
        }
        
        // Créer une nouvelle conversation
        Conversation conversation = new Conversation();
        conversation.setType(Conversation.ConversationType.DIRECT);
        conversation.setIsActive(true);
        conversation = conversationRepository.save(conversation);
        
        // Ajouter les participants
        User user1 = userRepository.findById(userId1)
            .orElseThrow(() -> new RuntimeException("Utilisateur 1 non trouvé"));
        User user2 = userRepository.findById(userId2)
            .orElseThrow(() -> new RuntimeException("Utilisateur 2 non trouvé"));
        
        addParticipantToConversation(conversation, user1);
        addParticipantToConversation(conversation, user2);
        
        return conversation;
    }
    
    // Ajouter un participant à une conversation
    private void addParticipantToConversation(Conversation conversation, User user) {
        ConversationParticipant participant = new ConversationParticipant();
        participant.setConversation(conversation);
        participant.setUser(user);
        participant.setRole(ConversationParticipant.ParticipantRole.MEMBER);
        participant.setIsActive(true);
        participantRepository.save(participant);
    }
    
    // Obtenir toutes les conversations d'un utilisateur
    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepository.findByUserIdOrderByLastMessageAtDesc(userId);
    }
    
    // Envoyer un message
    public Message sendMessage(Long conversationId, Long senderId, String content) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation non trouvée"));
        
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        
        // Vérifier que l'utilisateur participe à la conversation
        if (!participantRepository.existsByConversationIdAndUserIdAndIsActiveTrue(conversationId, senderId)) {
            throw new RuntimeException("L'utilisateur ne participe pas à cette conversation");
        }
        
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        message.setMessageType(Message.MessageType.TEXT);
        message.setIsEdited(false);
        message.setIsDeleted(false);
        
        message = messageRepository.save(message);
        
        // Mettre à jour la date du dernier message de la conversation
        conversation.setLastMessageAt(message.getCreatedAt());
        conversationRepository.save(conversation);
        
        return message;
    }
    
    // Obtenir les messages d'une conversation avec pagination
    public Page<Message> getConversationMessages(Long conversationId, Long userId, int page, int size) {
        // Vérifier que l'utilisateur participe à la conversation
        if (!participantRepository.existsByConversationIdAndUserIdAndIsActiveTrue(conversationId, userId)) {
            throw new RuntimeException("L'utilisateur ne participe pas à cette conversation");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByConversationIdAndIsDeletedFalseOrderByCreatedAtDesc(conversationId, pageable);
    }
    
    // Marquer les messages comme lus
    public void markMessagesAsRead(Long conversationId, Long userId) {
        participantRepository.updateLastReadAt(conversationId, userId, LocalDateTime.now());
    }
    
    // Compter les messages non lus
    public Long getUnreadMessageCount(Long conversationId, Long userId) {
        return messageRepository.countUnreadMessages(conversationId, userId);
    }
    
    // Compter le total des messages non lus pour un utilisateur
    public Long getTotalUnreadMessageCount(Long userId) {
        return messageRepository.countTotalUnreadMessages(userId);
    }
    
    // Mettre à jour le statut en ligne d'un utilisateur
    public void updateUserOnlineStatus(Long userId, boolean isOnline) {
        Optional<UserOnlineStatus> statusOpt = onlineStatusRepository.findByUserId(userId);
        
        if (statusOpt.isPresent()) {
            onlineStatusRepository.updateOnlineStatus(userId, isOnline, LocalDateTime.now(), LocalDateTime.now());
        } else {
            // Créer un nouveau statut
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            UserOnlineStatus status = new UserOnlineStatus();
            status.setUserId(userId);
            status.setUser(user);
            status.setIsOnline(isOnline);
            status.setLastSeenAt(LocalDateTime.now());
            onlineStatusRepository.save(status);
        }
    }
    
    // Obtenir les utilisateurs en ligne
    public List<UserOnlineStatus> getOnlineUsers() {
        return onlineStatusRepository.findByIsOnlineTrue();
    }
    
    // Rechercher des utilisateurs pour démarrer une conversation
    public List<User> searchUsers(String query, Long currentUserId) {
        // Utiliser le repository User existant pour rechercher
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
            .stream()
            .filter(user -> user.getId() != currentUserId)
            .limit(10)
            .toList();
    }
}
