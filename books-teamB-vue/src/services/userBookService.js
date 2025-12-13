import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const userBookService = {
  async getAllBooks(userId) {
    try {
      const response = await axios.get(`${API_BASE_URL}/user/books?userId=${userId}`,)
      return response.data
    } catch (error) {
      console.error('userBookService.getAllBooks error', error)
      throw error
    }
  },

  async addBook(userId, bookId) {
    try {
      const response = await axios.post(`${API_BASE_URL}/user/books`, {userId: userId, bookId: bookId})
      return response.data
    } catch (error) {
      console.error('userBookService.addBook error', error)
      throw error
    }
  },

  async deleteBook(userId, bookId) {
    try {
      const response = await axios.delete(`/users/${userId}/books/${bookId}`)
      return response.data
    } catch (error) {
      console.error('userBookService.deleteBook error', error)
      throw error
    }
  },

  async updateReadingStatus(userId, bookId, readingStatus) {
    try {
      const params = new URLSearchParams()
      if (readingStatus !== undefined && readingStatus !== null) params.append('status', readingStatus)
      const response = await axios.post(`${API_BASE_URL}/user/books/update?readingStatusId=${readingStatus}`, {userId: userId, bookId: bookId})
      return response.data
    } catch (error) {
      console.error('userBookService.updateReadingStatus error', error)
      throw error
    }
  }
}

export default userBookService
