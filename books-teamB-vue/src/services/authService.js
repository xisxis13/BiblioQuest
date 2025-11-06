import axios from 'axios'

// Configuration API
const API_BASE_URL = 'http://localhost:3000/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Intercepteur pour ajouter le token JWT
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Intercepteur pour gérer les erreurs
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expiré - nettoyer et rediriger
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const authService = {
  // Connexion
  async login(credentials) {
    try {
      const response = await api.post('/auth/login', credentials)
      const { token, user } = response.data
      
      // Stocker les données d'authentification
      localStorage.setItem('auth_token', token)
      localStorage.setItem('auth_user', JSON.stringify(user))
      
      return { token, user }
    } catch (error) {
      const message = error.response?.data?.message || 'Erreur de connexion'
      throw new Error(message)
    }
  },

  // Inscription
  async register(userData) {
    try {
      const response = await api.post('/auth/register', userData)
      const { token, user } = response.data
      
      // Stocker les données d'authentification
      localStorage.setItem('auth_token', token)
      localStorage.setItem('auth_user', JSON.stringify(user))
      
      return { token, user }
    } catch (error) {
      const message = error.response?.data?.message || 'Erreur d\'inscription'
      throw new Error(message)
    }
  },

  // Déconnexion
  logout() {
    localStorage.removeItem('auth_token')
    localStorage.removeItem('auth_user')
  },

  // Vérifier l'authentification
  isAuthenticated() {
    return !!localStorage.getItem('auth_token')
  },

  // Récupérer le token
  getToken() {
    return localStorage.getItem('auth_token')
  },

  // Récupérer l'utilisateur
  getUser() {
    const user = localStorage.getItem('auth_user')
    return user ? JSON.parse(user) : null
  }
}
