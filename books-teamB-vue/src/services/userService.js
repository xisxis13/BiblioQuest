import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

export default {
  /**
   * Récupère un utilisateur par son ID
   */
  async getUserById(userId) {
    const response = await apiClient.get(`/users/${userId}`)
    return response.data
  },

  /**
   * Récupère tous les utilisateurs
   */
  async getAllUsers() {
    const response = await apiClient.get('/users')
    return response.data
  }
}
