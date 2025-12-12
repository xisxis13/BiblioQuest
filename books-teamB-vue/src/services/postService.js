import axios from 'axios'
import { authService } from '@/services/authService'

const API_BASE = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' }
})


const postService = {
  list() {
    return api.get('/posts/all',)
  },

  add(post) {
    return api.post('/posts', post,)
  },

  remove(id) {
    return api.delete(`/posts/${id}`,)
  },

  getUserPosts(userId){
    return api.get(`/posts/user/${userId}`)
  }
}

export { postService }
export default postService
