<script>
import { useAuthStore } from '@/stores/authStore'

export default {
  name: 'App',
  setup() {
    const authStore = useAuthStore()
    
    // Initialize authentication state when app starts
    authStore.initializeAuth()
    
    return {
      text: 'Touch me',
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
#app {
  min-height: 100vh;
}
</style>
