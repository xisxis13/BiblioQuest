import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

// Instance Axios avec configuration
const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Intercepteur pour ajouter le token JWT
apiClient.interceptors.request.use(
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

// Intercepteur pour gérer les erreurs
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expiré ou invalide
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const booksService = {
  // Récupérer la bibliothèque d'un utilisateur
  getUserBooks(userId) {
    return apiClient.get(`/users/${userId}/library`)
  },

  // Ajouter un livre à la bibliothèque
  addBook(userId, bookData) {
    return apiClient.post(`/users/${userId}/books`, bookData)
  },

  // Mettre à jour un livre
  updateBook(userId, bookId, bookData) {
    return apiClient.put(`/users/${userId}/books/${bookId}`, bookData)
  },

  // Supprimer un livre
  deleteBook(userId, bookId) {
    return apiClient.delete(`/users/${userId}/books/${bookId}`)
  },

  // Rechercher des livres
  searchBooks(query) {
    return apiClient.get('/books/search', { params: { query } })
  },

  // Récupérer un livre par ID
  getBookById(bookId) {
    return apiClient.get(`/books/${bookId}`)
  },

  // Mettre à jour la progression de lecture
  updateProgress(userId, bookId, progress) {
    return apiClient.patch(`/users/${userId}/books/${bookId}/progress`, { progress })
  }
}

export default booksService
