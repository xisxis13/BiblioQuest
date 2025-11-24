<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen items-center justify-center">
    <div class="max-w-sm w-full flex flex-col items-center justify-center gap-8">
      <span class="text-primary-text font-semibold text-3xl text-center w-full">Bienvenue sur <span class="text-accent font-semibold text-3xl">BiblioQuest</span> !</span>
      
      <form @submit.prevent="handleRegister" class="bg-box border border-border rounded-2xl w-full p-6 flex flex-col justify-center items-center gap-4">
        <span class="text-primary-text text-sm">Déjà un compte ? <router-link to="/login" class="text-accent text-sm">Connectes toi ici</router-link></span>

        <hr class="border border-border w-full">

        <!-- Error message -->
        <div v-if="errorMessage" class="bg-red-50 border border-red-300 rounded-2xl w-full p-3 flex items-center justify-start gap-2">
          <Info class="text-red-600 " :size="20" />
          <span class="text-red-800 text-sm">{{ errorMessage }}</span>
        </div>

        <!-- Success message -->
        <div v-if="successMessage" class="bg-green-50 border border-green-400 rounded-2xl w-full p-3 flex items-center justify-start gap-2">
          <CircleCheck class="text-green-600 " :size="20" />
          <span class="text-green-800 text-sm">{{ successMessage }}</span>
        </div>

        <div class="w-full flex flex-col gap-1.5 items-start justify-center">
          <label for="username" class="text-primary-text font-semibold text-lg text-center">Nom d'utilisateur</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            required
            minlength="3"
            class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
            placeholder="Votre nom d'utilisateur"
            autocomplete="username"
          />
        </div>

        <div class="w-full flex flex-col gap-1.5 items-start justify-center">
          <label for="email" class="text-primary-text font-semibold text-lg text-center">Email</label>
          <input
            id="email"
            v-model="form.email"
            type="email"
            required
            class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
            placeholder="votre@email.com"
            autocomplete="email"
          />
        </div>

        <div class="w-full flex flex-col gap-1.5 items-start justify-center">
          <label for="password" class="text-primary-text font-semibold text-lg text-center">Mot de passe</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            required
            minlength="6"
            class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
            placeholder="Minimum 6 caractères"
            autocomplete="new-password"
          />
        </div>

        <div class="w-full flex flex-col gap-1.5 items-start justify-center">
          <label for="confirmPassword" class="text-primary-text font-semibold text-lg text-center">Confirmer le mot de passe</label>
          <input
            id="confirmPassword"
            v-model="form.confirmPassword"
            type="password"
            required
            minlength="6"
            class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
            placeholder="Répétez votre mot de passe"
            autocomplete="new-password"
          />
        </div>

        <button 
          type="submit"
          :disabled="isLoading"
          class="bg-accent hover:bg-accent/90 rounded-2xl w-full px-4 py-3 text-box font-medium cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
        >
          <template v-if="isLoading">
            <svg class="animate-spin h-4 w-4 text-box" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span>Inscription en cours...</span>
          </template>
          
          <span v-else>S'inscrire</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/authStore';
import { useRouter } from 'vue-router';

export default {
  name: 'Register',
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    return { authStore, router };
  },
  data() {
    return {
      form: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
      },
      errorMessage: '',
      successMessage: '',
      isLoading: false,
    };
  },
  methods: {
    validateForm() {
      if (!this.form.username || this.form.username.length < 3) {
        this.errorMessage = 'Le nom d\'utilisateur doit contenir au moins 3 caractères';
        return false;
      };
      
      if (!this.form.email || !this.form.email.includes('@')) {
        this.errorMessage = 'Format d\'email invalide';
        return false;
      };

      if (!this.form.password || this.form.password.length < 6) {
        this.errorMessage = 'Le mot de passe doit contenir au moins 6 caractères';
        return false;
      };

      if (this.form.password !== this.form.confirmPassword) {
        this.errorMessage = 'Les mots de passe ne correspondent pas';
        return false;
      };

      return true;
    },

    async handleRegister() {
      // Réinitialiser les messages
      this.errorMessage = '';
      this.successMessage = '';

      if (!this.validateForm()) return;

      this.isLoading = true;

      try {
        await this.authStore.register({
          username: this.form.username,
          email: this.form.email,
          password: this.form.password,
        });

        this.successMessage = 'Inscription réussie ! Redirection en cours...';

        // Rediriger après un court délai
        setTimeout(() => {
          this.router.push('/')
        }, 1000)
      } catch (error) {
        console.error('Registration failed:', error);
        this.errorMessage = error.message || 'Erreur lors de l\'inscription'
      } finally {
        this.isLoading = false;
      };
    },
    clearMessage() {
      this.errorMessage = '';
      this.successMessage = '';
    },
  },
  mounted() {
    if (this.authStore.isAuthenticated) {
      this.router.push('/library');
    };
  },
};
</script>