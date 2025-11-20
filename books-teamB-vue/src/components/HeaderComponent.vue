<template>
  <header class="bg-white border-b border-gray-200 py-3 px-6 fixed top-0 left-0 right-0 z-20">
    <div class="flex items-center justify-between max-w-7xl mx-auto">
      <!-- Logo et titre -->
      <router-link to="/" class="flex items-center gap-3 hover:opacity-80 transition-opacity cursor-pointer">
        <div class="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center border-2 border-orange-200">
          <img src="@/assets/BCO.png" alt="BiblioQuest Logo" class="h-7 w-7 object-contain" />
        </div>
        <span class="text-2xl font-bold text-[#8B4513]">BiblioQuest</span>
      </router-link>

      <!-- Barre de recherche centrale -->
      <div class="flex-1 max-w-md mx-8">
        <div class="relative">
          <input 
            type="text" 
            placeholder="Rechercher un livre, un auteur..." 
            class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-orange-500 text-sm"
            v-model="searchQuery"
            @keyup.enter="handleSearch"
          />
          <svg class="absolute left-3 top-2.5 h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
      </div>

      <!-- Actions utilisateur -->
      <div class="flex items-center gap-4">
        <!-- Notifications -->
        <button class="relative p-2 text-gray-600 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-colors">
          <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-5 5v-5zM10.5 3.5a6 6 0 0 1 6 6v2l1.5 3h-15l1.5-3v-2a6 6 0 0 1 6-6z" />
          </svg>
          <span class="absolute -top-1 -right-1 h-4 w-4 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">3</span>
        </button>

        <!-- Profil utilisateur -->
        <div class="relative">
          <div 
            @click="toggleDropdown"
            class="flex items-center gap-2 px-3 py-2 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
          >
            <div class="w-8 h-8 bg-orange-500 rounded-full flex items-center justify-center">
              <span class="text-white text-sm font-semibold">{{ getUserInitials() }}</span>
            </div>
            <div class="hidden sm:block">
              <div class="text-sm font-medium text-gray-900">{{ authStore.getUser?.username || 'Utilisateur' }}</div>
              <div class="text-xs text-gray-500">Administrateur</div>
            </div>
            <svg 
              class="h-4 w-4 text-gray-400 transition-transform duration-200"
              :class="{ 'rotate-180': showDropdown }"
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </div>

          <!-- Menu dropdown -->
          <div 
            v-if="showDropdown"
            class="absolute right-0 mt-2 w-64 bg-white rounded-xl shadow-lg border border-gray-200 py-2 z-50 max-h-80 overflow-y-auto"
            @click.stop
          >
            <!-- Profil utilisateur -->
            <div class="px-4 py-3 border-b border-gray-100">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-orange-500 rounded-full flex items-center justify-center">
                  <span class="text-white font-semibold">{{ getUserInitials() }}</span>
                </div>
                <div>
                  <div class="font-medium text-gray-900">{{ authStore.getUser?.username || 'Utilisateur' }}</div>
                  <div class="text-sm text-gray-500">{{ authStore.getUser?.email || 'admin@example.com' }}</div>
                </div>
              </div>
            </div>

            <!-- Menu items -->
            <div class="py-1">
              <!-- Mon profil -->
              <router-link 
                to="/profile" 
                class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                @click="closeDropdown"
              >
                <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                Mon profil
              </router-link>

              <!-- Ma bibliothèque -->
              <router-link 
                to="/my-library" 
                class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                @click="closeDropdown"
              >
                <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
                Ma bibliothèque
              </router-link>

              <!-- Statistiques -->
              <router-link 
                to="/reading-tracker" 
                class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                @click="closeDropdown"
              >
                <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
                Mes statistiques
              </router-link>
            </div>

            <!-- Séparateur -->
            <div class="border-t border-gray-100 my-1"></div>

            <!-- Section Administration -->
            <div class="py-1">
              <div class="px-4 py-2 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Administration
              </div>
              
              <!-- Gestion des livres -->
              <button 
                class="w-full flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors text-left"
                @click="closeDropdown"
              >
                <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
                Gestion des livres
              </button>

              <!-- Gestion des utilisateurs -->
              <button 
                class="w-full flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors text-left"
                @click="closeDropdown"
              >
                <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
                </svg>
                Gestion des utilisateurs
              </button>

              <!-- Statistiques globales -->
              <button 
                class="w-full flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors text-left"
                @click="closeDropdown"
              >
                <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 8v8m-4-5v5m-4-2v2m-2 4h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                Statistiques globales
              </button>
            </div>

            <!-- Séparateur -->
            <div class="border-t border-gray-100 my-1"></div>

            <!-- Section Paramètres -->
            <div class="py-1">
              <div class="px-4 py-2 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Paramètres
              </div>
              
              <!-- Préférences -->
              <button 
                class="w-full flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors text-left"
                @click="closeDropdown"
              >
                <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                Préférences
              </button>

              <!-- Notifications -->
              <button 
                class="w-full flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors text-left"
                @click="closeDropdown"
              >
                <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-5 5v-5zM10.5 3.5a6 6 0 0 1 6 6v2l1.5 3h-15l1.5-3v-2a6 6 0 0 1 6-6z" />
                </svg>
                Notifications
              </button>

              <!-- Aide -->
              <button 
                class="w-full flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors text-left"
                @click="closeDropdown"
              >
                <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                Aide & Support
              </button>
            </div>

            <!-- Séparateur -->
            <div class="border-t border-gray-100 my-1"></div>

            <!-- Déconnexion -->
            <button 
              @click="handleLogout"
              class="w-full flex items-center gap-3 px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors text-left"
            >
              <svg class="h-4 w-4 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
              Se déconnecter
            </button>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'

