import { defineStore } from 'pinia'
import userBookService from '@/services/userBookService'

export const useUserBookStore = defineStore('userBooks', {
  state: () => ({
    userBooks: [],
    loading: false,
    error: null
  }),

  getters: {
    allUserBooks: (state) => state.userBooks,
    isLoading: (state) => state.loading,
    getError: (state) => state.error
  },

  actions: {
    async getAllBooks(userId) {
      this.loading = true
      this.error = null
      try {
        const data = await userBookService.getAllBooks(userId)
        this.userBooks = data || []
        return this.userBooks
      } catch (error) {
        console.error('Erreur lors de la récupération des userBooks:', error)
        this.error = error.response?.data?.message || error.message || 'Erreur lors du chargement des livres utilisateur'
        throw error
      } finally {
        this.loading = false
      }
    },

    async addBook(userId, bookId) {
      this.loading = true
      this.error = null
      try {
        const added = await userBookService.addBook(userId, bookId)
        if (added) this.userBooks.push(added)
        return added
      } catch (error) {
        console.error('Erreur lors de l\'ajout du livre à la bibliothèque:', error)
        this.error = error.response?.data?.message || error.message || 'Erreur lors de l\'ajout du livre'
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteBook(userId, bookId) {
      this.loading = true
      this.error = null
      try {
        const res = await userBookService.deleteBook(userId, bookId)
        this.userBooks = this.userBooks.filter(b => {
          return !(b.bookId === bookId || b.id === bookId || (b.book && b.book.id === bookId))
        })
        return res
      } catch (error) {
        console.error('Erreur lors de la suppression du livre de la bibliothèque:', error)
        this.error = error.response?.data?.message || error.message || 'Erreur lors de la suppression du livre'
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateReadingStatus(userId, bookId, status) {
      this.loading = true
      this.error = null
      try {
        const updated = await userBookService.updateReadingStatus(userId, bookId, status)
        const idx = this.userBooks.findIndex(b => b.bookId === bookId || b.id === bookId || (b.book && b.book.id === bookId))
        if (idx !== -1) {
          // remplacer l'élément local par la réponse du backend si disponible
          this.userBooks[idx] = updated || { ...this.userBooks[idx], status }
        }
        return updated
      } catch (error) {
        console.error('Erreur lors de la mise à jour du statut de lecture:', error)
        this.error = error.response?.data?.message || error.message || 'Erreur lors de la mise à jour du statut'
        throw error
      } finally {
        this.loading = false
      }
    },

    clearError() {
      this.error = null
    }
  }
})
