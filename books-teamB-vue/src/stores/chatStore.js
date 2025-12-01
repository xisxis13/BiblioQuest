import { defineStore } from 'pinia'
import { chatService } from '@/services/chatService'

export const useChatStore = defineStore('chat', {
  state: () => ({
    conversations: [],
    activeConversation: null,
    messages: {},
    onlineUsers: [],
    isTyping: {},
    unreadCounts: {},
    loading: false,
    error: null
  }),

  getters: {
    // Obtenir toutes les conversations triées par dernière activité
    getSortedConversations: (state) => {
      return [...state.conversations].sort((a, b) => {
        const aTime = a.lastMessage?.timestamp || new Date(0)
        const bTime = b.lastMessage?.timestamp || new Date(0)
        return new Date(bTime) - new Date(aTime)
      })
    },

    // Obtenir les messages d'une conversation
    getConversationMessages: (state) => (conversationId) => {
      return state.messages[conversationId] || []
    },

    // Obtenir le nombre total de messages non lus
    getTotalUnreadCount: (state) => {
      return Object.values(state.unreadCounts).reduce((total, count) => total + count, 0)
    },

    // Vérifier si un utilisateur est en ligne
    isUserOnline: (state) => (userId) => {
      return state.onlineUsers.includes(userId)
    },

    // Vérifier si quelqu'un tape dans une conversation
    isUserTyping: (state) => (conversationId) => {
      return state.isTyping[conversationId] || false
    },

    // Obtenir une conversation par ID utilisateur
    getConversationByUserId: (state) => (userId) => {
      return state.conversations.find(conv => conv.user.id === userId)
    }
  },

  actions: {
    // Initialiser le store en chargeant les conversations depuis le backend
    async initializeChat() {
      this.loading = true
      this.error = null
      
      try {
        // Charger les conversations depuis le backend
        const conversations = await chatService.getConversations()
        this.conversations = conversations
        
        // Charger le nombre total de messages non lus
        const totalUnread = await chatService.getTotalUnreadCount()
        this.updateUnreadCounts(conversations, totalUnread)
        
        // Mettre le statut en ligne
        await chatService.setOnlineStatus(true)
        
      } catch (error) {
        console.error('Erreur lors de l\'initialisation du chat:', error)
        this.error = error.message
        
        // Fallback avec données fictives en cas d'erreur
        this.initializeFallbackData()
      } finally {
        this.loading = false
      }
    },

    // Données fictives en fallback
    initializeFallbackData() {
      this.conversations = [
        {
          id: 1,
          user: {
            id: 1,
            username: 'Alice Martin',
            email: 'alice@example.com',
            avatar: null
          },
          lastMessage: {
            id: 1,
            content: 'Salut ! As-tu lu le dernier livre de science-fiction ?',
            timestamp: new Date(Date.now() - 5 * 60 * 1000),
            senderId: 1
          },
          createdAt: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000)
        },
        {
          id: 2,
          user: {
            id: 2,
            username: 'Bob Dupont',
            email: 'bob@example.com',
            avatar: null
          },
          lastMessage: {
            id: 2,
            content: 'Merci pour la recommandation !',
            timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000),
            senderId: 2
          },
          createdAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000)
        },
        {
          id: 3,
          user: {
            id: 3,
            username: 'Claire Moreau',
            email: 'claire@example.com',
            avatar: null
          },
          lastMessage: {
            id: 3,
            content: 'J\'ai adoré ce livre, on peut en discuter ?',
            timestamp: new Date(Date.now() - 24 * 60 * 60 * 1000),
            senderId: 3
          },
          createdAt: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000)
        }
      ]

      // Messages fictifs
      this.messages = {
        1: [
          {
            id: 1,
            content: 'Salut ! Comment ça va ?',
            timestamp: new Date(Date.now() - 60 * 60 * 1000),
            senderId: 1,
            conversationId: 1
          },
          {
            id: 2,
            content: 'Ça va bien merci ! Et toi ?',
            timestamp: new Date(Date.now() - 55 * 60 * 1000),
            senderId: 'current_user',
            conversationId: 1
          },
          {
            id: 3,
            content: 'As-tu lu le dernier livre de science-fiction dont je t\'ai parlé ?',
            timestamp: new Date(Date.now() - 10 * 60 * 1000),
            senderId: 1,
            conversationId: 1
          }
        ],
        2: [
          {
            id: 4,
            content: 'Merci pour la recommandation de livre !',
            timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000),
            senderId: 2,
            conversationId: 2
          }
        ],
        3: [
          {
            id: 5,
            content: 'J\'ai adoré ce livre, on peut en discuter ?',
            timestamp: new Date(Date.now() - 24 * 60 * 60 * 1000),
            senderId: 3,
            conversationId: 3
          }
        ]
      }

      // Utilisateurs en ligne
      this.onlineUsers = [1, 3]

      // Comptes de messages non lus
      this.unreadCounts = {
        1: 2,
        2: 0,
        3: 1
      }
    },

    // Créer ou obtenir une conversation avec un utilisateur
    async getOrCreateConversation(userId, userInfo = null) {
      try {
        // Essayer de créer/obtenir la conversation via le backend
        const conversation = await chatService.getOrCreateDirectConversation(userId)
        
        // Ajouter à la liste locale si pas déjà présente
        const existingIndex = this.conversations.findIndex(conv => conv.id === conversation.id)
        if (existingIndex === -1) {
          this.conversations.unshift(conversation)
        }
        
        // Initialiser les messages et compteurs si nécessaire
        if (!this.messages[conversation.id]) {
          this.messages[conversation.id] = []
        }
        if (!this.unreadCounts[conversation.id]) {
          this.unreadCounts[conversation.id] = 0
        }
        
        return conversation
      } catch (error) {
        console.error('Erreur lors de la création de conversation:', error)
        
        // Fallback avec données locales
        let conversation = this.getConversationByUserId(userId)
        
        if (!conversation) {
          conversation = {
            id: Date.now(),
            user: userInfo || {
              id: userId,
              username: `Utilisateur ${userId}`,
              email: `user${userId}@example.com`,
              avatar: null
            },
            lastMessage: null,
            createdAt: new Date()
          }
          
          this.conversations.unshift(conversation)
          this.messages[conversation.id] = []
          this.unreadCounts[conversation.id] = 0
        }
        
        return conversation
      }
    },

    // Sélectionner une conversation active
    setActiveConversation(conversation) {
      this.activeConversation = conversation
      
      // Marquer comme lu
      if (conversation) {
        this.markAsRead(conversation.id)
      }
    },

    // Envoyer un message
    async sendMessage(conversationId, content) {
      if (!content.trim()) return

      try {
        // Envoyer le message via le backend
        const message = await chatService.sendMessage(conversationId, content.trim())
        
        // Ajouter le message à la conversation locale
        if (!this.messages[conversationId]) {
          this.messages[conversationId] = []
        }
        this.messages[conversationId].push({
          id: message.id,
          content: message.content,
          timestamp: new Date(message.createdAt),
          senderId: message.sender.id,
          conversationId: conversationId,
          isOwn: true
        })

        // Mettre à jour le dernier message de la conversation
        const conversation = this.conversations.find(conv => conv.id === conversationId)
        if (conversation) {
          conversation.lastMessage = {
            id: message.id,
            content: message.content,
            timestamp: new Date(message.createdAt),
            senderId: message.sender.id
          }
          conversation.lastMessageAt = new Date(message.createdAt)
        }

        return message
      } catch (error) {
        console.error('Erreur lors de l\'envoi du message:', error)
        
        // Fallback avec message local
        const message = {
          id: Date.now(),
          content: content.trim(),
          timestamp: new Date(),
          senderId: 'current_user',
          conversationId: conversationId,
          isOwn: true
        }

        if (!this.messages[conversationId]) {
          this.messages[conversationId] = []
        }
        this.messages[conversationId].push(message)

        const conversation = this.conversations.find(conv => conv.id === conversationId)
        if (conversation) {
          conversation.lastMessage = {
            id: message.id,
            content: message.content,
            timestamp: message.timestamp,
            senderId: 'current_user'
          }
        }

        return message
      }
    },

    // Simuler un message entrant (pour le développement)
    simulateIncomingMessage(conversationId) {
      const conversation = this.conversations.find(conv => conv.id === conversationId)
      if (!conversation) return

      const responses = [
        'Merci pour ton message !',
        'C\'est intéressant !',
        'Je vais y réfléchir.',
        'Bonne idée !',
        'On en reparle bientôt ?',
        'Absolument d\'accord !',
        'J\'ai hâte de lire ça !',
        'Excellente recommandation !'
      ]

      const message = {
        id: Date.now(),
        content: responses[Math.floor(Math.random() * responses.length)],
        timestamp: new Date(),
        senderId: conversation.user.id,
        conversationId: conversationId
      }

      this.messages[conversationId].push(message)
      
      // Mettre à jour le dernier message
      conversation.lastMessage = {
        id: message.id,
        content: message.content,
        timestamp: message.timestamp,
        senderId: conversation.user.id
      }

      // Incrémenter le compteur de non lus si ce n'est pas la conversation active
      if (this.activeConversation?.id !== conversationId) {
        this.unreadCounts[conversationId] = (this.unreadCounts[conversationId] || 0) + 1
      }
    },

    // Marquer une conversation comme lue
    async markAsRead(conversationId) {
      try {
        await chatService.markAsRead(conversationId)
        this.unreadCounts[conversationId] = 0
      } catch (error) {
        console.error('Erreur lors du marquage comme lu:', error)
        // Marquer localement même en cas d'erreur
        this.unreadCounts[conversationId] = 0
      }
    },

    // Mettre à jour les compteurs de messages non lus
    updateUnreadCounts(conversations, totalUnread) {
      conversations.forEach(conv => {
        this.unreadCounts[conv.id] = conv.unreadCount || 0
      })
    },

    // Définir l'état de frappe
    setTypingStatus(conversationId, isTyping) {
      if (isTyping) {
        this.isTyping[conversationId] = true
        // Arrêter automatiquement après 3 secondes
        setTimeout(() => {
          this.isTyping[conversationId] = false
        }, 3000)
      } else {
        this.isTyping[conversationId] = false
      }
    },

    // Mettre à jour le statut en ligne des utilisateurs
    updateOnlineUsers(userIds) {
      this.onlineUsers = userIds
    },

    // Supprimer une conversation
    deleteConversation(conversationId) {
      this.conversations = this.conversations.filter(conv => conv.id !== conversationId)
      delete this.messages[conversationId]
      delete this.unreadCounts[conversationId]
      delete this.isTyping[conversationId]
      
      if (this.activeConversation?.id === conversationId) {
        this.activeConversation = null
      }
    },

    // Rechercher dans les conversations
    searchConversations(query) {
      if (!query.trim()) return this.getSortedConversations

      const searchTerm = query.toLowerCase()
      return this.conversations.filter(conv => 
        conv.user.username.toLowerCase().includes(searchTerm) ||
        conv.user.email.toLowerCase().includes(searchTerm) ||
        conv.lastMessage?.content.toLowerCase().includes(searchTerm)
      )
    },

    // Nettoyer les erreurs
    clearError() {
      this.error = null
    },

    // Réinitialiser le store
    resetChat() {
      this.conversations = []
      this.activeConversation = null
      this.messages = {}
      this.onlineUsers = []
      this.isTyping = {}
      this.unreadCounts = {}
      this.loading = false
      this.error = null
    }
  }
})
