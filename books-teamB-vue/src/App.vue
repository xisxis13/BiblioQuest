<script>
import { useAuthStore } from '@/stores/authStore'

export default {
  name: 'App',
  setup() {
    const authStore = useAuthStore()
    
    // Initialiser l'authentification au démarrage
    authStore.initializeAuth()

    return {
      authStore
    }
  }
}
</script>

<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style>
#app { min-height: 100vh; }
</style>
