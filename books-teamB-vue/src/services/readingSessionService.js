import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.PROD
    ? '/api'
    : 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

export const readingSessionService = {

  async addSession(readingSessionDTO) {
    const response = await api.post(`/reading-sessions/`, readingSessionDTO)
    return response.data
  },

  async getSessions(userId) {
    try {
      const response = await api.get(`/reading-sessions?userId=${userId}`)
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
      
      const response = await api.put(`/reading-sessions/${sessionId}?${params}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la mise à jour de la session:', error)
      throw error
    }
  },
}

export default readingSessionService
