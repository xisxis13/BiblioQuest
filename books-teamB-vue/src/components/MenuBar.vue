<template>
  <div :class="['h-full flex flex-col py-3 gap-3 transition-all duration-300', uiStore.isMenuCollapsed ? 'w-16' : 'w-48']">
    <!-- Navigation -->
    <nav class="flex-1 space-y-1">
      <MenuBarItem
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        :label="item.label"
        :icon="item.icon"
        :collapsed="uiStore.isMenuCollapsed"
      />
    </nav>

    <!-- Logout -->
    <div class="pt-3 border-t border-border">
      <button
        @click="handleLogout"
        :class="[
          'flex items-center w-full py-3 gap-2 rounded-2xl transition-colors duration-200 text-secondary-text hover:bg-accent/10 hover:text-primary-text group cursor-pointer',
          uiStore.isMenuCollapsed ? 'px-2 justify-center' : 'px-4'
        ]"
        :title="uiStore.isMenuCollapsed ? 'Se déconnecter' : ''"
      >
        <LogOut class="w-5 h-5 hover:stroke-primary-text" />
        <span v-if="!uiStore.isMenuCollapsed">Se déconnecter</span>
      </button>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/authStore'
import { useUiStore } from '@/stores/uiStore'
import { useRouter } from 'vue-router'
import { onMounted } from 'vue'
import MenuBarItem from './MenuBarItem.vue'
import {
  Home,
  Compass,
  Library,
  User,
  LogOut,
  Users,
  ChartNoAxesCombined
} from 'lucide-vue-next'

export default {
  name: 'MenuBar',
  components: {
    MenuBarItem,
    LogOut
  },
  setup() {
    const authStore = useAuthStore()
    const uiStore = useUiStore()
    const router = useRouter()

    const navItems = [
      { path: '/', label: 'Accueil', icon: Home },
      { path: '/catalog', label: 'Catalogue', icon: Compass },
      { path: '/my-library', label: 'Ma bibliothèque', icon: Library },
      { path: '/reading-tracker', label: 'Suivi de lecture', icon: ChartNoAxesCombined },
      { path: '/community', label: 'Communauté', icon: Users },
      { path: '/profile', label: 'Profile', icon: User }
    ]

    const handleLogout = () => {
      authStore.logout()
      router.push('/login')
      console.log("redirect to login")
    }

    // Initialize menu state from localStorage
    onMounted(() => {
      uiStore.initializeMenuState()
    })

    return {
      navItems,
      handleLogout,
      authStore,
      uiStore
    }
  }
}
</script>
