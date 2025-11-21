<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen">
    <AppHeader />

    <main class="w-full flex-1 flex justify-center items-center gap-3">
      <!-- <div class="bg-box border border-border rounded-2xl w-56 h-full flex items-center justify-center">
        <span>Menu Bar</span>
      </div> -->
      <MenuBar />

      <div class="bg-box border border-border rounded-2xl flex-1 h-full flex flex-col gap-1 items-center justify-center p-6">
        <span class="text-primary-text font-semibold text-3xl">Bienvenue <span class="text-accent font-semibold text-3xl">{{ user?.username || 'Utilisateur' }}</span> !</span>
        <span class="text-secondary-text font-normal">Vous êtes maintenant connecté</span>
      </div>
    </main>
  </div>
</template>

<script>
import AppHeader from '@/components/AppHeader.vue';
import MenuBar from '@/components/MenuBar.vue';
import { useAuthStore } from '@/stores/authStore';

export default {
  name: 'Home',
  components: {
    AppHeader,
    MenuBar,
  },
  setup() {
    const authStore = useAuthStore();

    return authStore;
  },
};
</script>