<template>
  <div class="min-h-screen bg-background flex flex-col">
    <!-- Header -->
    <header class="bg-white shadow-sm py-4 border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 flex items-center justify-center gap-3">
        <div class="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center border-2 border-orange-200">
          <img src="@/assets/BCO.png" alt="BiblioQuest Logo" class="h-7 w-7 object-contain" />
        </div>
        <h1 class="text-2xl font-bold text-[#8B4513]">BiblioQuest</h1>
      </div>
    </header>

    <!-- Content -->
    <div class="flex-1 flex items-center justify-center py-12 px-4">
      <div class="max-w-md w-full">
        <!-- Titre -->
        <div class="text-center mb-8">
          <h2 class="text-3xl font-bold text-foreground mb-2">
            Rejoindre <span class="text-primary">BiblioQuest</span> !
          </h2>
        </div>

        <!-- Formulaire -->
        <div class="bg-card rounded-2xl shadow-md p-8 border border-border">
          <!-- Logo BQ -->
          <div class="flex justify-center mb-6">
            <div class="h-20 w-20 bg-orange-100 rounded-2xl flex items-center justify-center shadow-lg border-4 border-orange-200">
              <img src="@/assets/BCO.png" alt="BiblioQuest Logo" class="h-14 w-14 object-contain" />
            </div>
          </div>

          <!-- Texte connexion -->
          <div class="text-center mb-6">
            <p class="text-sm text-foreground">
              Déjà un compte ? <router-link to="/login" class="text-primary hover:underline font-medium">Se connecter</router-link>
            </p>
          </div>

          <!-- Form -->
          <form @submit.prevent="handleRegister" class="space-y-5">
            <!-- Nom d'utilisateur -->
            <div class="space-y-2">
              <label for="username" class="text-sm font-semibold text-foreground">
                Nom d'utilisateur
              </label>
              <input
                id="username"
                v-model="form.username"
                type="text"
                required
                minlength="3"
                class="w-full px-4 py-2.5 bg-input border border-border rounded-lg text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                placeholder="Votre nom d'utilisateur"
                autocomplete="username"
              />
              <p v-if="usernameError" class="text-sm text-destructive">{{ usernameError }}</p>
            </div>

            <!-- Email -->
            <div class="space-y-2">
              <label for="email" class="text-sm font-semibold text-foreground">
                Email
              </label>
              <input
                id="email"
                v-model="form.email"
                type="email"
                required
                class="w-full px-4 py-2.5 bg-input border border-border rounded-lg text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                placeholder="votre@email.com"
                autocomplete="email"
              />
              <p v-if="emailError" class="text-sm text-destructive">{{ emailError }}</p>
            </div>

            <!-- Mot de passe -->
            <div class="space-y-2">
              <label for="password" class="text-sm font-semibold text-foreground">
                Mot de passe
              </label>
              <input
                id="password"
                v-model="form.password"
                type="password"
                required
                minlength="6"
                class="w-full px-4 py-2.5 bg-input border border-border rounded-lg text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                placeholder="Minimum 6 caractères"
                autocomplete="new-password"
              />
              <p v-if="passwordError" class="text-sm text-destructive">{{ passwordError }}</p>
            </div>

            <!-- Confirmation mot de passe -->
            <div class="space-y-2">
              <label for="confirmPassword" class="text-sm font-semibold text-foreground">
                Confirmer le mot de passe
              </label>
              <input
                id="confirmPassword"
                v-model="form.confirmPassword"
                type="password"
                required
                minlength="6"
                class="w-full px-4 py-2.5 bg-input border border-border rounded-lg text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                placeholder="Répétez votre mot de passe"
                autocomplete="new-password"
              />
              <p v-if="confirmPasswordError" class="text-sm text-destructive">{{ confirmPasswordError }}</p>
            </div>

            <!-- Bouton d'inscription -->
            <button
              type="submit"
              :disabled="authStore.isLoading"
              class="w-full bg-primary hover:bg-primary/90 text-white font-semibold py-3 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ authStore.isLoading ? 'Inscription...' : 'S\'inscrire' }}
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
  name: 'RegisterPage',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    return { authStore, router }
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
      
      if (!this.form.username || this.form.username.length < 3) {
        this.usernameError = 'Le nom d\'utilisateur doit contenir au moins 3 caractères'
        return false
      }
      if (!this.form.email || !this.form.email.includes('@')) {
        this.emailError = 'Format d\'email invalide'
        return false
      }
      if (!this.form.password || this.form.password.length < 6) {
        this.passwordError = 'Le mot de passe doit contenir au moins 6 caractères'
        return false
      }
      if (this.form.password !== this.form.confirmPassword) {
        this.confirmPasswordError = 'Les mots de passe ne correspondent pas'
        return false
      }
      return true
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
        this.router.push('/library')
      }
    }
  },
  mounted() {
    if (this.authStore.isAuthenticated) {
      this.router.push('/library')
    }
  }
}
</script>