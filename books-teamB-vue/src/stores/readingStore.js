import { defineStore } from 'pinia'

export const useReadingStore = defineStore('reading', {
  state: () => ({
    readingSessions: [],
    readingNotes: [],
    readingGoals: [],
    currentProgress: {},
    isLoading: false,
    error: null
  }),

  getters: {
    // Get reading sessions for a specific book
    getSessionsByBook: (state) => (bookId) => {
      return state.readingSessions.filter(session => session.bookId === bookId)
    },

    // Get notes for a specific book
    getNotesByBook: (state) => (bookId) => {
      return state.readingNotes.filter(note => note.bookId === bookId)
    },

    // Get current progress for a book
    getProgressByBook: (state) => (bookId) => {
      return state.currentProgress[bookId] || { currentPage: 0, percentage: 0 }
    },

    // Get all active reading goals
    getActiveGoals: (state) => {
      return state.readingGoals.filter(goal => goal.status === 'active')
    },

    // Calculate total pages read
    getTotalPagesRead: (state) => {
      return state.readingSessions.reduce((total, session) => total + session.pagesRead, 0)
    }
  },

  actions: {
    // Create a new reading session
    async createReadingSession(sessionData) {
      this.isLoading = true
      this.error = null
      
      try {
        // API call to create session
        // const response = await axios.post('/api/reading-sessions', sessionData)
        
        // Mock implementation
        const newSession = {
          id: Date.now(),
          ...sessionData,
          createdAt: new Date().toISOString()
        }
        
        this.readingSessions.push(newSession)
        
        // Update progress
        this.updateProgress(sessionData.bookId, sessionData.currentPage, sessionData.totalPages)
        
        return { success: true, session: newSession }
      } catch (error) {
        this.error = error.message
        return { success: false, error: error.message }
      } finally {
        this.isLoading = false
      }
    },

    // Create a reading note
    async createReadingNote(noteData) {
      this.isLoading = true
      this.error = null
      
      try {
        // API call to create note
        // const response = await axios.post('/api/reading-notes', noteData)
        
        // Mock implementation
        const newNote = {
          id: Date.now(),
          ...noteData,
          createdAt: new Date().toISOString()
        }
        
        this.readingNotes.push(newNote)
        
        return { success: true, note: newNote }
      } catch (error) {
        this.error = error.message
        return { success: false, error: error.message }
      } finally {
        this.isLoading = false
      }
    },

    // Create a reading goal
    async createReadingGoal(goalData) {
      this.isLoading = true
      this.error = null
      
      try {
        // API call to create goal
        // const response = await axios.post('/api/reading-goals', goalData)
        
        // Mock implementation
        const newGoal = {
          id: Date.now(),
          ...goalData,
          status: 'active',
          progress: 0,
          createdAt: new Date().toISOString()
        }
        
        this.readingGoals.push(newGoal)
        
        return { success: true, goal: newGoal }
      } catch (error) {
        this.error = error.message
        return { success: false, error: error.message }
      } finally {
        this.isLoading = false
      }
    },

    // Update reading progress for a book
    updateProgress(bookId, currentPage, totalPages) {
      const percentage = totalPages > 0 ? Math.round((currentPage / totalPages) * 100) : 0
      
      this.currentProgress[bookId] = {
        currentPage,
        totalPages,
        percentage
      }
    },

    // Update goal progress
    updateGoalProgress(goalId, progress) {
      const goal = this.readingGoals.find(g => g.id === goalId)
      if (goal) {
        goal.progress = progress
        if (progress >= goal.target) {
          goal.status = 'completed'
        }
      }
    },

    // Delete a reading note
    async deleteNote(noteId) {
      this.isLoading = true
      this.error = null
      
      try {
        // API call to delete note
        // await axios.delete(`/api/reading-notes/${noteId}`)
        
        const index = this.readingNotes.findIndex(note => note.id === noteId)
        if (index > -1) {
          this.readingNotes.splice(index, 1)
        }
        
        return { success: true }
      } catch (error) {
        this.error = error.message
        return { success: false, error: error.message }
      } finally {
        this.isLoading = false
      }
    },

    // Fetch user's reading stats
    async fetchReadingStats(userId) {
      this.isLoading = true
      this.error = null
      
      try {
        // API call to fetch stats
        // const response = await axios.get(`/api/users/${userId}/stats`)
        
        // Mock data
        return {
          success: true,
          stats: {
            totalBooksRead: 0,
            totalPagesRead: this.getTotalPagesRead,
            averagePagesPerDay: 0,
            currentStreak: 0,
            longestStreak: 0
          }
        }
      } catch (error) {
        this.error = error.message
        return { success: false, error: error.message }
      } finally {
        this.isLoading = false
      }
    },

    // Clear all reading data
    clearReadingData() {
      this.readingSessions = []
      this.readingNotes = []
      this.readingGoals = []
      this.currentProgress = {}
      this.error = null
    },

    // Clear error
    clearError() {
      this.error = null
    }
  }
})
