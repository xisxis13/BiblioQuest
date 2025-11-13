<template>
  <div class="min-h-screen bg-background flex items-center justify-center px-4 py-12">
    <div class="max-w-md w-full">
      <div class="text-center mb-8">
        <h2 class="text-3xl font-bold">
          Bienvenue sur <span class="text-primary">BiblioQuest</span> !
        </h2>
      </div>

      <div class="bg-card border border-border rounded-2xl p-8">
        <div class="flex justify-center mb-6">
          <img src="@/assets/BCO.png" alt="BiblioQuest Logo" class="h-16 w-auto" />
        </div>

        <div class="text-center mb-6">
          <p class="text-sm text-foreground">
            Pas encore de compte ?
            <router-link to="/register" class="text-primary hover:underline font-medium">Inscris toi ici</router-link>
          </p>
        </div>

        <form @submit.prevent="handleLogin" class="space-y-5">
          <div class="space-y-2">
            <label for="email" class="text-sm font-semibold">Email</label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              required
              class="w-full px-4 py-2.5 bg-input border border-border rounded-2xl text-sm placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder="ex: example@mail.com"
            />
            <p v-if="emailError" class="text-sm text-destructive">{{ emailError }}</p>
          </div>

          <div class="space-y-2">
            <label for="password" class="text-sm font-semibold">Mot de passe</label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              required
              minlength="6"
              class="w-full px-4 py-2.5 bg-input border border-border rounded-2xl text-sm placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder="ex: ••••••••"
            />
            <p v-if="passwordError" class="text-sm text-destructive">{{ passwordError }}</p>
          </div>

          <button
            type="submit"
            :disabled="authStore.isLoading"
            class="w-full bg-primary hover:bg-primary/90 text-primary-foreground font-semibold py-3 rounded-2xl transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ authStore.isLoading ? 'Connexion...' : 'Connexion' }}
          </button>
        </form>
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
        if (!this.validateForm()) return

        this.authStore.clearError()

        const result = await this.authStore.login({
          username: this.form.email,
          password: this.form.password
        })

        if (result.success) {
          this.router.push('/library')
        }
      } catch (error) {
        console.error('Erreur dans handleLogin:', error)
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
