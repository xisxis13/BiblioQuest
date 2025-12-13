import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_SERVER_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const authService = {
  async login(credentials) {
    console.log("VITE_APP_SERVER_URL", import.meta.env.VITE_SERVER_URL)
    console.log("VUE_APP_SERVER_URL", import.meta.env.VUE_APP_SERVER_URL)

    console.log("chosed server url", API_BASE_URL)
    const response = await api.post('/auth/login', credentials)
    return response.data
  },

  async register(credentials) {
    const response = await api.post('/auth/register', credentials)
    return response.data
  }
}
