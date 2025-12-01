import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api/profile'

class ProfileService {
  constructor() {
    this.api = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json'
      }
    })

    // Ajouter l'intercepteur pour inclure le token JWT
    this.api.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('auth_token')
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // Intercepteur pour gérer les erreurs d'authentification
    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          localStorage.removeItem('auth_token')
          localStorage.removeItem('auth_user')
          window.location.href = '/login'
        }
        return Promise.reject(error)
      }
    )
  }

  async getCurrentProfile(userId) {
    try {
      const response = await this.api.get(`/${userId}`)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération du profil:', error)
      throw error
    }
  }

  async updateProfile(profileData) {
    try {
      const response = await this.api.put('/', profileData)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la mise à jour du profil:', error)
      throw error
    }
  }

  async uploadAvatar(file) {
    try {
      const formData = new FormData()
      formData.append('avatar', file)
      
      const response = await this.api.post('/avatar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de l\'upload de l\'avatar:', error)
      throw error
    }
  }

  async removeAvatar() {
    try {
      const response = await this.api.delete('/avatar')
      return response.data
    } catch (error) {
      console.error('Erreur lors de la suppression de l\'avatar:', error)
      throw error
    }
  }

  async changePassword(passwordData) {
    try {
      await this.api.post('/change-password', passwordData)
    } catch (error) {
      console.error('Erreur lors du changement de mot de passe:', error)
      throw error
    }
  }
}

export const profileService = new ProfileService()
export default profileService
