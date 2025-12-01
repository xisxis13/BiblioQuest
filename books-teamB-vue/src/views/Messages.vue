<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <HeaderComponent />
    <Sidebar />
    <div class="ml-44 pt-20 px-6 py-8">
      <div class="max-w-7xl mx-auto">
        <!-- Header de la page -->
        <div class="mb-8">
          <h1 class="text-4xl font-bold text-[#8B4513] mb-2">💬 Messagerie</h1>
          <p class="text-gray-600">Vos conversations privées avec les autres utilisateurs</p>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- Liste des conversations -->
          <div class="lg:col-span-1">
            <div class="bg-white rounded-2xl border border-gray-200 overflow-hidden">
              <div class="p-4 border-b border-gray-200">
                <h3 class="font-semibold text-gray-900">Conversations</h3>
              </div>
              
              <!-- Recherche -->
              <div class="p-4 border-b border-gray-200">
                <input 
                  type="text" 
                  placeholder="Rechercher une conversation..." 
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-orange-500"
                  v-model="searchQuery"
                />
              </div>

              <!-- Liste des conversations -->
              <div class="max-h-96 overflow-y-auto">
                <div v-if="conversations.length === 0" class="p-8 text-center text-gray-500">
                  <svg class="w-12 h-12 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                  </svg>
                  <p class="text-sm">Aucune conversation</p>
                  <p class="text-xs text-gray-400 mt-1">Commencez à discuter depuis la page Communauté</p>
                </div>

                <div 
                  v-for="conversation in filteredConversations" 
                  :key="conversation.id"
                  @click="selectConversation(conversation)"
                  class="p-4 border-b border-gray-100 hover:bg-gray-50 cursor-pointer transition-colors"
                  :class="{ 'bg-orange-50 border-orange-200': selectedConversation?.id === conversation.id }"
                >
                  <div class="flex items-center space-x-3">
                    <div class="relative">
                      <div class="w-12 h-12 bg-gradient-to-br from-purple-400 to-blue-500 rounded-full flex items-center justify-center text-white font-semibold">
                        {{ getUserInitials(conversation.otherUser.username) }}
                      </div>
                      <div v-if="conversation.otherUser.isOnline" class="absolute -bottom-1 -right-1 w-4 h-4 bg-green-400 border-2 border-white rounded-full"></div>
                    </div>
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center justify-between">
                        <p class="font-medium text-gray-900 truncate">{{ conversation.otherUser.username }}</p>
                        <span class="text-xs text-gray-500">{{ formatTime(conversation.lastMessageAt) }}</span>
                      </div>
                      <p class="text-sm text-gray-600 truncate">{{ conversation.lastMessage }}</p>
                      <div v-if="conversation.unreadCount > 0" class="inline-flex items-center justify-center w-5 h-5 bg-orange-500 text-white text-xs rounded-full mt-1">
                        {{ conversation.unreadCount }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Zone de chat -->
          <div class="lg:col-span-2">
            <div v-if="!selectedConversation" class="bg-white rounded-2xl border border-gray-200 h-96 flex items-center justify-center">
              <div class="text-center text-gray-500">
                <svg class="w-16 h-16 mx-auto mb-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                </svg>
                <p class="text-lg font-medium">Sélectionnez une conversation</p>
                <p class="text-sm text-gray-400">Choisissez une conversation pour commencer à discuter</p>
              </div>
            </div>

            <!-- Chat privé -->
            <PrivateChatWindow 
              v-else
              :conversation="selectedConversation"
              @message-sent="onMessageSent"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import Sidebar from '@/components/Sidebar.vue'
import HeaderComponent from '@/components/HeaderComponent.vue'
import PrivateChatWindow from '@/components/PrivateChatWindow.vue'

export default {
  name: 'MessagesPage',
  components: {
    Sidebar,
    HeaderComponent,
    PrivateChatWindow
  },
  setup() {
    const route = useRoute()
    const authStore = useAuthStore()
    const conversations = ref([])
    const selectedConversation = ref(null)
    const searchQuery = ref('')

    // Conversations filtrées par recherche
    const filteredConversations = computed(() => {
      if (!searchQuery.value) return conversations.value
      
      const query = searchQuery.value.toLowerCase()
      return conversations.value.filter(conv => 
        conv.otherUser.username.toLowerCase().includes(query)
      )
    })

    // Charger les conversations depuis la DB
    const loadConversations = async () => {
      try {
        // TODO: Charger les vraies conversations depuis l'API
        // Pour l'instant, liste vide jusqu'à implémentation complète
        conversations.value = []
      } catch (error) {
        console.error('Erreur lors du chargement des conversations:', error)
      }
    }

    // Sélectionner une conversation
    const selectConversation = (conversation) => {
      selectedConversation.value = conversation
      // Marquer comme lu
      conversation.unreadCount = 0
    }

    // Gérer l'envoi de message
    const onMessageSent = (message) => {
      if (selectedConversation.value) {
        selectedConversation.value.lastMessage = message.content
        selectedConversation.value.lastMessageAt = new Date()
      }
    }

    // Utilitaires
    const getUserInitials = (username) => {
      if (!username) return '?'
      return username.substring(0, 2).toUpperCase()
    }

    const formatTime = (date) => {
      if (!date) return ''
      const now = new Date()
      const diff = now - date
      
      if (diff < 60 * 1000) return 'À l\'instant'
      if (diff < 60 * 60 * 1000) return `${Math.floor(diff / (60 * 1000))}m`
      if (diff < 24 * 60 * 60 * 1000) return `${Math.floor(diff / (60 * 60 * 1000))}h`
      return date.toLocaleDateString()
    }

    onMounted(() => {
      loadConversations()
      
      // Si un utilisateur est passé en paramètre, créer/ouvrir la conversation
      const userId = route.query.userId
      const username = route.query.username
      
      if (userId && username) {
        // Créer une nouvelle conversation ou la trouver
        const newConversation = {
          id: `conv_${userId}`,
          otherUser: {
            id: parseInt(userId),
            username: username,
            isOnline: true // À déterminer plus tard
          },
          lastMessage: '',
          lastMessageAt: new Date(),
          unreadCount: 0
        }
        
        // Ajouter à la liste si pas déjà présente
        const existing = conversations.value.find(c => c.otherUser.id === parseInt(userId))
        if (!existing) {
          conversations.value.unshift(newConversation)
        }
        
        // Sélectionner la conversation
        selectedConversation.value = existing || newConversation
      }
    })

    return {
      conversations,
      selectedConversation,
      searchQuery,
      filteredConversations,
      selectConversation,
      onMessageSent,
      getUserInitials,
      formatTime
    }
  }
}
</script>
