import { defineStore } from 'pinia'
import { authService } from '@/services/authService'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    loading: false,
    error: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    getUser: (state) => state.user,
    getError: (state) => state.error,
    isLoading: (state) => state.loading
  },

  actions: {
    // Initialiser avec les données localStorage
    initializeAuth() {
      const token = authService.getToken()
      const user = authService.getUser()
      
      if (token && user) {
        this.token = token
        this.user = user
      }
    },

    // Connexion
    async login(credentials) {
      console.log('AuthStore login called with:', credentials)
      this.loading = true
      this.error = null
      
      try {
        const result = await authService.login(credentials)
        console.log('AuthService result:', result)
        
        const { user } = result
        this.token = 'simple-auth' // Token simple pour l'instant
        this.user = user
        
        console.log('User set in store:', this.user)
        return { success: true }
      } catch (error) {
        console.error('Login error in authStore:', error)
        this.error = error.message
        return { success: false, error: error.message }
      } finally {
        this.loading = false
      }
    },

    // Inscription
    async register(userData) {
      this.loading = true
      this.error = null
      
      try {
        const { user } = await authService.register(userData)
        this.token = 'simple-auth' // Token simple pour l'instant
        this.user = user
        return { success: true }
      } catch (error) {
        this.error = error.message
        return { success: false, error: error.message }
      } finally {
        this.loading = false
      }
    },

    // Déconnexion
    logout() {
      authService.logout()
      this.token = null
      this.user = null
      this.error = null
    },

    // Nettoyer les erreurs
    clearError() {
      this.error = null
    }
  }
})