export default {
  name: 'HeaderComponent',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()

    return {
      authStore,
      router
    }
  },
  data() {
    return {
      searchQuery: '',
      showDropdown: false
    }
  },
  mounted() {
    // Fermer le dropdown quand on clique ailleurs
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  methods: {
    handleSearch() {
      if (this.searchQuery.trim()) {
        // Rediriger vers la page de recherche avec la query
        this.router.push({
          path: '/catalogue',
          query: { search: this.searchQuery.trim() }
        })
      }
    },
    
    getUserInitials() {
      const username = this.authStore.getUser?.username || 'U'
      return username.charAt(0).toUpperCase()
    },

    toggleDropdown() {
      this.showDropdown = !this.showDropdown
    },

    closeDropdown() {
      this.showDropdown = false
    },

    handleClickOutside(event) {
      // Fermer le dropdown si on clique en dehors
      if (!this.$el.contains(event.target)) {
        this.showDropdown = false
      }
    },

    async handleLogout() {
      try {
        await this.authStore.logout()
        this.closeDropdown()
        this.router.push('/login')
      } catch (error) {
        console.error('Erreur lors de la déconnexion:', error)
      }
    }
  }
}
</script>

<style scoped>
/* Animation pour le focus de la barre de recherche */
input:focus {
  transform: scale(1.02);
  transition: transform 0.2s ease-in-out;
}

/* Animation pour les boutons */
button:hover {
  transform: translateY(-1px);
  transition: transform 0.2s ease-in-out;
}

/* Animation pour le dropdown */
.dropdown-enter-active, .dropdown-leave-active {
  transition: all 0.2s ease-in-out;
}

.dropdown-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Scrollbar personnalisée pour le dropdown */
.overflow-y-auto::-webkit-scrollbar {
  width: 4px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Responsive - masquer certains éléments sur mobile */
@media (max-width: 640px) {
  .flex-1.max-w-md {
    max-width: 200px;
  }
  
  input::placeholder {
    font-size: 12px;
  }
  
  /* Dropdown plus petit sur mobile */
  .w-64 {
    width: 16rem;
  }
}
</style>
