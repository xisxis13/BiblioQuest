<template>
  <div class="min-h-screen bg-[#F5F1ED] flex flex-col">
    <!-- Header -->
    <header class="bg-white border-b border-gray-200 py-4 px-6">
      <div class="flex items-center justify-center">
        <span class="text-2xl font-bold text-[#8B4513]">BiblioQuest</span>
      </div>
    </header>

    <!-- Main Content -->
    <div class="flex-1 flex items-center justify-center p-4">
      <div class="max-w-md w-full space-y-8">
        <!-- BiblioQuest Header -->
        <div class="text-center">
          <h2 class="text-3xl font-bold text-[#8B4513] mb-2">Bienvenue sur</h2>
          <h1 class="text-4xl font-bold text-orange-500 mb-6">BiblioQuest !</h1>
          
          <!-- Logo -->
          <div class="mx-auto h-20 w-20 bg-white rounded-2xl flex items-center justify-center mb-8 shadow-lg border-4 border-orange-200">
            <img src="@/assets/BCO.png" alt="BiblioQuest Logo" class="h-14 w-14 object-contain" />
          </div>
          
          <p class="text-sm text-gray-600 mb-6">Pas encore de compte ? <router-link to="/register" class="text-orange-500 hover:text-orange-600">Inscris toi ici</router-link></p>
        </div>

        <!-- Login Form -->
        <div class="bg-white rounded-2xl p-8 shadow-sm">
          <form @submit.prevent="handleLogin" class="space-y-6">
            <!-- Error Message -->
            <div v-if="errorMessage" class="bg-red-50 border border-red-200 rounded-lg p-4">
              <div class="flex items-center">
                <svg class="h-5 w-5 text-red-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <p class="text-sm text-red-800">{{ errorMessage }}</p>
              </div>
            </div>

            <!-- Success Message -->
            <div v-if="successMessage" class="bg-green-50 border border-green-200 rounded-lg p-4">
              <div class="flex items-center">
                <svg class="h-5 w-5 text-green-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <p class="text-sm text-green-800">{{ successMessage }}</p>
              </div>
            </div>

            <!-- Email Field -->
            <div class="space-y-2">
              <label for="email" class="block text-sm font-semibold text-[#8B4513]">Email</label>
              <input
                id="email"
                v-model="form.email"
                type="email"
                required
                class="w-full px-4 py-3 bg-[#F5F1ED] border border-gray-300 rounded-xl text-sm placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                placeholder="ex: admin@example.com"
                @input="clearMessages"
              />
            </div>

            <!-- Password Field -->
            <div class="space-y-2">
              <label for="password" class="block text-sm font-semibold text-[#8B4513]">Mot de passe</label>
              <div class="relative">
                <input
                  id="password"
                  v-model="form.password"
                  :type="showPassword ? 'text' : 'password'"
                  required
                  minlength="6"
                  class="w-full px-4 py-3 pr-12 bg-[#F5F1ED] border border-gray-300 rounded-xl text-sm placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                  placeholder="ex: ••••••••"
                  @input="clearMessages"
                />
                <button
                  type="button"
                  @click="showPassword = !showPassword"
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600"
                >
                  <svg v-if="!showPassword" class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  <svg v-else class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21" />
                  </svg>
                </button>
              </div>
            </div>

            <!-- Submit Button -->
            <button 
              type="submit" 
              :disabled="isLoading"
              class="w-full bg-orange-500 hover:bg-orange-600 disabled:bg-orange-300 text-white font-semibold py-3 px-4 rounded-xl transition-colors duration-200 flex items-center justify-center gap-2"
              @click="clearMessages"
            >
              <svg v-if="isLoading" class="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span v-if="isLoading">Connexion en cours...</span>
              <span v-else>Se connecter</span>
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'

export default {
  name: 'LoginPage',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()

    return {
      authStore,
      router
    }
  },
  data() {
    return {
      form: {
        email: '',
        password: ''
      },
      errorMessage: '',
      successMessage: '',
      isLoading: false,
      showPassword: false
    }
  },
  methods: {
    async handleLogin() {
      // Réinitialiser les messages
      this.errorMessage = ''
      this.successMessage = ''
      this.isLoading = true

      try {
        await this.authStore.login(this.form)
        this.successMessage = 'Connexion réussie ! Redirection en cours...'
        
        // Rediriger après un court délai pour montrer le message de succès
        setTimeout(() => {
          this.router.push('/')
        }, 1000)
      } catch (error) {
        console.error('Login failed:', error)
        
        // Gestion des différents types d'erreurs
        if (error.response && error.response.data) {
          const errorData = error.response.data
          if (errorData.code === 'INVALID_CREDENTIALS') {
            this.errorMessage = 'Email ou mot de passe incorrect'
          } else if (errorData.message) {
            this.errorMessage = errorData.message
          } else {
            this.errorMessage = 'Erreur de connexion'
          }
        } else if (error.message) {
          this.errorMessage = error.message
        } else {
          this.errorMessage = 'Erreur de connexion. Vérifiez que le serveur est démarré.'
        }
      } finally {
        this.isLoading = false
      }
    },

    clearMessages() {
      this.errorMessage = ''
      this.successMessage = ''
    }
  },
  mounted() {
    this.authStore.clearError()
  }
}
</script>
