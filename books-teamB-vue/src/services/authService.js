import axios from 'axios'


const api = axios.create({
  baseURL: import.meta.env.PROD
    ? '/api/events'
    : 'http://localhost:8080/api/events',
  headers: {
    'Content-Type': 'application/json'
  }
})

export const authService = {
  async login(credentials) {
    console.log("PROD env variable", import.meta.env.PROD)
    console.log("VUE_APP_SERVER_URL", import.meta.env.VUE_APP_SERVER_URL)
    const response = await api.post('/auth/login', credentials)
    return response.data
  },

  async register(credentials) {
    const response = await api.post('/auth/register', credentials)
    return response.data
  }
}
