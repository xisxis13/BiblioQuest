
import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'

const API_BASE_URL = 'http://localhost:8080/api'

const getCurrentUsername = () => {
  return localStorage.getItem('currentUsername') || 'admin'
}

const reviewService = {
  // Récupérer les avis d'un livre
  async getBookReviews(bookId) {
    const username = getCurrentUsername()
    const response = await axios.get(`${API_BASE_URL}/books/${bookId}/reviews`, {
      headers: { 'X-Username': username }
    })
    return response.data
  },

  // Ajouter un avis
  async addReview(bookId, review) {
    const authStore = useAuthStore()
    const userId = authStore.getUser().id
    console.log("review", review)
    review.userBookDTO = { userId: userId, bookId: bookId }
    console.log("review", review)
    const response = await axios.post(`${API_BASE_URL}/books/reviews/update`, review, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    return response.data
  },

  // Supprimer un avis
  async deleteReview(bookId) {
    const username = getCurrentUsername()
    await axios.delete(`${API_BASE_URL}/books/${bookId}/reviews`, {
      headers: { 'X-Username': username }
    })
  },

  // Modifier un avis (PATCH ou PUT selon l'API, ici POST pour cohérence avec backend)
  async updateReview(bookId, review) {
    const username = getCurrentUsername()
    const response = await axios.post(`${API_BASE_URL}/books/reviews/update`, review, {
      headers: {
        'X-Username': username,
        'Content-Type': 'application/json'
      }
    })
    return response.data
  },
  //TODO CORRIGER PLUS TARD
  // getReviewsStats(reviews){
  // const average = reviews => reviews.reduce((a, b) => a.rating + b.rating) / reviews.length;
  // console.log("average review rating", average)
  // }
}

export default reviewService
