import axios from 'axios'
import { authService } from '@/services/authService'

const API_BASE = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' }
})

// // Attach token from authService/localStorage if present
// api.interceptors.request.use((config) => {
//   try {
//     const token = typeof authService.getToken === 'function' ? authService.getToken() : localStorage.getItem('auth_token')
//     if (token) {
//       config.headers.Authorization = `Bearer ${token}`
//     }
//   } catch (e) {
//     // ignore

//   }
//   return config
// })

export const bookService = {
  async getAll() {
    return api.get('/books')
  },

  async getById(id) {
    return api.get(`/books/${id}`)
  },

  async add(book) {
    return api.post('/books', book)
  },

  async update(id, book) {
    return api.put(`/books/${id}`, book)
  },

  async delete(id) {
    return api.delete(`/books/${id}`)
  }
}

export default bookService
