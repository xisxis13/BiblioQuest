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
      this.loading = true
      this.error = null
      
      try {
        const { token, user } = await authService.login(credentials)
        this.token = token
        this.user = user
        return { success: true }
      } catch (error) {
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
        const { token, user } = await authService.register(userData)
        this.token = token
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
