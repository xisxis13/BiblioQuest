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

      <!-- Liste des utilisateurs -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
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

          <!-- Statistiques -->
          <div class="grid grid-cols-3 gap-4 mb-4">
            <div class="text-center">
              <p class="text-xl font-bold text-gray-900">{{ user.booksCount }}</p>
              <p class="text-xs text-gray-600">Livres</p>
            </div>
            <div class="text-center">
              <p class="text-xl font-bold text-gray-900">{{ user.readBooks }}</p>
              <p class="text-xs text-gray-600">Lus</p>
            </div>
            <div class="text-center">
              <p class="text-xl font-bold text-gray-900">{{ user.followers }}</p>
              <p class="text-xs text-gray-600">Abonnés</p>
            </div>
          </div>

          <!-- Livres récents -->
          <div class="mb-4">
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
              @click="toggleFollow(user)"
              class="flex items-center justify-center px-3 py-2 rounded-lg transition-colors"
              :class="user.isFollowing 
                ? 'bg-orange-50 border border-orange-300 text-orange-700 hover:bg-orange-100' 
                : 'border border-gray-300 text-gray-700 hover:bg-gray-50'"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
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

    // Données fictives d'utilisateurs
    const users = ref([
      {
        id: 1,
        username: 'Alice Martin',
        email: 'alice@example.com',
        isOnline: true,
        lastSeen: new Date(Date.now() - 5 * 60 * 1000),
        booksCount: 42,
        readBooks: 28,
        followers: 156,
        isFollowing: false,
        recentBooks: [
          { id: 1, title: 'Le Petit Prince', cover: null },
          { id: 2, title: '1984', cover: null },
          { id: 3, title: 'L\'Étranger', cover: null },
          { id: 4, title: 'Harry Potter', cover: null }
        ]
      },
      {
        id: 2,
        username: 'Bob Dupont',
        email: 'bob@example.com',
        isOnline: false,
        lastSeen: new Date(Date.now() - 30 * 60 * 1000),
        booksCount: 38,
        readBooks: 22,
        followers: 89,
        isFollowing: true,
        recentBooks: [
          { id: 5, title: 'Dune', cover: null },
          { id: 6, title: 'Foundation', cover: null }
        ]
      },
      {
        id: 3,
        username: 'Claire Moreau',
        email: 'claire@example.com',
        isOnline: true,
        lastSeen: new Date(),
        booksCount: 67,
        readBooks: 45,
        followers: 234,
        isFollowing: false,
        recentBooks: [
          { id: 7, title: 'Pride and Prejudice', cover: null },
          { id: 8, title: 'Jane Eyre', cover: null },
          { id: 9, title: 'Wuthering Heights', cover: null }
        ]
      },
      {
        id: 4,
        username: 'David Chen',
        email: 'david@example.com',
        isOnline: false,
        lastSeen: new Date(Date.now() - 2 * 60 * 60 * 1000),
        booksCount: 29,
        readBooks: 18,
        followers: 67,
        isFollowing: false,
        recentBooks: [
          { id: 10, title: 'The Hobbit', cover: null },
          { id: 11, title: 'LOTR', cover: null }
        ]
      },
      {
        id: 5,
        username: 'Emma Wilson',
        email: 'emma@example.com',
        isOnline: true,
        lastSeen: new Date(),
        booksCount: 53,
        readBooks: 31,
        followers: 178,
        isFollowing: true,
        recentBooks: [
          { id: 12, title: 'The Great Gatsby', cover: null },
          { id: 13, title: 'To Kill a Mockingbird', cover: null },
          { id: 14, title: 'Brave New World', cover: null }
        ]
      },
      {
        id: 6,
        username: 'François Dubois',
        email: 'francois@example.com',
        isOnline: false,
        lastSeen: new Date(Date.now() - 24 * 60 * 60 * 1000),
        booksCount: 71,
        readBooks: 52,
        followers: 298,
        isFollowing: false,
        recentBooks: [
          { id: 15, title: 'Les Misérables', cover: null },
          { id: 16, title: 'Notre-Dame de Paris', cover: null }
        ]
      }
    ])

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
      // Créer ou obtenir la conversation avec cet utilisateur
      await chatStore.getOrCreateConversation(user.id, {
        id: user.id,
        username: user.username,
        email: user.email,
        avatar: null
      })
      
      // Rediriger vers la page de chat
      router.push(`/chat/${user.id}`)
    }

    const viewProfile = (user) => {
      router.push(`/profile/${user.id}`)
    }

    const toggleFollow = (user) => {
      user.isFollowing = !user.isFollowing
      if (user.isFollowing) {
        user.followers++
      } else {
        user.followers--
      }
    }

    // Initialiser le store de chat
    onMounted(() => {
      chatStore.initializeChat()
    })

    return {
      searchQuery,
      selectedFilter,
      currentPage,
      filters,
      filteredUsers,
      totalPages,
      visiblePages,
      getUserInitials,
      formatLastSeen,
      openChat,
      viewProfile,
      toggleFollow
    }
  }
}
</script>
