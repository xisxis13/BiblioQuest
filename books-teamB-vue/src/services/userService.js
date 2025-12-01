import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

// Configuration axios avec token
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
})

// Intercepteur pour ajouter le token d'authentification
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

// Service pour récupérer les utilisateurs
export const userService = {
  // Récupérer tous les utilisateurs
  async getAllUsers() {
    try {
      const response = await apiClient.get('/users')
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des utilisateurs:', error)
      throw error
    }
  },

  // Récupérer un utilisateur par ID
  async getUserById(id) {
    try {
      const response = await apiClient.get(`/users/${id}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération de l\'utilisateur:', error)
      throw error
    }
  },

  // Rechercher des utilisateurs
  async searchUsers(query) {
    try {
      const response = await apiClient.get(`/users/search?q=${encodeURIComponent(query)}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la recherche d\'utilisateurs:', error)
      throw error
    }
  }
}

export default userService
