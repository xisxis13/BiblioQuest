import { defineStore } from 'pinia'
import { authService } from '@/services/authService'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    loading: false,
    error: null,
  }),

  getters: {
    isAuthenticated: (state) => !(state.user === null),
    getError: (state) => state.error,
    isLoading: (state) => state.loading
  },

  actions: {
    setUser(user) {
      if (user != null) {
        this.user = {
          id: user.id,
          username: user.username,
          email: user.email,
          role: user.role,
          followersCount: user.followersCount,
          followingCount: user.followingCount,
          lastActivityAt: user.lastActivityAt
        }
      } else {
        this.user = null
      }
    },

    async login(credentials) {
      try {
        const response = await authService.login(credentials)
        this.setUser(response)
        console.log("user info", this.user)
        return response

      } catch (error) {
        return error
      }
    },

    async register(credentials) {
      try {
        const response = await authService.register(credentials)
        this.setUser(response)
        return response

      } catch (error) {
        return error
      }
    },

    getUser() {
      return {
        username: this.user?.username,
        email: this.user?.email,
        role: this.user?.role
      }
    }
  },

  logout() {
    this.setUser(null)
  }
})
