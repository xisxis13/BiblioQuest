<template>
  <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 animate-fadeIn">
      <!-- En-tête avec logo -->
      <div class="text-center">
        <div class="flex justify-center mb-6">
          <img src="@/assets/BCO.png" alt="Logo" class="w-20 h-20 object-contain" />
        </div>
        <h2 class="text-3xl font-bold text-gray-900 mb-2">Connexion</h2>
        <p class="text-gray-600">Accédez à votre compte</p>
      </div>

      <!-- Formulaire -->
      <div class="bg-white rounded-2xl shadow-sm p-8">
        <form @submit.prevent="handleLogin" class="space-y-6">
            <!-- Email -->
            <div>
              <label for="email" class="block text-sm font-medium text-gray-700 mb-2">
                Adresse email
              </label>
              <input
                id="email"
                v-model="form.email"
                type="email"
                required
                :class="[
                  'w-full px-4 py-3 border rounded-lg text-sm transition-all',
                  emailError ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500',
                  'focus:outline-none focus:ring-2 focus:border-transparent'
                ]"
                placeholder="votre@email.com"
                autocomplete="email"
              />
              <p v-if="emailError" class="mt-1 text-sm text-red-600">{{ emailError }}</p>
            </div>

            <!-- Mot de passe -->
            <div>
              <label for="password" class="block text-sm font-medium text-gray-700 mb-2">
                Mot de passe
              </label>
              <input
                id="password"
                v-model="form.password"
                type="password"
                required
                :class="[
                  'w-full px-4 py-3 border rounded-lg text-sm transition-all',
                  passwordError ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500',
                  'focus:outline-none focus:ring-2 focus:border-transparent'
                ]"
                placeholder="••••••••"
                autocomplete="current-password"
              />
              <p v-if="passwordError" class="mt-1 text-sm text-red-600">{{ passwordError }}</p>
            </div>

            <!-- Message d'erreur global -->
            <div v-if="authStore.getError" class="p-4 bg-red-50 border border-red-200 rounded-lg">
              <p class="text-sm text-red-600">{{ authStore.getError }}</p>
            </div>

            <!-- Bouton de connexion -->
            <button
              type="submit"
              :disabled="authStore.isLoading"
              class="w-full px-4 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="authStore.isLoading" class="spinner mr-2"></span>
              {{ authStore.isLoading ? 'Connexion...' : 'Se connecter' }}
            </button>
          </form>

        <!-- Lien vers inscription -->
        <div class="mt-6 text-center">
          <p class="text-sm text-gray-600">
            Pas encore de compte ?
            <router-link to="/register" class="font-medium text-blue-600 hover:text-blue-500">
              Créer un compte
            </router-link>
          </p>
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
      emailError: '',
      passwordError: ''
    }
  },
  methods: {
    validateForm() {
      this.emailError = ''
      this.passwordError = ''
      
      if (!this.form.email) {
        this.emailError = 'L\'email est requis'
        return false
      }
      
      if (!this.form.email.includes('@')) {
        this.emailError = 'Format d\'email invalide'
        return false
      }
      
      if (!this.form.password) {
        this.passwordError = 'Le mot de passe est requis'
        return false
      }
      
      if (this.form.password.length < 6) {
        this.passwordError = 'Le mot de passe doit contenir au moins 6 caractères'
        return false
      }
      
      return true
    },
    
    async handleLogin() {
      try {
        console.log('Tentative de connexion...')
        if (!this.validateForm()) return
        
        this.authStore.clearError()
        
        const result = await this.authStore.login({
          username: this.form.email,  // On envoie l'email dans le champ 'username'
          password: this.form.password
        })
        
        console.log('Résultat login:', result)
        
        if (result.success) {
          console.log('Connexion réussie, redirection...')
          this.router.push('/')
        } else {
          console.error('Échec de connexion:', result.error)
        }
      } catch (error) {
        console.error('Erreur dans handleLogin:', error)
      }
    }
  },
  
  mounted() {
    // Rediriger si déjà connecté
    if (this.authStore.isAuthenticated) {
      this.router.push('/')
    }
  }
}
</script>
