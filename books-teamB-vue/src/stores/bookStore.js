import { defineStore } from 'pinia'
import bookService from '@/services/bookService'

export const useBooksStore = defineStore('books', {
  state: () => ({
    books: [],
    loading: false,
    error: null
  }),

  getters: {
    // Obtenir tous les livres
    allBooks: (state) => state.books,

    toReadBooks: (state) => state.books.filter(book => book.status === 'TO_READ'),
    inProgressBooks: (state) => state.books.filter(book => book.status === 'IN_PROGRESS'),
    completedBooks: (state) => state.books.filter(book => book.status === 'COMPLETED'),

    stats: (state) => ({
      total: state.books.length,
      toRead: state.books.filter(b => b.status === 'TO_READ').length,
      inProgress: state.books.filter(b => b.status === 'IN_PROGRESS').length,
      completed: state.books.filter(b => b.status === 'COMPLETED').length
    }),

    isLoading: (state) => state.loading,
    getError: (state) => state.error
  },

  actions: {
    // Récupérer tous les livres disponibles (catalogue)
    async getAll() {
      this.loading = true
      this.error = null
      try {
        this.books = await bookService.getAllBooks()
      } catch (error) {
        console.error('Erreur lors de la récupération des livres:', error)
        this.error = error.response?.data?.message || 'Erreur lors du chargement des livres'
        throw error
      } finally {
        this.loading = false
      }
    },

    // Récupérer un livre par ID
    async getById(id) {
      this.loading = true
      this.error = null
      try {
        return await bookService.getBookById(id)
      } catch (error) {
        console.error('Erreur lors de la récupération du livre:', error)
        this.error = error.response?.data?.message || 'Erreur lors du chargement du livre'
        throw error
      } finally {
        this.loading = false
      }
    },

    // Ajouter un nouveau livre
    async add(bookData) {
      this.loading = true
      this.error = null
      try {
        const newBook = await bookService.addBook(bookData)
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
    async update(id, bookData) {
      this.loading = true
      this.error = null
      try {
        const updatedBook = await bookService.updateBook(id, bookData)
        const index = this.books.findIndex(book => book.id === id)
        if (index !== -1) {
          this.books[index] = updatedBook
        }
        return updatedBookerror
      } catch () {
        console.error('Erreur lors de la mise à jour du livre:', error)
        this.error = error.response?.data?.message || 'Erreur lors de la mise à jour du livre'
        throw error
      } finally {
        this.loading = false
      }
    },

    // Supprimer un livre
    async delete(id) {
      this.loading = true
      this.error = null
      try {
        await bookService.deleteBook(id)
        this.books = this.books.filter(book => book.id !== id)
      } catch (error) {
        console.error('Erreur lors de la suppression du livre:', error)
        this.error = error.response?.data?.message || 'Erreur lors de la suppression du livre'
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
