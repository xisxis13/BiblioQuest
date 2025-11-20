import axios from 'axios'
import { authService } from '@/services/authService'

const API_BASE = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' }
})

// Attach token from authService/localStorage if present
api.interceptors.request.use((config) => {
  try {
    const token = typeof authService.getToken === 'function' ? authService.getToken() : localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
  } catch (e) {
    // ignore
  }
  return config
})

function usernameHeader() {
  try {
    const user = typeof authService.getUser === 'function' ? authService.getUser() : null
    const username = user?.username || user?.email || user?.name
    return username ? { 'X-Username': username } : {}
  } catch (e) {
    return {}
  }
}

export const bookService = {
  list() {
    const headers = usernameHeader()
    return api.get('/books', { headers })
  },

  get(id) {
    const headers = usernameHeader()
    return api.get(`/books/${id}`, { headers })
  },

  add(book) {
    const headers = usernameHeader()
    return api.post('/books', book, { headers })
  },

  update(id, book) {
    const headers = usernameHeader()
    return api.put(`/books/${id}`, book, { headers })
  },

  remove(id) {
    const headers = usernameHeader()
    return api.delete(`/books/${id}`, { headers })
  }
}
