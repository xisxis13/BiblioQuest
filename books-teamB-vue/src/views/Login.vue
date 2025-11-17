<template>
  <div class="min-h-screen bg-[#F5F1ED] flex flex-col">
    <!-- Header -->
    <header class="bg-white border-b border-gray-200 py-4 px-6">
      <div class="flex items-center justify-center">
        <span class="text-2xl font-bold text-[#8B4513]">Browser</span>
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
            <div v-if="authStore.getError" class="bg-red-50 border border-red-200 rounded-lg p-4">
              <p class="text-sm text-red-600">{{ authStore.getError }}</p>
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
                placeholder="ex: example@mail.com"
              />
            </div>

            <!-- Password Field -->
            <div class="space-y-2">
              <label for="password" class="block text-sm font-semibold text-[#8B4513]">Mot de passe</label>
              <input
                id="password"
                v-model="form.password"
                type="password"
                required
                minlength="6"
                class="w-full px-4 py-3 bg-[#F5F1ED] border border-gray-300 rounded-xl text-sm placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                placeholder="ex: ••••••••"
              />
            </div>

            <!-- Submit Button -->
            <button
              type="submit"
              :disabled="authStore.isLoading"
              class="w-full bg-orange-500 hover:bg-orange-600 disabled:bg-orange-300 text-white font-semibold py-3 px-4 rounded-xl transition-colors duration-200"
            >
              <span v-if="authStore.isLoading">Connexion...</span>
              <span v-else>Connexion</span>
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
      }
    }
  },
  methods: {
    async handleLogin() {
      try {
        await this.authStore.login(this.form)
        this.router.push('/')
      } catch (error) {
        console.error('Login failed:', error)
      }
    }
  },
  mounted() {
    this.authStore.clearError()
  }
}
</script>
