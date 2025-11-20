<script>
import { useAuthStore } from '@/stores/authStore'
import VerticalNavbar from './components/VerticalNavbar.vue';

export default {
  name: 'App',
  components: {
    VerticalNavbar
  },
  data: () => {
    return {
      navItems: [
        {name: "home", route: "/"},
        {name: "login", route: "/catalog"}
      ]
    }
  },
  setup() {
    const authStore = useAuthStore()

    // Initialize authentication state when app starts
    authStore.initializeAuth()

    return {
      authStore
    }
  }
}
</script>

<template>
  <div id="app">
    <VerticalNavbar :items="navItems"/>
    <router-view />
  </div>
</template>

<style>
#app { min-height: 100vh; }
</style>
