<template>
  <div class="w-48 h-full flex flex-col py-3 gap-3">
    <!-- Navigation -->
    <nav class="flex-1 space-y-1">
      <MenuBarItem
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        :label="item.label"
        :icon="item.icon"
      />
    </nav>

    <!-- Logout -->
    <div class="pt-3 border-t border-border">
      <button 
        @click="handleLogout"
        class="flex items-center w-full px-4 py-3 gap-2 rounded-2xl transition-colors duration-200 text-secondary-text hover:bg-accent/10 hover:text-primary-text group cursor-pointer"
      >
        <LogOut class="w-5 h-5 hover:stroke-primary-text" />
        <span>Se déconnecter</span>
      </button>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'
import MenuBarItem from './MenuBarItem.vue'
import { 
  Home, 
  Compass, 
  Library, 
  ShoppingBag, 
  User,
  LogOut 
} from 'lucide-vue-next'

export default {
  name: 'MenuBar',
  components: {
    MenuBarItem,
    LogOut
  },
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()

    const navItems = [
      { path: '/', label: 'Accueil', icon: Home },
      { path: '/catalog', label: 'Catalogue', icon: Compass },
      { path: '/my-library', label: 'Ma bibliothèque', icon: Library },
      { path: '/marketplace', label: 'Marketplace', icon: ShoppingBag },
      { path: '/profile', label: 'Profile', icon: User }
    ]

    const handleLogout = async () => {
      await authStore.logout()
      router.push('/login')
    }

    return {
      navItems,
      handleLogout,
      authStore
    }
  }
}
</script>