
import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'

const API_BASE_URL = 'http://localhost:8080/api'

const reviewService = {
  // Récupérer les avis d'un livre
  async getBookReviews(bookId) {
    const response = await axios.get(`${API_BASE_URL}/books/${bookId}/reviews`,)
    return response.data
  },

  // Ajouter un avis
  async addReview(bookId, review) {
    const authStore = useAuthStore()
    const userId = authStore.getUser().id

    review.userBookDTO = { userId: userId, bookId: bookId }

    const response = await axios.post(`${API_BASE_URL}/books/reviews/update`, review,)
    return response.data
  },

  async deleteReview(bookId) {
    const authStore = useAuthStore()
    const userId = authStore.getUser().userId

    const userBookDTO = { userId: userId, bookId: bookId }
    await axios.delete(`${API_BASE_URL}/books/reviews/delete`, userBookDTO,)
  },

  async updateReview(bookId, review) {
    const authStore = useAuthStore()
    const userId = authStore.getUser().userId

    review.userBookDTO = { userId: userId, bookId: bookId }

    const response = await axios.post(`${API_BASE_URL}/books/reviews/update`, review,)
    return response.data
  },

  getReviewsStats(reviews) {
    let average = 0

    if (reviews.length > 0) {
      const somme = reviews.reduce((acc, obj) => {
        return acc + (typeof obj.rating === "number" ? obj.rating : 0);
      }, 0);

      average = somme / reviews.length;
    }

    return {
      averageRating: average,
      totalReviews: reviews.length
    }
  }
}

export default reviewService
