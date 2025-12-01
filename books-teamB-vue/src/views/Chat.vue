<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <HeaderComponent />
    <Sidebar />
    
    <!-- Contenu principal -->
    <div class="ml-44 pt-20 px-6 py-8">
      <div class="max-w-7xl mx-auto">
        <!-- Header de la page -->
        <div class="mb-8">
          <div class="flex items-center justify-between">
            <div>
              <h1 class="text-4xl font-bold text-[#8B4513] mb-2">💬 Chat Communauté</h1>
              <p class="text-gray-600">Discutez en temps réel avec la communauté BiblioQuest</p>
            </div>
            
            <!-- Stats rapides -->
            <div class="flex space-x-4">
              <div class="bg-white rounded-xl p-4 shadow-sm border border-gray-200">
                <div class="flex items-center space-x-2">
                  <div class="w-3 h-3 bg-green-400 rounded-full animate-pulse"></div>
                  <span class="text-sm font-medium text-gray-700">{{ connectedUsersCount }} en ligne</span>
                </div>
              </div>
              <div class="bg-white rounded-xl p-4 shadow-sm border border-gray-200">
                <div class="flex items-center space-x-2">
                  <svg class="w-4 h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                  </svg>
                  <span class="text-sm font-medium text-gray-700">{{ messagesCount }} messages</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Layout responsive -->
        <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
          <!-- Chat principal -->
          <div class="lg:col-span-3">
            <ChatWindow 
              :auto-connect="true"
              :show-users="false"
            />
          </div>

          <!-- Sidebar utilisateurs et infos -->
          <div class="lg:col-span-1">
            <div class="bg-white rounded-xl shadow-lg border border-gray-200 p-6">
              <h3 class="font-semibold text-gray-800 mb-4 flex items-center">
                <svg class="w-5 h-5 mr-2 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                Utilisateurs en ligne
              </h3>
              
              <div class="space-y-3">
                <div v-if="connectedUsers.length === 0" class="text-center text-gray-500 py-8">
                  <svg class="w-12 h-12 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                  <p class="text-sm">Aucun utilisateur connecté</p>
                </div>
                
                <div 
                  v-for="user in connectedUsers" 
                  :key="user.username"
                  class="flex items-center space-x-3 p-3 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  <div class="relative">
                    <div class="w-10 h-10 bg-gradient-to-br from-purple-400 to-blue-500 rounded-full flex items-center justify-center text-white font-semibold">
                      {{ getUserInitials(user.username) }}
                    </div>
                    <div class="absolute -bottom-1 -right-1 w-4 h-4 bg-green-400 border-2 border-white rounded-full"></div>
                  </div>
                  <div class="flex-1">
                    <p class="font-medium text-gray-800">{{ user.username }}</p>
                    <p class="text-xs text-green-600">En ligne</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Règles du chat -->
            <div class="bg-white rounded-xl shadow-lg border border-gray-200 p-6 mt-6">
              <h3 class="font-semibold text-gray-800 mb-4 flex items-center">
                <svg class="w-5 h-5 mr-2 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                Règles du chat
              </h3>
              <ul class="text-sm text-gray-600 space-y-2">
                <li class="flex items-start">
                  <span class="text-green-500 mr-2">✓</span>
                  Soyez respectueux envers les autres
                </li>
                <li class="flex items-start">
                  <span class="text-green-500 mr-2">✓</span>
                  Partagez vos découvertes littéraires
                </li>
                <li class="flex items-start">
                  <span class="text-green-500 mr-2">✓</span>
                  Aidez les autres membres
                </li>
                <li class="flex items-start">
                  <span class="text-red-500 mr-2">✗</span>
                  Pas de spam ou de contenu inapproprié
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, onMounted } from 'vue'
import { useWebSocketStore } from '@/stores/websocketStore'
import HeaderComponent from '@/components/HeaderComponent.vue'
import Sidebar from '@/components/Sidebar.vue'
import ChatWindow from '@/components/ChatWindow.vue'

export default {
  name: 'Chat',
  components: {
    HeaderComponent,
    Sidebar,
    ChatWindow
  },
  setup() {
    const websocketStore = useWebSocketStore()

    // Computed properties
    const connectedUsers = computed(() => websocketStore.getConnectedUsers)
    const connectedUsersCount = computed(() => connectedUsers.value.length)
    const messagesCount = computed(() => websocketStore.getMessages.length)

    // Charger les messages historiques au montage
    onMounted(async () => {
      try {
        await websocketStore.loadHistoricalMessages()
        console.log('Messages historiques chargés dans Chat.vue')
      } catch (error) {
        console.error('Erreur lors du chargement des messages historiques:', error)
      }
    })

    // Methods
    const getUserInitials = (username) => {
      if (!username) return '?'
      return username.substring(0, 2).toUpperCase()
    }

    return {
      // Computed
      connectedUsers,
      connectedUsersCount,
      messagesCount,
      
      // Methods
      getUserInitials
    }
  }
}
</script>

<style scoped>
/* Animations personnalisées */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* Responsive adjustments */
@media (max-width: 1024px) {
  .ml-44 {
    margin-left: 0;
  }
}
</style>
