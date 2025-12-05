import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
// import Register from '../views/Register.vue'
// import Library from '../views/Library.vue'
// import MyLibrary from '../views/MyLibrary.vue'
// import ReadingTracker from '../views/ReadingTracker.vue'
// import Catalogue from '../views/Catalogue.vue'
// import BookDetail from '../views/BookDetail.vue'
// import BookAdd from '../views/BookAdd.vue'
// import BookEdit from '../views/BookEdit.vue'

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
      path: '/:pathMatch(.*)*',
      redirect: '/'
    },
    // {
    //   path: '/library',
    //   name: 'library',
    //   component: Library,
    //   meta: { requiresAuth: true }
    // },
    // {
    //   path: '/my-library',
    //   name: 'my-library',
    //   component: MyLibrary,
    //   meta: { requiresAuth: true }
    // },
    // {
    //   path: '/reading-tracker',
    //   name: 'reading-tracker',
    //   component: ReadingTracker,
    //   meta: { requiresAuth: true }
    // },
    // {
    //   path: '/catalogue',
    //   name: 'catalogue',
    //   component: Catalogue,
    //   meta: { requiresAuth: true }
    // },
    // {
    //   path: '/books/add',
    //   name: 'book-add',
    //   component: BookAdd,
    //   meta: { requiresAuth: true }
    // },
    // {
    //   path: '/books/:id/edit',
    //   name: 'book-edit',
    //   component: BookEdit,
    //   meta: { requiresAuth: true }
    // },
    // {
    //   path: '/books/:id',
    //   name: 'book-detail',
    //   component: BookDetail,
    //   meta: { requiresAuth: true }
    // },
    // {
    //   path: '/marketplace',
    //   name: 'marketplace',
    //   redirect: '/library'
    // },
    // {
    //   path: '/profile',
    //   name: 'profile',
    //   redirect: '/library'
    // },
    // {
    //   path: '/register',
    //   name: 'register',
    //   component: Register,
    //   meta: { requiresGuest: true }
    // },    
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
