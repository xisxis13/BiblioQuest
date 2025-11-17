import axios from 'axios'

// Configuration API - Backend Spring Boot
const API_BASE_URL = 'http://localhost:8080/api'

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
      // Mapper email vers username pour le backend
      const loginData = {
        username: credentials.email,
        password: credentials.password
      }
      console.log('AuthService: Calling backend with:', loginData)
      const response = await api.post('/auth/login', loginData)
      console.log('AuthService: Backend response:', response.data)
      
      const user = response.data
      
      // Créer un token de session qui expire dans 15 minutes
      const sessionToken = {
        token: 'session_' + Date.now(),
        expiresAt: Date.now() + (15 * 60 * 1000) // 15 minutes
      }
      
      // Stocker les données d'authentification
      localStorage.setItem('auth_token', JSON.stringify(sessionToken))
      localStorage.setItem('auth_user', JSON.stringify(user))
      
      return { user, token: sessionToken.token }
    } catch (error) {
      console.error('AuthService login error:', error)
      const message = error.response?.data || 'Erreur de connexion'
      throw new Error(message)
    }
  },

  // Inscription
  async register(userData) {
    try {
      const response = await api.post('/auth/register', userData)
      const user = response.data
      
      // Créer un token de session qui expire dans 15 minutes
      const sessionToken = {
        token: 'session_' + Date.now(),
        expiresAt: Date.now() + (15 * 60 * 1000) // 15 minutes
      }
      
      // Stocker les données d'authentification
      localStorage.setItem('auth_token', JSON.stringify(sessionToken))
      localStorage.setItem('auth_user', JSON.stringify(user))
      
      return { user, token: sessionToken.token }
    } catch (error) {
      const message = error.response?.data || 'Erreur d\'inscription'
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
    const tokenData = this.getToken()
    if (!tokenData) return false
    
    // Vérifier si le token n'est pas expiré
    if (tokenData.expiresAt && Date.now() > tokenData.expiresAt) {
      this.logout()
      return false
    }
    
    return true
  },

  // Récupérer le token
  getToken() {
    const token = localStorage.getItem('auth_token')
    if (!token) return null
    
    try {
      return JSON.parse(token)
    } catch {
      return null
    }
  },

  // Récupérer l'utilisateur
  getUser() {
    const user = localStorage.getItem('auth_user')
    return user ? JSON.parse(user) : null
  }
}
