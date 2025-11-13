<template>
  <div class="py-10">
    <div class="text-center mb-10">
      <h2 class="text-3xl font-bold">
        Bienvenue <span class="text-primary">{{ authStore.getUser?.username || 'user' }}</span> !
      </h2>
      <p class="text-muted-foreground">Vous êtes maintenant connecté</p>
    </div>
    <div class="max-w-xl mx-auto">
      <router-link to="/library" class="inline-flex items-center justify-center rounded-2xl text-sm font-medium bg-primary text-primary-foreground hover:bg-primary/90 h-11 px-6 w-full">
        Ma Bibliothèque
      </router-link>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'

export default {
  name: 'HomePage',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()

    return {
      authStore,
      router
    }
  }
}
</script>
