<template>
  <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 animate-fadeIn">
      <!-- En-tête avec logo -->
      <div class="text-center">
        <div class="flex justify-center mb-6">
          <img src="@/assets/BCO.png" alt="Logo" class="logo-lg" />
        </div>
        <h2 class="text-3xl font-bold text-gray-900 mb-2">Inscription</h2>
        <p class="text-gray-600">Créez votre compte</p>
      </div>

      <!-- Formulaire -->
      <div class="card">
        <div class="card-body">
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
                class="input"
                :class="{ 'input-error': usernameError }"
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
                class="input"
                :class="{ 'input-error': emailError }"
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
                class="input"
                :class="{ 'input-error': passwordError }"
                placeholder="Minimum 6 caractères"
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
                class="input"
                :class="{ 'input-error': confirmPasswordError }"
                placeholder="Répétez votre mot de passe"
                autocomplete="new-password"
              />
              <p v-if="confirmPasswordError" class="mt-1 text-sm text-red-600">{{ confirmPasswordError }}</p>
            </div>

            <!-- Message d'erreur global -->
            <div v-if="authStore.getError" class="alert alert-error">
              {{ authStore.getError }}
            </div>

            <!-- Bouton d'inscription -->
            <button
              type="submit"
              :disabled="authStore.isLoading"
              class="btn btn-primary w-full"
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
