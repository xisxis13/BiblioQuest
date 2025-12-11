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
    getError: (state) => state.error,
    isLoading: (state) => state.loading
  },

  actions: {
    async login(credentials) {
      const response = await authService.login(credentials)
      return response
    },

    async register(credentials) {
      const response = await authService.register(credentials)
      return response
    },

    getUser() {
      return {
        username: this.user?.username,
        email: this.user?.email,
        role: this.user?.role
      }
    }
  }
})
