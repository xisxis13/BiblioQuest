<template>
  <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 animate-fadeIn">
      <!-- En-tête avec logo -->
      <div class="text-center">
        <div class="flex justify-center mb-6">
          <img src="@/assets/BCO.png" alt="Logo" class="w-20 h-20 object-contain" />
        </div>
        <h2 class="text-3xl font-bold text-gray-900 mb-2">Inscription</h2>
        <p class="text-gray-600">Créez votre compte</p>
      </div>

      <!-- Formulaire -->
      <div class="bg-white rounded-2xl shadow-sm p-8">
        <form @submit.prevent="handleRegister" class="space-y-6">
            <!-- Nom d'utilisateur -->
            <div>
              <label for="username" class="block text-sm font-medium text-gray-700 mb-2">
                Nom d'utilisateur
              </label>
              <input
                id="username"
                v-model="form.username"
                type="text"
                required
                :class="[
                  'w-full px-4 py-3 border rounded-lg text-sm transition-all',
                  usernameError ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500',
                  'focus:outline-none focus:ring-2 focus:border-transparent'
                ]"
                placeholder="Votre nom d'utilisateur"
                autocomplete="username"
              />
              <p v-if="usernameError" class="mt-1 text-sm text-red-600">{{ usernameError }}</p>
            </div>

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
                autocomplete="new-password"
              />
              <p v-if="passwordError" class="mt-1 text-sm text-red-600">{{ passwordError }}</p>
            </div>

            <!-- Confirmation mot de passe -->
            <div>
              <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-2">
                Confirmer le mot de passe
              </label>
              <input
                id="confirmPassword"
                v-model="form.confirmPassword"
                type="password"
                required
                :class="[
                  'w-full px-4 py-3 border rounded-lg text-sm transition-all',
                  confirmPasswordError ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-blue-500',
                  'focus:outline-none focus:ring-2 focus:border-transparent'
                ]"
                placeholder="••••••••"
                autocomplete="new-password"
              />
              <p v-if="confirmPasswordError" class="mt-1 text-sm text-red-600">{{ confirmPasswordError }}</p>
            </div>

            <!-- Message d'erreur global -->
            <div v-if="authStore.getError" class="p-4 bg-red-50 border border-red-200 rounded-lg">
              <p class="text-sm text-red-600">{{ authStore.getError }}</p>
            </div>

            <!-- Bouton d'inscription -->
            <button
              type="submit"
              :disabled="authStore.isLoading"
              class="w-full px-4 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="authStore.isLoading" class="spinner mr-2"></span>
              {{ authStore.isLoading ? 'Inscription...' : 'Créer un compte' }}
            </button>
          </form>

        <!-- Lien vers connexion -->
        <div class="mt-6 text-center">
          <p class="text-sm text-gray-600">
            Déjà un compte ?
            <router-link to="/login" class="font-medium text-blue-600 hover:text-blue-500">
              Se connecter
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
  name: 'Register',
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
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      usernameError: '',
      emailError: '',
      passwordError: '',
      confirmPasswordError: ''
    }
  },
  methods: {
    validateForm() {
      this.usernameError = ''
      this.emailError = ''
      this.passwordError = ''
      this.confirmPasswordError = ''
      
      let isValid = true
      
      if (!this.form.username) {
        this.usernameError = 'Le nom d\'utilisateur est requis'
        isValid = false
      } else if (this.form.username.length < 3) {
        this.usernameError = 'Le nom d\'utilisateur doit contenir au moins 3 caractères'
        isValid = false
      }
      
      if (!this.form.email) {
        this.emailError = 'L\'email est requis'
        isValid = false
      } else if (!this.form.email.includes('@')) {
        this.emailError = 'Format d\'email invalide'
        isValid = false
      }
      
      if (!this.form.password) {
        this.passwordError = 'Le mot de passe est requis'
        isValid = false
      } else if (this.form.password.length < 6) {
        this.passwordError = 'Le mot de passe doit contenir au moins 6 caractères'
        isValid = false
      }
      
      if (!this.form.confirmPassword) {
        this.confirmPasswordError = 'La confirmation du mot de passe est requise'
        isValid = false
      } else if (this.form.password !== this.form.confirmPassword) {
        this.confirmPasswordError = 'Les mots de passe ne correspondent pas'
        isValid = false
      }
      
      return isValid
    },
    
    async handleRegister() {
      if (!this.validateForm()) return
      
      this.authStore.clearError()
      
      const result = await this.authStore.register({
        username: this.form.username,
        email: this.form.email,
        password: this.form.password
      })
      
      if (result.success) {
        this.router.push('/')
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
