import axios from 'axios'

const API_BASE_URL = '/api/chat'

class ChatService {
  constructor() {
    // Utiliser la même instance axios que authService pour les intercepteurs JWT
    this.api = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json'
      }
    })

    // Ajouter l'intercepteur pour inclure le token JWT
    this.api.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('auth_token')
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // Intercepteur pour gérer les erreurs d'authentification
    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          console.log('Chat API: 401 error, but NOT forcing logout to prevent conflicts')
          // Ne pas forcer la déconnexion ici, laisser authService gérer ça
          // localStorage.removeItem('auth_token')
          // localStorage.removeItem('auth_user')
          // window.location.href = '/login'
        }
        return Promise.reject(error)
      }
    )
  }

  // Obtenir toutes les conversations de l'utilisateur
  async getConversations() {
    try {
      const response = await this.api.get('/conversations')
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des conversations:', error)
      throw error
    }
  }

  // Créer ou obtenir une conversation directe avec un utilisateur
  async getOrCreateDirectConversation(otherUserId) {
    try {
      const response = await this.api.post(`/conversations/direct/${otherUserId}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la création de la conversation:', error)
      throw error
    }
  }

  // Obtenir les messages d'une conversation
  async getConversationMessages(conversationId, page = 0, size = 20) {
    try {
      const response = await this.api.get(`/conversations/${conversationId}/messages`, {
        params: { page, size }
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des messages:', error)
      throw error
    }
  }

  // Envoyer un message
  async sendMessage(conversationId, content, messageType = 'TEXT') {
    try {
      const response = await this.api.post(`/conversations/${conversationId}/messages`, {
        content,
        messageType
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de l\'envoi du message:', error)
      throw error
    }
  }

  // Marquer les messages comme lus
  async markAsRead(conversationId) {
    try {
      await this.api.post(`/conversations/${conversationId}/read`)
    } catch (error) {
      console.error('Erreur lors du marquage comme lu:', error)
      throw error
    }
  }

  // Obtenir le nombre de messages non lus d'une conversation
  async getUnreadCount(conversationId) {
    try {
      const response = await this.api.get(`/conversations/${conversationId}/unread-count`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération du nombre de non lus:', error)
      throw error
    }
  }

  // Obtenir le nombre total de messages non lus
  async getTotalUnreadCount() {
    try {
      const response = await this.api.get('/unread-count')
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération du total de non lus:', error)
      throw error
    }
  }

  // Rechercher des utilisateurs
  async searchUsers(query) {
    try {
      const response = await this.api.get('/users/search', {
        params: { query }
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de la recherche d\'utilisateurs:', error)
      throw error
    }
  }

  // Mettre à jour le statut en ligne
  async setOnlineStatus(isOnline) {
    try {
      await this.api.post('/status/online', null, {
        params: { isOnline }
      })
    } catch (error) {
      console.error('Erreur lors de la mise à jour du statut:', error)
      throw error
    }
  }

  // Obtenir les utilisateurs en ligne
  async getOnlineUsers() {
    try {
      const response = await this.api.get('/users/online')
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des utilisateurs en ligne:', error)
      throw error
    }
  }
}

export const chatService = new ChatService()
export default chatService
