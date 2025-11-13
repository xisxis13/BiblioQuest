<script>
import { useAuthStore } from '@/stores/authStore'
import { useRoute, useRouter } from 'vue-router'

export default {
  name: 'App',
  setup() {
    const authStore = useAuthStore()
    const route = useRoute()
    const router = useRouter()

    authStore.initializeAuth()

    const navItems = [
      { label: 'Accueil', to: { name: 'home' }, icon: 'home' },
      { label: 'My Library', to: { name: 'home' }, icon: 'library' }
    ]

    const isGuestLayout = () => route.matched.some(r => r.meta?.requiresGuest)

    const isActive = (to) => route.name === to.name

    const handleLogout = () => {
      authStore.logout()
      router.push('/login')
    }

    return {
      authStore,
      route,
      navItems,
      isGuestLayout,
      isActive,
      handleLogout
    }
  }
}
</script>

<template>
  <div id="app" class="min-h-screen bg-background text-foreground">
    <div v-if="isGuestLayout()" class="min-h-screen">
      <router-view />
    </div>
    <div v-else>
      <header class="sticky top-0 z-40 bg-card border-b border-border">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-center">
          <h1 class="text-2xl font-semibold">
            <span class="text-foreground">Browser</span>
          </h1>
        </div>
      </header>

      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 grid grid-cols-12 gap-6">
        <aside class="col-span-3 md:col-span-3 lg:col-span-3">
          <div class="bg-card border border-border rounded-2xl p-3 flex flex-col justify-between h-full">
            <nav class="space-y-2">
              <router-link
                v-for="item in navItems"
                :key="item.label"
                :to="item.to"
                :class="[
                  'flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
                  isActive(item.to) ? 'bg-primary text-primary-foreground' : 'hover:bg-accent hover:text-accent-foreground'
                ]"
              >
                <span class="inline-flex items-center justify-center w-5 h-5">
                  <svg v-if="item.icon==='home'" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M10.707 1.293a1 1 0 00-1.414 0l-8 8A1 1 0 002 10h1v6a2 2 0 002 2h2a2 2 0 002-2v-4h2v4a2 2 0 002 2h2a2 2 0 002-2v-6h1a1 1 0 00.707-1.707l-8-8z"/></svg>
                  <svg v-else-if="item.icon==='library'" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M4 3a2 2 0 00-2 2v10a2 2 0 102 0V5a2 2 0 00-2-2zm4 0a2 2 0 00-2 2v10a2 2 0 102 0V5a2 2 0 00-2-2zm4 0a2 2 0 00-2 2v10a2 2 0 102 0V5a2 2 0 00-2-2zm4 0a2 2 0 00-2 2v10a2 2 0 102 0V5a2 2 0 00-2-2z"/></svg>
                </span>
                <span class="text-sm font-medium">{{ item.label }}</span>
              </router-link>
            </nav>

            <button @click="handleLogout" class="mt-6 flex items-center gap-3 px-4 py-3 rounded-xl transition-colors border border-input bg-background hover:bg-accent hover:text-accent-foreground">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M3 3a1 1 0 00-1 1v12a1 1 0 102 0V4a1 1 0 00-1-1zm10.293 9.293a1 1 0 001.414 1.414l3-3a1 1 0 000-1.414l-3-3a1 1 0 10-1.414 1.414L14.586 9H7a1 1 0 100 2h7.586l-1.293 1.293z" clip-rule="evenodd"/></svg>
              <span class="text-sm font-medium">Se déconnecter</span>
            </button>
          </div>
        </aside>

        <main class="col-span-9 md:col-span-9 lg:col-span-9">
          <div class="bg-card border border-border rounded-2xl p-6">
            <router-view />
          </div>
        </main>
      </div>
    </div>
  </div>
 </template>

<style>
#app { min-height: 100vh; }
</style>
