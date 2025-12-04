<template>
  <div class="fixed left-0 top-16 bottom-0 w-44 bg-white border-r border-gray-200 z-10">
    <!-- Header with Logo -->
    <div class="p-4 border-b border-gray-200">
      <div class="flex items-center gap-3 mb-4">
        <div class="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center border-2 border-orange-200">
          <img src="@/assets/BCO.png" alt="BiblioQuest Logo" class="h-7 w-7 object-contain" />
        </div>
        <div>
          <div class="text-sm font-bold text-[#8B4513]">BiblioQuest</div>
          <div class="text-xs text-gray-500">Votre bibliothèque</div>
        </div>
      </div>
    </div>

    <!-- Navigation -->
    <nav class="p-4 space-y-1">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="nav-item"
        :class="{ 'nav-item-active': isActive(item.path) }"
      >
        <!-- Home Icon -->
        <svg v-if="item.icon === 'home'" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
        </svg>
        <!-- Search Icon -->
        <svg v-else-if="item.icon === 'search'" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <!-- Library Icon -->
        <svg v-else-if="item.icon === 'library'" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
        </svg>
        <!-- Collection Icon -->
        <svg v-else-if="item.icon === 'collection'" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
        </svg>
        <!-- Shopping Icon -->
        <svg v-else-if="item.icon === 'shopping'" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
        </svg>
        <!-- User Icon -->
        <svg v-else-if="item.icon === 'user'" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
        </svg>
        <span>{{ item.label }}</span>
      </router-link>
    </nav>

    <!-- Logout -->
    <div class="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-200">
      <button 
        @click="handleLogout"
        class="nav-item text-gray-500 hover:text-red-600 w-full"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
        </svg>
        <span>Se déconnecter</span>
      </button>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/authStore'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'Sidebar',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    const route = useRoute()

    const navItems = [
      { path: '/', label: 'Accueil', icon: 'home' },
      { path: '/catalogue', label: 'Catalogue', icon: 'collection' },
      { path: '/my-library', label: 'Ma Bibliothèque', icon: 'library' },
      { path: '/profile', label: 'Profile', icon: 'user' }
    ]

    const isActive = (path) => {
      return route.path === path
    }

    const handleLogout = async () => {
      await authStore.logout()
      router.push('/login')
    }

    const getUserInitials = () => {
      const user = authStore.getUser
      if (user && user.username) {
        return user.username.substring(0, 2).toUpperCase()
      }
      return 'U'
    }

    return {
      navItems,
      isActive,
      handleLogout,
      getUserInitials,
      authStore
    }
  }
}
</script>

<style scoped>
.nav-item {
  @apply flex items-center gap-3 px-3 py-2 text-sm text-gray-700 rounded-lg hover:bg-gray-100 transition-colors;
}

.nav-item-active {
  @apply bg-orange-100 text-orange-700 font-medium;
}
</style>
