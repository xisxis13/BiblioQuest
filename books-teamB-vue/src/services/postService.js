import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.PROD
    ? '/api'
    : 'http://localhost:8080/api',
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
