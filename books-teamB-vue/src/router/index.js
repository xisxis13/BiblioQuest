import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Catalog from '../views/Catalog.vue'
import EditBook from '../views/EditBook.vue'
import BookDetail from '../views/BookDetail.vue'
import MyLibrary from '../views/MyLibrary.vue'
import ReadingTracker from '../views/ReadingTracker.vue'
import Profile from '@/views/Profile.vue'

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
      path: '/catalog',
      name: 'catalog',
      component: Catalog,
      meta: { requiresAuth: true }
    },
    {
      path: '/books/add',
      name: 'add-book',
      component: EditBook,
      meta: { requiresAuth: true }
    },
    {
      path: '/books/:id',
      name: 'book-detail',
      component: BookDetail,
      meta: { requiresAuth: true }
    },
    {
      path: '/my-library',
      name: 'my-library',
      component: MyLibrary,
      meta: { requiresAuth: true }
    },
    {
      path: '/reading-tracker',
      name: 'reading-tracker',
      component: ReadingTracker,
      meta: { requiresAuth: true }
    },
    {
      path: '/books/:id/edit',
      name: 'book-edit',
      component: EditBook,
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: Profile,
      meta: { requiresAuth: true }
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/'
    },
  ]
})

// Navigation guards
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

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
