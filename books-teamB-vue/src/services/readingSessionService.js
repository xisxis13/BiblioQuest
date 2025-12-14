import axios from 'axios'

// Configuration API - Backend Spring Boot
const API_BASE_URL = 'http://localhost:8080/api'

export const readingSessionService = {

  async addSession(readingSessionDTO) {
    const response = await axios.post(`${API_BASE_URL}/reading-sessions/`, readingSessionDTO)
    return response.data
  },

  async getSessions(userId) {
    try {
      const response = await axios.get(`${API_BASE_URL}/reading-sessions?userId=${userId}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des sessions du user:', error)
      throw error
    }
  },

  async updateReadingSession(sessionId, updates) {
    try {
      const params = new URLSearchParams()
      if (updates.duration !== undefined) params.append('duration', updates.duration)
      if (updates.pagesRead !== undefined) params.append('pagesRead', updates.pagesRead)
      if (updates.notes) params.append('notes', updates.notes)
      
      const response = await axios.put(`${API_BASE_URL}/reading-sessions/${sessionId}?${params}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la mise à jour de la session:', error)
      throw error
    }
  },
}

export default readingSessionService
