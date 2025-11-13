<template>
  <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 animate-fadeIn">
      <!-- En-tête avec logo -->
      <div class="text-center">
        <div class="flex justify-center mb-6">
          <img src="@/assets/BCO.png" alt="Logo" class="w-20 h-20 object-contain" />
        </div>
        <h2 class="text-3xl font-bold text-gray-900 mb-2">PAGE D'ACCUEIL</h2>
        <p class="text-gray-600">Connexion réussie ✅</p>
      </div>

      <!-- Message de bienvenue -->
      <div class="bg-white rounded-2xl shadow-sm p-8">
        <div class="text-center">
          <h3 class="text-xl font-semibold text-gray-900 mb-4">
            Bienvenue {{ authStore.getUser?.username || 'Utilisateur' }} !
          </h3>
          <p class="text-gray-600 mb-6">Vous êtes maintenant connecté</p>
          
          <!-- Actions rapides -->
          <div class="space-y-3 mb-6">
            <router-link to="/library" class="inline-flex items-center justify-center gap-2 w-full px-4 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors shadow-sm">
              <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 715.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z"/>
              </svg>
              Ma Bibliothèque
            </router-link>
          </div>
          
          <button @click="handleLogout" class="w-full px-4 py-3 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors">
            Se déconnecter
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'

export default {
  name: 'HomePage',
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
