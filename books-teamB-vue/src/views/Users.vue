<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <HeaderComponent />
    <Sidebar />
    <div class="ml-44 pt-20 px-6 py-8">
      <!-- En-tête -->
      <div class="mb-8">
        <h1 class="text-4xl font-bold text-[#8B4513] mb-2">Communauté</h1>
        <p class="text-gray-600">Découvrez les autres lecteurs de BiblioQuest</p>
      </div>

      <!-- Barre de recherche et filtres -->
      <div class="bg-white rounded-2xl border border-gray-200 p-6 mb-6">
        <div class="flex flex-col md:flex-row gap-4">
          <!-- Recherche -->
          <div class="flex-1 relative">
            <input 
              type="text" 
              placeholder="Rechercher un utilisateur..." 
              class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-orange-500"
              v-model="searchQuery"
            />
            <svg class="absolute left-3 top-3.5 h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>

          <!-- Filtres -->
          <div class="flex gap-2">
            <button 
              v-for="filter in filters" 
              :key="filter.value"
              @click="selectedFilter = filter.value"
              class="px-4 py-2 rounded-lg font-medium transition-colors"
              :class="selectedFilter === filter.value 
                ? 'bg-orange-500 text-white' 
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
            >
              {{ filter.label }}
            </button>
          </div>
        </div>
      </div>

      <!-- Message d'erreur -->
      <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
        <div class="flex items-center">
          <svg class="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
          <p class="text-red-800">{{ error }}</p>
        </div>
      </div>

      <!-- Indicateur de chargement -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500"></div>
        <p class="ml-3 text-gray-600">Chargement des utilisateurs...</p>
      </div>

      <!-- Liste des utilisateurs -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div 
          v-for="user in filteredUsers" 
          :key="user.id"
          class="bg-white rounded-2xl border border-gray-200 p-6 hover:shadow-lg transition-all group"
        >
          <!-- En-tête du profil -->
          <div class="flex items-center gap-4 mb-4">
            <div class="relative">
              <div class="w-16 h-16 bg-orange-500 rounded-full flex items-center justify-center">
                <span class="text-white text-xl font-bold">{{ getUserInitials(user.username) }}</span>
              </div>
              <div 
                v-if="user.isOnline"
                class="absolute -bottom-1 -right-1 w-5 h-5 bg-green-500 border-2 border-white rounded-full"
              ></div>
            </div>
            
            <div class="flex-1">
              <h3 class="font-semibold text-gray-900 text-lg">{{ user.username }}</h3>
              <p class="text-gray-600 text-sm">{{ user.email }}</p>
              <div class="flex items-center gap-2 mt-1">
                <span class="text-xs text-gray-500">
                  {{ user.isOnline ? 'En ligne' : `Vu ${formatLastSeen(user.lastSeen)}` }}
                </span>
              </div>
            </div>
          </div>

          <!-- Statistiques (toujours affichées) -->
          <div class="grid grid-cols-3 gap-4 mb-4">
            <div class="text-center">
              <p class="text-xl font-bold text-gray-900">{{ user.booksCount || 0 }}</p>
              <p class="text-xs text-gray-600">Livres</p>
            </div>
            <div class="text-center">
              <p class="text-xl font-bold text-gray-900">{{ user.readBooks || 0 }}</p>
              <p class="text-xs text-gray-600">Lus</p>
            </div>
            <div class="text-center">
              <p class="text-xl font-bold text-gray-900">{{ user.followers || 0 }}</p>
              <p class="text-xs text-gray-600">{{ user.followers === 1 ? 'Abonné' : 'Abonnés' }}</p>
            </div>
          </div>

          <!-- Livres récents (masqués si aucun livre) -->
          <div v-if="user.recentBooks && user.recentBooks.length > 0" class="mb-4">
            <h4 class="text-sm font-medium text-gray-900 mb-2">Livres récents</h4>
            <div class="flex gap-2">
              <div 
                v-for="book in user.recentBooks.slice(0, 3)" 
                :key="book.id"
                class="w-8 h-10 bg-gray-200 rounded flex-shrink-0 overflow-hidden"
              >
                <img 
                  v-if="book.cover" 
                  :src="book.cover" 
                  :alt="book.title"
                  class="w-full h-full object-cover"
                />
                <div v-else class="w-full h-full flex items-center justify-center">
                  <svg class="w-3 h-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                  </svg>
                </div>
              </div>
              <div v-if="user.recentBooks.length > 3" class="w-8 h-10 bg-gray-100 rounded flex items-center justify-center">
                <span class="text-xs text-gray-500">+{{ user.recentBooks.length - 3 }}</span>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex gap-2">
            <button 
              @click="openChat(user)"
              class="flex-1 flex items-center justify-center gap-2 px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors font-medium"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
              Message
            </button>
            
            <button 
              @click="viewProfile(user)"
              class="flex items-center justify-center gap-2 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors font-medium"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              Profil
            </button>
            
            <button 
              v-if="!isCurrentUser(user)"
              @click="toggleFollow(user)"
              :disabled="user.followLoading"
              class="flex items-center justify-center gap-2 px-4 py-2 rounded-lg transition-colors font-medium min-w-[100px]"
              :class="user.isFollowing 
                ? 'bg-orange-500 text-white hover:bg-orange-600' 
                : 'border border-orange-500 text-orange-500 hover:bg-orange-50'"
            >
              <svg v-if="user.followLoading" class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="m4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <svg v-else-if="user.isFollowing" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
              <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
              <span>{{ user.isFollowing ? 'Suivi' : 'Suivre' }}</span>
            </button>
          </div>
        </div>
      </div>

      <!-- État vide -->
      <div v-if="filteredUsers.length === 0" class="text-center py-12">
        <div class="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
        </div>
        <h3 class="text-lg font-medium text-gray-900 mb-2">Aucun utilisateur trouvé</h3>
        <p class="text-gray-500">Essayez de modifier vos critères de recherche</p>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="flex justify-center mt-8">
        <div class="flex gap-2">
          <button 
            @click="currentPage--"
            :disabled="currentPage === 1"
            class="px-3 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Précédent
          </button>
          
          <button 
            v-for="page in visiblePages" 
            :key="page"
            @click="currentPage = page"
            class="px-3 py-2 rounded-lg font-medium"
            :class="currentPage === page 
              ? 'bg-orange-500 text-white' 
              : 'border border-gray-300 hover:bg-gray-50'"
          >
            {{ page }}
          </button>
          
          <button 
            @click="currentPage++"
            :disabled="currentPage === totalPages"
            class="px-3 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Suivant
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '@/stores/chatStore'
import { useAuthStore } from '@/stores/authStore'
import userService from '@/services/userService'
import Sidebar from '@/components/Sidebar.vue'
import HeaderComponent from '@/components/HeaderComponent.vue'

