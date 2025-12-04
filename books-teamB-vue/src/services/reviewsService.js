import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const getCurrentUsername = () => {
  return localStorage.getItem('currentUsername') || 'admin'
}

const reviewsService = {
  async getReviewsForBook(bookId) {
    try {
      const username = getCurrentUsername()
      const response = await axios.get(`${API_BASE_URL}/books/${bookId}/reviews`, {
        headers: {
          'X-Username': username
        }
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des avis:', error)
      throw error
    }
  },

  async addOrUpdateReview(bookId, reviewData) {
    try {
      const username = getCurrentUsername()
      console.log("review username: ", username)
      const response = await axios.post(`${API_BASE_URL}/books/${bookId}/reviews`, reviewData, {
        headers: {
          'X-Username': username,
          'Content-Type': 'application/json'
        }
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de l\'ajout/modification de l\'avis:', error)
      throw error
    }
  },

  async deleteReview(bookId) {
    try {
      const username = getCurrentUsername()
      await axios.delete(`${API_BASE_URL}/books/${bookId}/reviews`, {
        headers: {
          'X-Username': username
        }
      })
    } catch (error) {
      console.error('Erreur lors de la suppression de l\'avis:', error)
      throw error
    }
  },

  calculateReviewStats(reviews) {
    if (!reviews || reviews.length === 0) {
      return {
        averageRating: 0,
        totalReviews: 0
      }
    }

    const totalRating = reviews.reduce((sum, review) => sum + review.rating, 0)
    const averageRating = totalRating / reviews.length

    return {
      averageRating,
      totalReviews: reviews.length
    }
  }
}

export default reviewsService
