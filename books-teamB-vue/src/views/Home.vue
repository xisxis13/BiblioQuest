<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation -->
    <nav class="navbar">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <!-- Logo -->
          <div class="flex items-center space-x-3">
            <img src="@/assets/BCO.png" alt="Logo" class="logo" />
            <div>
              <h1 class="text-lg font-bold text-gray-900">Accueil</h1>
              <p class="text-xs text-gray-500">Tableau de bord</p>
            </div>
          </div>
          
          <!-- Menu utilisateur -->
          <div class="flex items-center space-x-4">
            <div class="hidden md:flex items-center space-x-3 text-sm">
              <span class="text-gray-600">Bonjour,</span>
              <span class="font-semibold text-gray-900">{{ authStore.getUser?.username || 'Utilisateur' }}</span>
            </div>
            <button
              @click="handleLogout"
              class="btn btn-secondary"
            >
              Déconnexion
            </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- Contenu principal -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      
      <!-- En-tête de bienvenue -->
      <div class="text-center mb-12 animate-fadeIn">
        <h2 class="text-3xl font-bold text-gray-900 mb-4">
          Bienvenue dans votre espace
        </h2>
        <p class="text-lg text-gray-600 max-w-2xl mx-auto">
          Gérez vos livres, découvrez de nouvelles lectures et partagez vos coups de cœur.
        </p>
      </div>

      <!-- Statistiques -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
        <div class="card">
          <div class="card-body text-center">
            <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mx-auto mb-4">
              <svg class="w-6 h-6 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 015.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z"/>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Ma Collection</h3>
            <p class="text-3xl font-bold text-gray-900 mb-1">0</p>
            <p class="text-sm text-gray-500">livres</p>
          </div>
        </div>
        
        <div class="card">
          <div class="card-body text-center">
            <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center mx-auto mb-4">
              <svg class="w-6 h-6 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Terminés</h3>
            <p class="text-3xl font-bold text-gray-900 mb-1">0</p>
            <p class="text-sm text-gray-500">lectures</p>
          </div>
        </div>
        
        <div class="card">
          <div class="card-body text-center">
            <div class="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center mx-auto mb-4">
              <svg class="w-6 h-6 text-yellow-600" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M3 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z" clip-rule="evenodd"/>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-900 mb-2">En cours</h3>
            <p class="text-3xl font-bold text-gray-900 mb-1">0</p>
            <p class="text-sm text-gray-500">lectures</p>
          </div>
        </div>
        
        <div class="card">
          <div class="card-body text-center">
            <div class="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center mx-auto mb-4">
              <svg class="w-6 h-6 text-red-600" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Favoris</h3>
            <p class="text-3xl font-bold text-gray-900 mb-1">0</p>
            <p class="text-sm text-gray-500">coups de cœur</p>
          </div>
        </div>
      </div>

      <!-- Actions rapides -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="card">
          <div class="card-body">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Actions rapides</h3>
            <div class="space-y-3">
              <button class="btn btn-primary w-full">
                Ajouter un livre
              </button>
              <button class="btn btn-secondary w-full">
                Rechercher
              </button>
              <button class="btn btn-secondary w-full">
                Mes favoris
              </button>
            </div>
          </div>
        </div>
        
        <div class="card">
          <div class="card-body">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Profil utilisateur</h3>
            <div class="space-y-3">
              <div class="flex items-center p-3 bg-gray-50 rounded-lg">
                <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mr-3">
                  <svg class="w-4 h-4 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-500">Utilisateur</p>
                  <p class="text-sm font-semibold text-gray-900">{{ authStore.getUser?.username }}</p>
                </div>
              </div>
              
              <div class="flex items-center p-3 bg-gray-50 rounded-lg">
                <div class="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center mr-3">
                  <svg class="w-4 h-4 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                    <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
                  </svg>
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-500">Email</p>
                  <p class="text-sm font-semibold text-gray-900">{{ authStore.getUser?.email }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="card">
          <div class="card-body">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Activité récente</h3>
            <div class="text-center py-8 text-gray-500">
              <div class="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg class="w-6 h-6 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 715.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z"/>
                </svg>
              </div>
              <p class="font-medium">Aucune activité récente</p>
              <p class="text-sm mt-2">Commencez par ajouter votre premier livre</p>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'

export default {
  name: 'Home',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()

    return {
      authStore,
      router
    }
  },
  methods: {
    handleLogout() {
      this.authStore.logout()
      this.router.push('/login')
    }
  },
  mounted() {
    // Initialiser l'authentification au montage
    this.authStore.initializeAuth()
    
    // Rediriger si pas connecté
    if (!this.authStore.isAuthenticated) {
      this.router.push('/login')
    }
  }
}
</script>
