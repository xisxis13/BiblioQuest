import { defineStore } from 'pinia'
import booksService from '@/services/booksService'

export const useBooksStore = defineStore('books', {
  state: () => ({
    books: [],
    loading: false,
    error: null
  }),

  getters: {
    // Obtenir tous les livres
    allBooks: (state) => state.books,

    // Livres par statut
    toReadBooks: (state) => state.books.filter(book => book.status === 'TO_READ'),
    inProgressBooks: (state) => state.books.filter(book => book.status === 'IN_PROGRESS'),
    completedBooks: (state) => state.books.filter(book => book.status === 'COMPLETED'),

    // Statistiques
    stats: (state) => ({
      total: state.books.length,
      toRead: state.books.filter(b => b.status === 'TO_READ').length,
      inProgress: state.books.filter(b => b.status === 'IN_PROGRESS').length,
      completed: state.books.filter(b => b.status === 'COMPLETED').length
    }),

    // État de chargement et d'erreur
    isLoading: (state) => state.loading,
    getError: (state) => state.error
  },

  actions: {
    // Récupérer tous les livres de l'utilisateur
    async fetchUserBooks() {
      this.loading = true
      this.error = null
      try {
        this.books = await booksService.getUserBooks()
      } catch (error) {
        console.error('Erreur lors de la récupération des livres:', error)
        this.error = error.response?.data?.message || 'Erreur lors du chargement des livres'
        throw error
      } finally {
        this.loading = false
      }
    },

    // Ajouter un nouveau livre
    async addBook(bookData) {
      this.loading = true
      this.error = null
      try {
        const newBook = await booksService.addBook(bookData)
        this.books.push(newBook)
        return newBook
      } catch (error) {
        console.error("Erreur lors de l'ajout du livre:", error)
        this.error = error.response?.data?.message || "Erreur lors de l'ajout du livre"
        throw error
      } finally {
        this.loading = false
      }
    },

    // Mettre à jour un livre
    async updateBook(id, bookData) {
      this.loading = true
      this.error = null
      try {
        const updatedBook = await booksService.updateBook(id, bookData)
        const index = this.books.findIndex(book => book.id === id)
        if (index !== -1) {
          this.books[index] = updatedBook
        }
        return updatedBook
      } catch (error) {
        console.error('Erreur lors de la mise à jour du livre:', error)
        this.error = error.response?.data?.message || 'Erreur lors de la mise à jour du livre'
        throw error
      } finally {
        this.loading = false
      }
    },

    // Supprimer un livre
    async deleteBook(id) {
      this.loading = true
      this.error = null
      try {
        await booksService.deleteBook(id)
        this.books = this.books.filter(book => book.id !== id)
      } catch (error) {
        console.error('Erreur lors de la suppression du livre:', error)
        this.error = error.response?.data?.message || 'Erreur lors de la suppression du livre'
        throw error
      } finally {
        this.loading = false
      }
    },

    // Mettre à jour le statut d'un livre
    async updateBookStatus(id, status) {
      this.loading = true
      this.error = null
      try {
        const updatedBook = await booksService.updateBookStatus(id, status)
        const index = this.books.findIndex(book => book.id === id)
        if (index !== -1) {
          this.books[index] = { ...this.books[index], status }
        }
        return updatedBook
      } catch (error) {
        console.error('Erreur lors de la mise à jour du statut:', error)
        this.error = error.response?.data?.message || 'Erreur lors de la mise à jour du statut'
        throw error
      } finally {
        this.loading = false
      }
    },

    // Effacer les erreurs
    clearError() {
      this.error = null
    }
  }
})
