import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const getAuthToken = () => {
  const tokenData = localStorage.getItem('auth_token')
  if (tokenData) {
    try {
      const parsed = JSON.parse(tokenData)
      return parsed.token || tokenData
    } catch (e) {
      return tokenData
    }
  }
  return null
}

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(
  (config) => {
    const token = getAuthToken()
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
  },
  (error) => Promise.reject(error)
)

const userBookService = {
  async getAllBooks(userId) {
    try {
      const response = await api.get(`/users/${userId}/library`)
      return response.data
    } catch (error) {
      console.error('userBookService.getAllBooks error', error)
      throw error
    }
  },

  async addBook(userId, bookId) {
    try {
      const response = await api.post(`/users/${userId}/books?bookId=${bookId}`)
      return response.data
    } catch (error) {
      console.error('userBookService.addBook error', error)
      throw error
    }
  },

  async deleteBook(userId, bookId) {
    try {
      const response = await api.delete(`/users/${userId}/books/${bookId}`)
      return response.data
    } catch (error) {
      console.error('userBookService.deleteBook error', error)
      throw error
    }
  },

  async updateReadingStatus(userId, bookId, readingStatus) {
    try {
      const params = new URLSearchParams()
      if (readingStatus !== undefined && readingStatus !== null) params.append('status', readingStatus)
      const response = await api.put(`/users/${userId}/books/${bookId}?${params}`)
      return response.data
    } catch (error) {
      console.error('userBookService.updateReadingStatus error', error)
      throw error
    }
  }
}

export default userBookService
