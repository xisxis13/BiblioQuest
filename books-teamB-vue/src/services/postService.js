import axios from 'axios'
import { authService } from '@/services/authService'

const API_BASE = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use((config) => {
  try {
    let token = null

    if (typeof authService.getToken === 'function') {
      const t = authService.getToken()

      if (typeof t === 'string') {
        token = t
      } else if (t && typeof t === 'object') {
        token = t.token || t.accessToken || t.jwt || null
      }
    } else {
      const raw = localStorage.getItem('auth_token')
      try {
        const parsed = JSON.parse(raw)
        token = parsed.token || parsed.accessToken || parsed.jwt || raw
      } catch {
        token = raw
      }
    }

    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
  } catch (e) {
    // on ignore, pas bloquant
  }

  return config
})

function usernameHeader() {
  try {
    const user =
      typeof authService.getUser === 'function' ? authService.getUser() : null
    const username = user?.username || user?.email || user?.name
    return username ? { 'X-Username': username } : {}
  } catch (e) {
    return {}
  }
}

const postService = {
  list() {
    const headers = usernameHeader()
    return api.get('/posts', { headers })
  },

  add(post) {
    const headers = usernameHeader()
    return api.post('/posts', post, { headers })
  },

  remove(id) {
    const headers = usernameHeader()
    return api.delete(`/posts/${id}`, { headers })
  }
}

export { postService }
export default postService