export default {
  name: 'UsersPage',
  components: {
    Sidebar,
    HeaderComponent
  },
  setup() {
    const router = useRouter()
    const chatStore = useChatStore()
    const authStore = useAuthStore()
    
    const searchQuery = ref('')
    const selectedFilter = ref('all')
    const currentPage = ref(1)
    const usersPerPage = 12
    
    const filters = [
      { value: 'all', label: 'Tous' },
      { value: 'online', label: 'En ligne' },
      { value: 'recent', label: 'Récents' },
      { value: 'following', label: 'Suivis' }
    ]

    // Données des utilisateurs (chargées depuis l'API)
    const users = ref([])
    const loading = ref(false)
    const error = ref(null)

    // Fonction pour déterminer si un utilisateur est en ligne (actif dans les 5 dernières minutes)
    const isUserOnline = (lastActivityAt) => {
      if (!lastActivityAt) return false
      const lastActivity = new Date(lastActivityAt)
      const fiveMinutesAgo = new Date(Date.now() - 5 * 60 * 1000)
      return lastActivity > fiveMinutesAgo
    }

    // Charger le statut d'abonnement pour tous les utilisateurs
    const loadFollowingStatus = async () => {
      const currentUserId = authStore.getUser?.id
      if (!currentUserId) return
      
      for (const user of users.value) {
        try {
          const response = await fetch(`http://localhost:8080/api/follow/status?followerId=${currentUserId}&followedId=${user.id}`)
          if (response.ok) {
            const result = await response.json()
            user.isFollowing = result.isFollowing
            user.followers = result.followersCount || 0
          }
        } catch (error) {
          console.error('Erreur lors du chargement du statut d\'abonnement:', error)
        }
      }
    }

    // Charger les utilisateurs depuis l'API
    const loadUsers = async () => {
      try {
        loading.value = true
        error.value = null
        const response = await userService.getAllUsers()
        
        // Transformer les données et filtrer l'utilisateur connecté + admin
        const currentUserId = authStore.getUser?.id
        users.value = response
          .filter(user => user.id !== currentUserId && user.username !== 'admin') // Exclure l'utilisateur connecté et admin
          .map(user => ({
          id: user.id,
          username: user.username,
          email: user.email,
          isOnline: user.lastActivityAt ? isUserOnline(user.lastActivityAt) : false,
          lastSeen: user.lastActivityAt ? new Date(user.lastActivityAt) : new Date(Date.now() - 24 * 60 * 60 * 1000),
          booksCount: user.booksCount || 0,
          readBooks: 0, // À implémenter plus tard
          followers: user.followersCount || 0,
          following: user.followingCount || 0,
          isFollowing: false, // Sera chargé après
          followLoading: false, // Pour l'état de chargement du bouton
          recentBooks: [], // À implémenter plus tard
          avatar_url: user.avatarUrl || null
        }))
        
        // Charger le statut d'abonnement pour chaque utilisateur
        await loadFollowingStatus()
      } catch (err) {
        console.error('Erreur lors du chargement des utilisateurs:', err)
        error.value = 'Impossible de charger les utilisateurs'
        // En cas d'erreur, utiliser des données de fallback
        users.value = [
          {
            id: 1,
            username: 'Utilisateur de test',
            email: 'test@example.com',
            isOnline: false,
            lastSeen: new Date(),
            booksCount: 0,
            readBooks: 0,
            followers: 0,
            isFollowing: false,
            recentBooks: []
          }
        ]
      } finally {
        loading.value = false
      }
    }

    const filteredUsers = computed(() => {
      let filtered = users.value

      // Filtrer par recherche
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        filtered = filtered.filter(user => 
          user.username.toLowerCase().includes(query) ||
          user.email.toLowerCase().includes(query)
        )
      }

      // Filtrer par statut
      switch (selectedFilter.value) {
        case 'online':
          filtered = filtered.filter(user => user.isOnline)
          break
        case 'recent':
          filtered = filtered.filter(user => {
            const hoursSinceLastSeen = (Date.now() - user.lastSeen) / (1000 * 60 * 60)
            return hoursSinceLastSeen < 24
          })
          break
        case 'following':
          filtered = filtered.filter(user => user.isFollowing)
          break
      }

      // Pagination
      const start = (currentPage.value - 1) * usersPerPage
      const end = start + usersPerPage
      return filtered.slice(start, end)
    })

    const totalPages = computed(() => {
      let filtered = users.value
      
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        filtered = filtered.filter(user => 
          user.username.toLowerCase().includes(query) ||
          user.email.toLowerCase().includes(query)
        )
      }

      switch (selectedFilter.value) {
        case 'online':
          filtered = filtered.filter(user => user.isOnline)
          break
        case 'recent':
          filtered = filtered.filter(user => {
            const hoursSinceLastSeen = (Date.now() - user.lastSeen) / (1000 * 60 * 60)
            return hoursSinceLastSeen < 24
          })
          break
        case 'following':
          filtered = filtered.filter(user => user.isFollowing)
          break
      }

      return Math.ceil(filtered.length / usersPerPage)
    })

    const visiblePages = computed(() => {
      const pages = []
      const start = Math.max(1, currentPage.value - 2)
      const end = Math.min(totalPages.value, currentPage.value + 2)
      
      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      
      return pages
    })

    const getUserInitials = (username) => {
      return username.split(' ').map(n => n[0]).join('').toUpperCase()
    }

    const formatLastSeen = (lastSeen) => {
      const now = new Date()
      const diff = now - lastSeen
      const minutes = Math.floor(diff / (1000 * 60))
      const hours = Math.floor(diff / (1000 * 60 * 60))
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))

      if (minutes < 1) return 'à l\'instant'
      if (minutes < 60) return `il y a ${minutes}min`
      if (hours < 24) return `il y a ${hours}h`
      return `il y a ${days}j`
    }

    const openChat = async (user) => {
      console.log('Ouverture du chat privé avec:', user.username)
      
      // Rediriger vers la messagerie avec l'utilisateur sélectionné
      router.push({
        name: 'messages',
        query: { userId: user.id, username: user.username }
      })
    }

    const viewProfile = (user) => {
      router.push(`/profile/${user.id}`)
    }

    const toggleFollow = async (user) => {
      // Empêcher l'auto-abonnement
      if (isCurrentUser(user)) {
        return
      }
      
      user.followLoading = true
      
      try {
        const currentUserId = authStore.getUser?.id
        if (!currentUserId) {
          console.error('Utilisateur non connecté')
          return
        }
        
        const url = user.isFollowing 
          ? `http://localhost:8080/api/follow/${user.id}?followerId=${currentUserId}`
          : `http://localhost:8080/api/follow/${user.id}?followerId=${currentUserId}`
        
        const method = user.isFollowing ? 'DELETE' : 'POST'
        
        const response = await fetch(url, {
          method: method,
          headers: {
            'Content-Type': 'application/json',
          }
        })
        
        const result = await response.json()
        
        if (result.success) {
          user.isFollowing = result.isFollowing
          // Mettre à jour le compteur si nécessaire
          if (result.isFollowing) {
            user.followers++
          } else {
            user.followers--
          }
        } else {
          // Si l'utilisateur suit déjà, on met à jour l'état local
          if (result.message && result.message.includes('déjà')) {
            user.isFollowing = true
          } else {
            console.error('Erreur:', result.message)
          }
        }
      } catch (error) {
        console.error('Erreur lors de l\'abonnement:', error)
      } finally {
        user.followLoading = false
      }
    }
    
    // Fonction pour vérifier si c'est l'utilisateur actuel
    const isCurrentUser = (user) => {
      const currentUser = authStore.getUser
      return currentUser && currentUser.id === user.id
    }

    // Initialiser le store de chat
    onMounted(() => {
      // Temporairement désactivé pour éviter les erreurs 401
      // chatStore.initializeChat()
      console.log('Chat initialization disabled to prevent logout')
      
      // Charger les utilisateurs depuis l'API
      loadUsers()
    })

    return {
      searchQuery,
      selectedFilter,
      currentPage,
      filters,
      filteredUsers,
      totalPages,
      visiblePages,
      loading,
      error,
      loadUsers,
      getUserInitials,
      formatLastSeen,
      openChat,
      viewProfile,
      toggleFollow,
      isCurrentUser
    }
  }
}
</script>
