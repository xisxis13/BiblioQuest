import { defineStore } from 'pinia'
import userService from '@/services/userService'

export const useUserStore = defineStore('user', {
  state: () => ({
    users: [],
    currentUser: null,
    loading: false,
    error: null
  }),

  getters: {
    getAllUsers: (state) => state.users,
    getCurrentUser: (state) => state.currentUser,
    isLoading: (state) => state.loading,
    getError: (state) => state.error
  },

  actions: {
    async getById(userId) {
      this.loading = true
      this.error = null
      
      try {
        const user = await userService.getUserById(userId)
        this.currentUser = user
        return user
      } catch (error) {
        console.error('Error fetching user by ID:', error)
        this.error = error.message || 'Error retrieving user'
        throw error
      } finally {
        this.loading = false
      }
    },

    async getAll() {
      this.loading = true
      this.error = null
      
      try {
        const users = await userService.getAllUsers()
        this.users = users
        return users
      } catch (error) {
        console.error('Error fetching all users:', error)
        this.error = error.message || 'Error retrieving users'
        throw error
      } finally {
        this.loading = false
      }
    }
  }
})
