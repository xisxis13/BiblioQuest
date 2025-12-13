import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.PROD
    ? '/api'
    : 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

export const readingService = {
  // === BIBLIOTHÈQUE UTILISATEUR ===

  // Récupérer la bibliothèque d'un utilisateur
  async getUserLibrary(userId) {
    try {
      const response = await api.get(`/users/${userId}/library`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération de la bibliothèque:', error)
      throw error
    }
  },

  // Ajouter un livre à la bibliothèque
  async addBookToLibrary(userId, bookId) {
    try {
      const response = await api.post(`/users/${userId}/books?bookId=${bookId}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de l\'ajout du livre:', error)
      throw error
    }
  },

  // Mettre à jour un livre dans la bibliothèque (statut, progression, etc.)
  async updateUserBook(userId, bookId, updates) {
    try {
      const params = new URLSearchParams()
      if (updates.status) params.append('status', updates.status)
      if (updates.progress !== undefined) params.append('progress', updates.progress)
      if (updates.rating) params.append('rating', updates.rating)
      if (updates.personalNote) params.append('personalNote', updates.personalNote)

      const response = await api.put(`/users/${userId}/books/${bookId}?${params}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la mise à jour du livre:', error)
      throw error
    }
  },

  // Supprimer un livre de la bibliothèque
  async removeBookFromLibrary(userId, bookId) {
    try {
      await api.delete(`/users/${userId}/books/${bookId}`)
      return true
    } catch (error) {
      console.error('Erreur lors de la suppression du livre:', error)
      throw error
    }
  },

  // === SESSIONS DE LECTURE ===

  // Créer une nouvelle session de lecture
  async createReadingSession(userId, userBookId, duration, pagesRead) {
    try {
      const params = new URLSearchParams()
      params.append('userId', userId)
      params.append('userBookId', userBookId)
      params.append('duration', duration)
      params.append('pagesRead', pagesRead)

      const response = await api.post(`/reading-sessions?${params}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la création de la session:', error)
      throw error
    }
  },

  // Récupérer les sessions de lecture d'un utilisateur
  async getUserSessions(userId) {
    try {
      const response = await api.get(`/users/${userId}/reading-sessions`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des sessions:', error)
      throw error
    }
  },

  // Mettre à jour une session de lecture
  async updateReadingSession(sessionId, updates) {
    try {
      const params = new URLSearchParams()
      if (updates.duration !== undefined) params.append('duration', updates.duration)
      if (updates.pagesRead !== undefined) params.append('pagesRead', updates.pagesRead)
      if (updates.notes) params.append('notes', updates.notes)
      if (updates.mood) params.append('mood', updates.mood)

      const response = await api.put(`/reading-sessions/${sessionId}?${params}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la mise à jour de la session:', error)
      throw error
    }
  },

  // Ajouter une réaction à une session
  async addReactionToSession(sessionId, comment, mood) {
    try {
      const params = new URLSearchParams()
      params.append('comment', comment)
      params.append('mood', mood)

      const response = await api.post(`/reading-sessions/${sessionId}/reaction?${params}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de l\'ajout de la réaction:', error)
      throw error
    }
  },

  // Partager une session publiquement
  async shareSession(sessionId) {
    try {
      const response = await api.put(`/reading-sessions/${sessionId}/share`)
      return response.data
    } catch (error) {
      console.error('Erreur lors du partage de la session:', error)
      throw error
    }
  },

  // === STATISTIQUES ===

  // Récupérer les statistiques de lecture d'un utilisateur
  async getReadingStats(userId) {
    try {
      const response = await api.get(`/users/${userId}/reading-stats`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des statistiques:', error)
      throw error
    }
  },

  // === LIVRES ===

  // Récupérer tous les livres disponibles
  async getAllBooks() {
    try {
      const response = await api.get('/books')
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des livres:', error)
      throw error
    }
  },

  // Rechercher des livres
  async searchBooks(query) {
    try {
      const response = await api.get(`/books/search?query=${encodeURIComponent(query)}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la recherche de livres:', error)
      throw error
    }
  },

  // Récupérer un livre par ID
  async getBookById(bookId) {
    try {
      const response = await api.get(`/books/${bookId}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération du livre:', error)
      throw error
    }
  }
}

export default readingService
