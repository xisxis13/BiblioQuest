<template>
  <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 animate-fadeIn">
      <!-- En-tête avec logo -->
      <div class="text-center">
        <div class="flex justify-center mb-6">
          <img src="@/assets/BCO.png" alt="Logo" class="logo-lg" />
        </div>
        <h2 class="text-3xl font-bold text-gray-900 mb-2">PAGE D'ACCUEIL</h2>
        <p class="text-gray-600">Connexion réussie ✅</p>
      </div>

      <!-- Message de bienvenue -->
      <div class="card">
        <div class="card-body text-center">
          <h3 class="text-xl font-semibold text-gray-900 mb-4">
            Bienvenue {{ authStore.getUser?.username || 'Utilisateur' }} !
          </h3>
          <p class="text-gray-600 mb-6">Vous êtes maintenant connecté</p>
          <button
            @click="handleLogout"
            class="btn btn-secondary"
          >
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
