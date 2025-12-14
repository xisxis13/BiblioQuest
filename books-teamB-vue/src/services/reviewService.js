
import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'

const api = axios.create({
  baseURL: import.meta.env.PROD
    ? '/api'
    : 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

const reviewService = {
  // Récupérer les avis d'un livre
  async getBookReviews(bookId) {
    const response = await api.get(`/books/${bookId}/reviews`,)
    return response.data
  },

  // Ajouter un avis
  async addReview(bookId, review) {
    const authStore = useAuthStore()
    const userId = authStore.getUser().id

    review.userBookDTO = { userId: userId, bookId: bookId }

    const response = await api.post(`/books/reviews/update`, review,)
    return response.data
  },

  async deleteReview(bookId) {
    const authStore = useAuthStore()
    const userId = authStore.getUser().userId

    const userBookDTO = { userId: userId, bookId: bookId }
    await api.delete(`/books/reviews/delete`, userBookDTO,)
  },

  async updateReview(bookId, review) {
    const authStore = useAuthStore()
    const userId = authStore.getUser().userId

    review.userBookDTO = { userId: userId, bookId: bookId }

    const response = await api.post(`/books/reviews/update`, review,)
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
