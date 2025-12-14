import { defineStore } from 'pinia'
import { readingSessionService } from '@/services/readingSessionService'

export const useReadingStore = defineStore('reading', {
  state: () => ({
    userLibrary: [],
    readingSessions: [],
    readingNotes: [],
    readingGoals: [],
    currentProgress: {},
    isLoading: false,
    error: null
  }),

  getters: {
    // Get reading sessions for a specific book
    getSessionsByBook: (state) => (bookId) => {
      return state.readingSessions.filter(session => session.bookId === bookId)
    },

    // Get notes for a specific book
    getNotesByBook: (state) => (bookId) => {
      return state.readingNotes.filter(note => note.bookId === bookId)
    },

    // Get current progress for a book
    getProgressByBook: (state) => (bookId) => {
      return state.currentProgress[bookId] || { currentPage: 0, percentage: 0 }
    },

    // Get all active reading goals
    getActiveGoals: (state) => {
      return state.readingGoals.filter(goal => goal.status === 'active')
    },

    // Calculate total pages read
    getTotalPagesRead: (state) => {
      return state.readingSessions.reduce((total, session) => total + session.pagesRead, 0)
    }
  },

  actions: {
    // === BIBLIOTHÈQUE UTILISATEUR ===
    
    // Récupérer la bibliothèque de l'utilisateur connecté
    async fetchUserLibrary() {
      this.isLoading = true
      this.error = null
      
      try {
        const currentUser = JSON.parse(localStorage.getItem('auth_user'))
        if (!currentUser) throw new Error('Utilisateur non connecté')
        
        const library = await readingSessionService.getUserLibrary(currentUser.id || 1)
        this.userLibrary = library
        return library
      } catch (error) {
        this.error = error.message
        console.error('Erreur lors de la récupération de la bibliothèque:', error)
        throw error
      } finally {
        this.isLoading = false
      }
    },

    // Ajouter un livre à la bibliothèque
    async addBookToLibrary(bookId) {
      this.isLoading = true
      this.error = null
      
      try {
        const currentUser = JSON.parse(localStorage.getItem('auth_user'))
        if (!currentUser) throw new Error('Utilisateur non connecté')
        
        const userBook = await readingSessionService.addBookToLibrary(currentUser.id || 1, bookId)
        this.userLibrary.push(userBook)
        return userBook
      } catch (error) {
        this.error = error.message
        console.error('Erreur lors de l\'ajout du livre:', error)
        throw error
      } finally {
        this.isLoading = false
      }
    },

    // Mettre à jour le statut/progression d'un livre
    async updateUserBook(bookId, updates) {
      this.isLoading = true
      this.error = null
      
      try {
        const currentUser = JSON.parse(localStorage.getItem('auth_user'))
        if (!currentUser) throw new Error('Utilisateur non connecté')
        
        const updatedBook = await readingSessionService.updateUserBook(currentUser.id || 1, bookId, updates)
        
        // Mettre à jour dans le store local
        const index = this.userLibrary.findIndex(book => book.book.id === bookId)
        if (index !== -1) {
          this.userLibrary[index] = updatedBook
        }
        
        return updatedBook
      } catch (error) {
        this.error = error.message
        console.error('Erreur lors de la mise à jour du livre:', error)
        throw error
      } finally {
        this.isLoading = false
      }
    },

    // === SESSIONS DE LECTURE ===
    
    // Create a new reading session
    async createReadingSession(userBookId, duration, pagesRead) {
      this.isLoading = true
      this.error = null
      
      try {
        const currentUser = JSON.parse(localStorage.getItem('auth_user'))
        if (!currentUser) throw new Error('Utilisateur non connecté')
        
        const session = await readingSessionService.createReadingSession(
          currentUser.id || 1, 
          userBookId, 
          duration, 
          pagesRead
        )
        
        this.readingSessions.push(session)
        return session
      } catch (error) {
        this.error = error.message
        console.error('Erreur lors de la création de la session:', error)
        throw error
      } finally {
        this.isLoading = false
      }
    },

    // Récupérer les sessions de lecture de l'utilisateur
    async fetchUserSessions() {
      this.isLoading = true
      this.error = null
      
      try {
        const currentUser = JSON.parse(localStorage.getItem('auth_user'))
        if (!currentUser) throw new Error('Utilisateur non connecté')
        
        const sessions = await readingSessionService.getUserSessions(currentUser.id || 1)
        this.readingSessions = sessions
        return sessions
      } catch (error) {
        this.error = error.message
        console.error('Erreur lors de la récupération des sessions:', error)
        throw error
      } finally {
        this.isLoading = false
      }
    },

    // Récupérer les statistiques de lecture
    async fetchReadingStats() {
      this.isLoading = true
      this.error = null
      
      try {
        const currentUser = JSON.parse(localStorage.getItem('auth_user'))
        if (!currentUser) throw new Error('Utilisateur non connecté')
        
        const stats = await readingSessionService.getReadingStats(currentUser.id || 1)
        // Les stats arrivent sous forme de string, on peut les parser si nécessaire
        return stats
      } catch (error) {
        this.error = error.message
        console.error('Erreur lors de la récupération des statistiques:', error)
        throw error
      } finally {
        this.isLoading = false
      }
    },

    // Effacer les erreurs
    clearError() {
      this.error = null
    }
  }
})
