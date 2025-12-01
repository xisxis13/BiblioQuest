import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

// Créer une instance axios
const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Intercepteur pour ajouter le token JWT à toutes les requêtes
apiClient.interceptors.request.use(
  config => {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// Intercepteur pour gérer les erreurs d'authentification
apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default {
  // Récupérer tous les livres disponibles (pour le catalogue)
  async getAllBooks() {
    const response = await apiClient.get('/books')
    return response.data
  },

  // Récupérer le catalogue public (sans authentification)
  async getCatalogBooks(filters = {}) {
    const params = new URLSearchParams()
    if (filters.author) params.append('author', filters.author)
    if (filters.subject) params.append('subject', filters.subject)
    if (filters.title) params.append('title', filters.title)
    if (filters.year) params.append('year', filters.year)
    params.append('page', filters.page || 0)
    params.append('size', filters.size || 100)

    const response = await apiClient.get(`/catalog/search?${params.toString()}`)
    return response.data
  },

  // Rechercher des livres
  async searchBooks(query) {
    const response = await apiClient.get(`/books/search?query=${encodeURIComponent(query)}`)
    return response.data
  },

  // Récupérer un livre par ID
  async getBookById(id) {
    const response = await apiClient.get(`/catalog/${id}`)
    return response.data
  },

  // Ajouter un nouveau livre (auteur)
  async addBook(bookData) {
    const response = await apiClient.post('/books/add', bookData)
    return response.data
  },

  async updateBook(bookId, bookData) {
    const response = await apiClient.put(`/books/${bookId}`, bookData)
    return response.data
  }
}
