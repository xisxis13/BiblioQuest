import axios from 'axios'


const api = axios.create({
  baseURL: import.meta.env.PROD
    ? '/api'
    : 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

export const authService = {
  async login(credentials) {
    const response = await api.post('/auth/login', credentials)
    return response.data
  },

  async register(credentials) {
    const response = await api.post('/auth/register', credentials)
    return response.data
  }
}
