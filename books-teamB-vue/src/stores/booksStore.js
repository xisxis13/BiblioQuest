import { defineStore } from 'pinia'
import { booksService } from '@/services/booksService'

export const useBooksStore = defineStore('books', {
  state: () => ({
    books: [],
    currentBook: null,
    isLoading: false,
    error: null
  }),

  getters: {
    // Livres par statut
    toReadBooks: (state) => state.books.filter(book => book.status === 'TO_READ'),
    inProgressBooks: (state) => state.books.filter(book => book.status === 'IN_PROGRESS'),
    completedBooks: (state) => state.books.filter(book => book.status === 'COMPLETED'),

    // Statistiques
    totalBooks: (state) => state.books.length,
    booksCount: (state) => ({
      total: state.books.length,
      toRead: state.books.filter(b => b.status === 'TO_READ').length,
      inProgress: state.books.filter(b => b.status === 'IN_PROGRESS').length,
      completed: state.books.filter(b => b.status === 'COMPLETED').length
    }),

    // Livre par ID
    getBookById: (state) => (id) => state.books.find(book => book.id === id)
  },

  actions: {
    // Récupérer tous les livres de l'utilisateur
    async fetchUserBooks() {
      this.isLoading = true
      this.error = null
      
      try {
        const user = JSON.parse(localStorage.getItem('auth_user'))
        if (!user) throw new Error('Utilisateur non connecté')

        const response = await booksService.getUserBooks(user.id)
        this.books = response.data || []
        return { success: true }
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Erreur lors du chargement des livres'
        console.error('Erreur fetchUserBooks:', error)
        return { success: false, error: this.error }
      } finally {
        this.isLoading = false
      }
    },

    // Ajouter un livre
    async addBook(bookData) {
      this.isLoading = true
      this.error = null

      try {
        const user = JSON.parse(localStorage.getItem('auth_user'))
        if (!user) throw new Error('Utilisateur non connecté')

        const response = await booksService.addBook(user.id, bookData)
        this.books.push(response.data)
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message || "Erreur lors de l'ajout du livre"
        console.error('Erreur addBook:', error)
        return { success: false, error: this.error }
      } finally {
        this.isLoading = false
      }
    },

    // Mettre à jour un livre
    async updateBook(bookId, bookData) {
      this.isLoading = true
      this.error = null

      try {
        const user = JSON.parse(localStorage.getItem('auth_user'))
        if (!user) throw new Error('Utilisateur non connecté')

        const response = await booksService.updateBook(user.id, bookId, bookData)
        const index = this.books.findIndex(b => b.id === bookId)
        if (index !== -1) {
          this.books[index] = response.data
        }
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Erreur lors de la mise à jour du livre'
        console.error('Erreur updateBook:', error)
        return { success: false, error: this.error }
      } finally {
        this.isLoading = false
      }
    },

    // Supprimer un livre
    async deleteBook(bookId) {
      this.isLoading = true
      this.error = null

      try {
        const user = JSON.parse(localStorage.getItem('auth_user'))
        if (!user) throw new Error('Utilisateur non connecté')

        await booksService.deleteBook(user.id, bookId)
        this.books = this.books.filter(b => b.id !== bookId)
        return { success: true }
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Erreur lors de la suppression du livre'
        console.error('Erreur deleteBook:', error)
        return { success: false, error: this.error }
      } finally {
        this.isLoading = false
      }
    },

    // Mettre à jour le statut d'un livre
    async updateBookStatus(bookId, newStatus) {
      this.isLoading = true
      this.error = null

      try {
        const user = JSON.parse(localStorage.getItem('auth_user'))
        if (!user) throw new Error('Utilisateur non connecté')

        const book = this.books.find(b => b.id === bookId)
        if (!book) throw new Error('Livre non trouvé')

        const updatedData = { ...book, status: newStatus }
        const response = await booksService.updateBook(user.id, bookId, updatedData)
        
        const index = this.books.findIndex(b => b.id === bookId)
        if (index !== -1) {
          this.books[index] = response.data
        }
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Erreur lors de la mise à jour du statut'
        console.error('Erreur updateBookStatus:', error)
        return { success: false, error: this.error }
      } finally {
        this.isLoading = false
      }
    },

    // Définir le livre actuel
    setCurrentBook(book) {
      this.currentBook = book
    },

    // Nettoyer les erreurs
    clearError() {
      this.error = null
    },

    // Réinitialiser le store
    resetStore() {
      this.books = []
      this.currentBook = null
      this.isLoading = false
      this.error = null
    }
  }
})
