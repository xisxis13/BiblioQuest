<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen items-center justify-center">
    <div class="max-w-sm w-full flex flex-col items-center justify-center gap-8">
      <span class="text-primary-text font-semibold text-3xl text-center w-full">Bienvenue sur <span class="text-accent font-semibold text-3xl">BiblioQuest</span> !</span>
      
      <form class="bg-box border border-border rounded-2xl w-full px-6 pt-1 pb-6 flex flex-col justify-center items-center gap-1">
        <img :src="biblioQuestIcon" alt="BilbioQuest icon" class="h-20">

        <div class="w-full flex flex-col justify-center items-center gap-4">
          <hr class="border border-border w-full">

          <span class="text-primary-text text-sm">Pas encore de compte ? <router-link class="text-accent text-sm">Inscris toi ici</router-link></span>
  
          <div class="w-full flex flex-col gap-1.5 items-start justify-center">
            <label for="email" class="text-primary-text font-semibold text-lg text-center">Email</label>
            <input
              type="email"
              required
              class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
              placeholder="ex: admin@example.com"
            />
          </div>

          <div class="w-full flex flex-col gap-1.5 items-start justify-center">
            <label for="password" class="text-primary-text font-semibold text-lg text-center">Mot de passe</label>
            <input
              type="password"
              required
              minlength="6"
              class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
              placeholder="ex: ••••••••"
            />
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import biblioQuestIcon from '@/assets/BCO.png';

export default {
  name: 'Login',
  components: {
    
  },
  data() {
    return {
      biblioQuestIcon,
    };
  },
};
</script>