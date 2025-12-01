<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <HeaderComponent />
    <Sidebar />
    <div class="ml-44 pt-20 px-6 py-8">
      <!-- En-tête du profil -->
      <div class="max-w-4xl mx-auto">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
          <!-- Bannière -->
          <div class="h-32 bg-gradient-to-r from-orange-400 to-orange-600"></div>
          
          <!-- Informations du profil -->
          <div class="px-6 pb-6">
            <div class="flex items-start justify-between -mt-16">
              <div class="flex items-end gap-4">
                <!-- Avatar -->
                <div class="w-24 h-24 bg-white rounded-full border-4 border-white shadow-lg flex items-center justify-center">
                  <span v-if="user" class="text-2xl font-bold text-orange-600">{{ getUserInitials() }}</span>
                  <div v-else class="w-6 h-6 bg-gray-300 rounded-full animate-pulse"></div>
                </div>
                
                <!-- Infos utilisateur -->
                <div class="mb-2">
                  <div v-if="loading" class="animate-pulse">
                    <div class="h-8 bg-gray-200 rounded w-48 mb-2"></div>
                    <div class="h-4 bg-gray-200 rounded w-64"></div>
                  </div>
                  <div v-else-if="user">
                    <h1 class="text-2xl font-bold text-gray-900">{{ user.username }}</h1>
                    <p class="text-gray-600">{{ user.email }}</p>
                  </div>
                  <div v-else-if="error" class="text-red-600">
                    {{ error }}
                  </div>
                  <div v-if="user" class="flex items-center gap-4 mt-2 text-sm text-gray-500">
                    <span class="flex items-center gap-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                      </svg>
                      {{ user?.booksCount || 0 }} livres
                    </span>
                    <span class="flex items-center gap-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      Membre depuis {{ user ? formatDate(user.createdAt) : '' }}
                    </span>
                  </div>
                </div>
              </div>
              
              <!-- Actions -->
              <div class="flex gap-3 mt-4" v-if="!isOwnProfile">
                <!-- Bouton Envoyer un message -->
                <button 
                  @click="openChat"
                  class="flex items-center gap-2 px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors font-medium"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                  </svg>
                  Envoyer un message
                </button>
                
                <!-- Bouton Suivre -->
                <button 
                  @click="toggleFollow"
                  class="flex items-center gap-2 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors font-medium"
                  :class="{ 'bg-orange-50 border-orange-300 text-orange-700': isFollowing }"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                  {{ isFollowing ? 'Suivi' : 'Suivre' }}
                </button>
              </div>
              
              <!-- Actions pour son propre profil -->
              <div class="flex gap-3 mt-4" v-else>
                <button 
                  @click="editProfile"
                  class="flex items-center gap-2 px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors font-medium"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                  Modifier le profil
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Statistiques -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mt-6">
          <div class="bg-white rounded-xl p-6 border border-gray-200">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
              </div>
              <div>
                <p class="text-2xl font-bold text-gray-900">{{ user?.booksCount || 0 }}</p>
                <p class="text-sm text-gray-600">Livres possédés</p>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-xl p-6 border border-gray-200">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div>
                <p class="text-2xl font-bold text-gray-900">{{ user?.readBooks || 0 }}</p>
                <p class="text-sm text-gray-600">Livres lus</p>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-xl p-6 border border-gray-200">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
              <div>
                <p class="text-2xl font-bold text-gray-900">{{ user?.followersCount || 0 }}</p>
                <p class="text-sm text-gray-600">Abonnés</p>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-xl p-6 border border-gray-200">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
                </svg>
              </div>
              <div>
                <p class="text-2xl font-bold text-gray-900">{{ user?.followingCount || 0 }}</p>
                <p class="text-sm text-gray-600">Abonnements</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Bibliothèque récente -->
        <div class="bg-white rounded-xl border border-gray-200 mt-6">
          <div class="p-6 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900">Livres récents</h2>
          </div>
          <div class="p-6">
            <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
              <div 
                v-for="book in recentBooks" 
                :key="book.id"
                class="group cursor-pointer"
              >
                <div class="aspect-[3/4] bg-gray-200 rounded-lg mb-2 overflow-hidden">
                  <img 
                    v-if="book.cover" 
                    :src="book.cover" 
                    :alt="book.title"
                    class="w-full h-full object-cover group-hover:scale-105 transition-transform"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center">
                    <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                    </svg>
                  </div>
                </div>
                <h3 class="text-sm font-medium text-gray-900 truncate">{{ book.title }}</h3>
                <p class="text-xs text-gray-600 truncate">{{ book.author }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import userService from '@/services/userService'
import Sidebar from '@/components/Sidebar.vue'
import HeaderComponent from '@/components/HeaderComponent.vue'

export default {
  name: 'ProfilePage',
  components: {
    Sidebar,
    HeaderComponent
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const authStore = useAuthStore()
    
    const user = ref(null)
    const loading = ref(false)
    const error = ref(null)
    const isFollowing = ref(false)
    const recentBooks = ref([
      { id: 1, title: 'Le Petit Prince', author: 'Antoine de Saint-Exupéry', cover: null },
      { id: 2, title: '1984', author: 'George Orwell', cover: null },
      { id: 3, title: 'L\'Étranger', author: 'Albert Camus', cover: null },
      { id: 4, title: 'Harry Potter', author: 'J.K. Rowling', cover: null },
      { id: 5, title: 'Le Seigneur des Anneaux', author: 'J.R.R. Tolkien', cover: null },
      { id: 6, title: 'Dune', author: 'Frank Herbert', cover: null }
    ])

    // Vérifier si c'est le profil de l'utilisateur connecté
    const isOwnProfile = computed(() => {
      const userId = route.params.id
      return !userId || userId === authStore.getUser?.id?.toString()
    })

    const getUserInitials = () => {
      if (user.value && user.value.username) {
        return user.value.username.substring(0, 2).toUpperCase()
      }
      return 'U'
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR', { 
        year: 'numeric', 
        month: 'long' 
      })
    }

    const openChat = () => {
      // Rediriger vers la page de chat avec l'ID de l'utilisateur
      router.push(`/chat/${user.value.id}`)
    }

    const toggleFollow = () => {
      isFollowing.value = !isFollowing.value
      // Ici on ajouterait la logique pour suivre/ne plus suivre l'utilisateur
    }

    const editProfile = () => {
      router.push('/edit-profile')
    }

    // Fonction pour charger les données utilisateur
    const loadUserProfile = async (userId) => {
      try {
        loading.value = true
        error.value = null
        
        let userData
        if (userId) {
          // Charger un utilisateur spécifique
          userData = await userService.getUserById(userId)
        } else {
          // Utiliser les données de l'utilisateur connecté
          userData = authStore.getUser
        }
        
        user.value = {
          id: userData.id,
          username: userData.username,
          email: userData.email,
          booksCount: userData.booksCount || 0,
          readBooks: 0, // À implémenter plus tard
          followers: 0, // À implémenter plus tard
          following: 0, // À implémenter plus tard
          joinedDate: new Date(), // À implémenter plus tard
          bio: '', // À implémenter plus tard
          favoriteGenres: [], // À implémenter plus tard
          currentlyReading: [], // À implémenter plus tard
          recentlyFinished: [], // À implémenter plus tard
          avatarUrl: userData.avatarUrl || null,
          lastActivityAt: userData.lastActivityAt
        }
      } catch (err) {
        console.error('Erreur lors du chargement du profil:', err)
        error.value = 'Impossible de charger le profil utilisateur'
      } finally {
        loading.value = false
      }
    }

    // Charger les données du profil
    onMounted(() => {
      const userId = route.params.id
      loadUserProfile(userId)
    })

    return {
      user,
      loading,
      error,
      isFollowing,
      recentBooks,
      isOwnProfile,
      getUserInitials,
      formatDate,
      openChat,
      toggleFollow,
      editProfile,
      loadUserProfile
    }
  }
}
</script>
