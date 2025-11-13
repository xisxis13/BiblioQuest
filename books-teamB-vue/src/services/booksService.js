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
  // Récupérer tous les livres de l'utilisateur
  async getUserBooks() {
    const response = await apiClient.get('/books')
    return response.data
  },

  // Récupérer un livre par ID
  async getBookById(id) {
    const response = await apiClient.get(`/books/${id}`)
    return response.data
  },

  // Ajouter un nouveau livre
  async addBook(bookData) {
    const response = await apiClient.post('/books', bookData)
    return response.data
  },

  // Mettre à jour un livre
  async updateBook(id, bookData) {
    const response = await apiClient.put(`/books/${id}`, bookData)
    return response.data
  },

  // Supprimer un livre
  async deleteBook(id) {
    const response = await apiClient.delete(`/books/${id}`)
    return response.data
  },

  // Mettre à jour le statut d'un livre
  async updateBookStatus(id, status) {
    const response = await apiClient.patch(`/books/${id}/status`, { status })
    return response.data
  }
}
