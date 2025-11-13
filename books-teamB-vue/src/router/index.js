import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
      meta: { requiresGuest: true }
    },
    {
      path: '/register',
      name: 'register',
      component: Register,
      meta: { requiresGuest: true }
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/'
    }
  ]
})

// Navigation guards
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // Initialiser l'état d'authentification
  authStore.initializeAuth()
  
  const isAuthenticated = authStore.isAuthenticated
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const requiresGuest = to.matched.some(record => record.meta.requiresGuest)
  
  if (requiresAuth && !isAuthenticated) {
    // Rediriger vers login si authentification requise
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (requiresGuest && isAuthenticated) {
    // Rediriger vers dashboard si utilisateur déjà connecté
    next({ name: 'home' })
  } else {
    // Continuer vers la route
    next()
  }
})

export default router
