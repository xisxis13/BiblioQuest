import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.PROD
    ? '/api'
    : 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

export default {
  /**
   * Récupère un utilisateur par son ID
   */
  async getUserById(userId) {
    const response = await api.get(`/users/${userId}`)
    return response.data
  },

  /**
   * Récupère tous les utilisateurs
   */
  async getAllUsers() {
    const response = await api.get('/users')
    return response.data
  }
}
