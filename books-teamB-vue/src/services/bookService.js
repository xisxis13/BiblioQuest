import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.PROD
    ? '/api'
    : 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' }
})

export const bookService = {
  async getAll() {
    return (await api.get('/books')).data
  },

  async getById(id) {
    return (await api.get(`/books/${id}`)).data
  },

  async add(book) {
    return api.post('/books/add', book)
  },

  async update(id, book) {
    return api.put(`/books/${id}`, book)
  },

  async delete(id) {
    return api.delete(`/books/${id}`)
  },

  async getSubjects(){
    return api.get('/books/subjects')
  },
}

export default bookService
