<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <HeaderComponent />
    <Sidebar />
    <div class="ml-44 pt-20 flex h-screen">
      <!-- Sidebar des conversations -->
      <div class="w-72 bg-white border-r border-gray-200 flex flex-col">
      <!-- En-tête du chat -->
      <div class="p-4 border-b border-gray-200">
        <div class="flex items-center justify-between mb-4">
          <h1 class="text-xl font-bold text-gray-900">Messages</h1>
          <button 
            @click="goBack"
            class="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        
        <!-- Barre de recherche -->
        <div class="relative">
          <input 
            type="text" 
            placeholder="Rechercher des conversations..." 
            class="w-full pl-10 pr-4 py-2 bg-gray-100 border-0 rounded-full focus:ring-2 focus:ring-orange-500 focus:bg-white text-sm"
            v-model="searchQuery"
          />
          <svg class="absolute left-3 top-2.5 h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
      </div>

      <!-- Liste des conversations -->
      <div class="flex-1 overflow-y-auto">
        <div 
          v-for="conversation in filteredConversations" 
          :key="conversation.id"
          @click="selectConversation(conversation)"
          class="flex items-center gap-3 p-3 hover:bg-gray-50 cursor-pointer transition-colors"
          :class="{ 'bg-orange-50 border-r-2 border-orange-500': selectedConversation?.id === conversation.id }"
        >
          <!-- Avatar -->
          <div class="relative">
            <div class="w-12 h-12 bg-orange-500 rounded-full flex items-center justify-center">
              <span class="text-white font-semibold">{{ getInitials(conversation.user.username) }}</span>
            </div>
            <div 
              v-if="conversation.user.isOnline"
              class="absolute -bottom-1 -right-1 w-4 h-4 bg-green-500 border-2 border-white rounded-full"
            ></div>
          </div>
          
          <!-- Infos conversation -->
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between">
              <h3 class="font-medium text-gray-900 truncate">{{ conversation.user.username }}</h3>
              <span class="text-xs text-gray-500">{{ formatTime(conversation.lastMessage.timestamp) }}</span>
            </div>
            <div class="flex items-center justify-between">
              <p class="text-sm text-gray-600 truncate">{{ conversation.lastMessage.content }}</p>
              <div 
                v-if="conversation.unreadCount > 0"
                class="w-5 h-5 bg-orange-500 text-white text-xs rounded-full flex items-center justify-center font-medium"
              >
                {{ conversation.unreadCount }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Zone de chat principale -->
    <div class="flex-1 flex flex-col">
      <!-- En-tête de la conversation -->
      <div v-if="selectedConversation" class="bg-white border-b border-gray-200 p-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="relative">
              <div class="w-10 h-10 bg-orange-500 rounded-full flex items-center justify-center">
                <span class="text-white font-semibold">{{ getInitials(selectedConversation.user.username) }}</span>
              </div>
              <div 
                v-if="selectedConversation.user.isOnline"
                class="absolute -bottom-1 -right-1 w-3 h-3 bg-green-500 border-2 border-white rounded-full"
              ></div>
            </div>
            <div>
              <h2 class="font-semibold text-gray-900">{{ selectedConversation.user.username }}</h2>
              <p class="text-sm text-gray-500">
                {{ selectedConversation.user.isOnline ? 'En ligne' : `Vu il y a ${formatTime(selectedConversation.user.lastSeen)}` }}
              </p>
            </div>
          </div>
          
          <!-- Actions -->
          <div class="flex items-center gap-2">
            <button class="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
              </svg>
            </button>
            <button class="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" />
              </svg>
            </button>
            <button class="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Messages -->
      <div v-if="selectedConversation" class="flex-1 overflow-y-auto p-4 space-y-4" ref="messagesContainer">
        <div 
          v-for="message in messages" 
          :key="message.id"
          class="flex"
          :class="{ 'justify-end': message.isOwn, 'justify-start': !message.isOwn }"
        >
          <div class="flex items-end gap-2 max-w-xs lg:max-w-md">
            <!-- Avatar pour les messages reçus -->
            <div 
              v-if="!message.isOwn" 
              class="w-8 h-8 bg-orange-500 rounded-full flex items-center justify-center flex-shrink-0"
            >
              <span class="text-white text-sm font-semibold">{{ getInitials(selectedConversation.user.username) }}</span>
            </div>
            
            <!-- Bulle de message -->
            <div 
              class="px-4 py-2 rounded-2xl"
              :class="{
                'bg-orange-500 text-white': message.isOwn,
                'bg-gray-200 text-gray-900': !message.isOwn
              }"
            >
              <p class="text-sm">{{ message.content }}</p>
              <p 
                class="text-xs mt-1 opacity-70"
                :class="{ 'text-orange-100': message.isOwn, 'text-gray-500': !message.isOwn }"
              >
                {{ formatTime(message.timestamp) }}
              </p>
            </div>
            
            <!-- Avatar pour les messages envoyés -->
            <div 
              v-if="message.isOwn" 
              class="w-8 h-8 bg-gray-400 rounded-full flex items-center justify-center flex-shrink-0"
            >
              <span class="text-white text-sm font-semibold">{{ getInitials(authStore.getUser?.username || 'U') }}</span>
            </div>
          </div>
        </div>
        
        <!-- Indicateur de frappe -->
        <div v-if="isTyping" class="flex justify-start">
          <div class="flex items-end gap-2">
            <div class="w-8 h-8 bg-orange-500 rounded-full flex items-center justify-center">
              <span class="text-white text-sm font-semibold">{{ getInitials(selectedConversation.user.username) }}</span>
            </div>
            <div class="bg-gray-200 px-4 py-2 rounded-2xl">
              <div class="flex space-x-1">
                <div class="w-2 h-2 bg-gray-500 rounded-full animate-bounce"></div>
                <div class="w-2 h-2 bg-gray-500 rounded-full animate-bounce" style="animation-delay: 0.1s"></div>
                <div class="w-2 h-2 bg-gray-500 rounded-full animate-bounce" style="animation-delay: 0.2s"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Zone de saisie -->
      <div v-if="selectedConversation" class="bg-white border-t border-gray-200 p-4">
        <div class="flex items-end gap-3">
          <!-- Boutons d'actions -->
          <div class="flex gap-1">
            <button class="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
              </svg>
            </button>
            <button class="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </button>
          </div>
          
          <!-- Zone de texte -->
          <div class="flex-1 relative">
            <textarea
              v-model="newMessage"
              @keydown.enter.exact.prevent="sendMessage"
              @keydown.shift.enter="newMessage += '\n'"
              @input="handleTyping"
              placeholder="Tapez votre message..."
              rows="1"
              class="w-full px-4 py-2 bg-gray-100 border-0 rounded-full resize-none focus:ring-2 focus:ring-orange-500 focus:bg-white text-sm"
              style="min-height: 40px; max-height: 120px;"
            ></textarea>
          </div>
          
          <!-- Bouton d'envoi -->
          <button 
            @click="sendMessage"
            :disabled="!newMessage.trim()"
            class="p-2 bg-orange-500 text-white rounded-full hover:bg-orange-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
            </svg>
          </button>
        </div>
      </div>

      <!-- État vide -->
      <div v-else class="flex-1 flex items-center justify-center">
        <div class="text-center">
          <div class="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg class="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
            </svg>
          </div>
          <h3 class="text-lg font-medium text-gray-900 mb-2">Sélectionnez une conversation</h3>
          <p class="text-gray-500">Choisissez une conversation dans la liste pour commencer à discuter</p>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import Sidebar from '@/components/Sidebar.vue'
import HeaderComponent from '@/components/HeaderComponent.vue'

export default {
  name: 'ChatPage',
  components: {
    Sidebar,
    HeaderComponent
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const authStore = useAuthStore()
    
    const searchQuery = ref('')
    const selectedConversation = ref(null)
    const newMessage = ref('')
    const isTyping = ref(false)
    const messagesContainer = ref(null)
    
    // Données fictives pour les conversations
    const conversations = ref([
      {
        id: 1,
        user: {
          id: 1,
          username: 'Alice Martin',
          isOnline: true,
          lastSeen: new Date()
        },
        lastMessage: {
          content: 'Salut ! As-tu lu le dernier livre de science-fiction ?',
          timestamp: new Date(Date.now() - 5 * 60 * 1000)
        },
        unreadCount: 2
      },
      {
        id: 2,
        user: {
          id: 2,
          username: 'Bob Dupont',
          isOnline: false,
          lastSeen: new Date(Date.now() - 30 * 60 * 1000)
        },
        lastMessage: {
          content: 'Merci pour la recommandation !',
          timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000)
        },
        unreadCount: 0
      },
      {
        id: 3,
        user: {
          id: 3,
          username: 'Claire Moreau',
          isOnline: true,
          lastSeen: new Date()
        },
        lastMessage: {
          content: 'J\'ai adoré ce livre, on peut en discuter ?',
          timestamp: new Date(Date.now() - 24 * 60 * 60 * 1000)
        },
        unreadCount: 1
      }
    ])
    
    // Messages de la conversation sélectionnée
    const messages = ref([])
    
    // Messages fictifs par conversation
    const conversationMessages = {
      1: [
        {
          id: 1,
          content: 'Salut ! Comment ça va ?',
          timestamp: new Date(Date.now() - 60 * 60 * 1000),
          isOwn: false
        },
        {
          id: 2,
          content: 'Ça va bien merci ! Et toi ?',
          timestamp: new Date(Date.now() - 55 * 60 * 1000),
          isOwn: true
        },
        {
          id: 3,
          content: 'As-tu lu le dernier livre de science-fiction dont je t\'ai parlé ?',
          timestamp: new Date(Date.now() - 10 * 60 * 1000),
          isOwn: false
        },
        {
          id: 4,
          content: 'Pas encore, mais il est dans ma liste !',
          timestamp: new Date(Date.now() - 5 * 60 * 1000),
          isOwn: true
        }
      ],
      2: [
        {
          id: 1,
          content: 'Merci pour la recommandation de livre !',
          timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000),
          isOwn: false
        }
      ],
      3: [
        {
          id: 1,
          content: 'J\'ai adoré ce livre, on peut en discuter ?',
          timestamp: new Date(Date.now() - 24 * 60 * 60 * 1000),
          isOwn: false
        }
      ]
    }

    const filteredConversations = computed(() => {
      if (!searchQuery.value) return conversations.value
      return conversations.value.filter(conv => 
        conv.user.username.toLowerCase().includes(searchQuery.value.toLowerCase())
      )
    })

    const getInitials = (name) => {
      return name.split(' ').map(n => n[0]).join('').toUpperCase()
    }

    const formatTime = (timestamp) => {
      const now = new Date()
      const diff = now - timestamp
      const minutes = Math.floor(diff / (1000 * 60))
      const hours = Math.floor(diff / (1000 * 60 * 60))
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))

      if (minutes < 1) return 'À l\'instant'
      if (minutes < 60) return `${minutes}min`
      if (hours < 24) return `${hours}h`
      if (days < 7) return `${days}j`
      return timestamp.toLocaleDateString('fr-FR')
    }

    const selectConversation = (conversation) => {
      selectedConversation.value = conversation
      messages.value = conversationMessages[conversation.id] || []
      
      // Marquer comme lu
      conversation.unreadCount = 0
      
      // Faire défiler vers le bas
      nextTick(() => {
        scrollToBottom()
      })
    }

    const scrollToBottom = () => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    const sendMessage = () => {
      if (!newMessage.value.trim() || !selectedConversation.value) return

      const message = {
        id: Date.now(),
        content: newMessage.value.trim(),
        timestamp: new Date(),
        isOwn: true
      }

      messages.value.push(message)
      
      // Mettre à jour le dernier message de la conversation
      selectedConversation.value.lastMessage = {
        content: message.content,
        timestamp: message.timestamp
      }

      newMessage.value = ''
      
      nextTick(() => {
        scrollToBottom()
      })

      // Simuler une réponse automatique après 2 secondes
      setTimeout(() => {
        if (selectedConversation.value) {
          isTyping.value = true
          setTimeout(() => {
            isTyping.value = false
            const responses = [
              'Merci pour ton message !',
              'C\'est intéressant !',
              'Je vais y réfléchir.',
              'Bonne idée !',
              'On en reparle bientôt ?'
            ]
            const response = {
              id: Date.now(),
              content: responses[Math.floor(Math.random() * responses.length)],
              timestamp: new Date(),
              isOwn: false
            }
            messages.value.push(response)
            selectedConversation.value.lastMessage = {
              content: response.content,
              timestamp: response.timestamp
            }
            nextTick(() => {
              scrollToBottom()
            })
          }, 2000)
        }
      }, 1000)
    }

    const handleTyping = () => {
      // Ici on pourrait envoyer un signal de frappe au serveur
    }

    const goBack = () => {
      router.go(-1)
    }

    // Initialiser la conversation si un ID est fourni dans l'URL
    onMounted(() => {
      const userId = route.params.id
      if (userId) {
        // Trouver ou créer une conversation avec cet utilisateur
        let conversation = conversations.value.find(conv => conv.user.id === parseInt(userId))
        
        if (!conversation) {
          // Créer une nouvelle conversation
          conversation = {
            id: Date.now(),
            user: {
              id: parseInt(userId),
              username: `Utilisateur ${userId}`,
              isOnline: Math.random() > 0.5,
              lastSeen: new Date(Date.now() - Math.random() * 60 * 60 * 1000)
            },
            lastMessage: {
              content: 'Nouvelle conversation',
              timestamp: new Date()
            },
            unreadCount: 0
          }
          conversations.value.unshift(conversation)
        }
        
        selectConversation(conversation)
      }
    })

    // Faire défiler vers le bas quand de nouveaux messages arrivent
    watch(messages, () => {
      nextTick(() => {
        scrollToBottom()
      })
    }, { deep: true })

    return {
      searchQuery,
      selectedConversation,
      newMessage,
      isTyping,
      messagesContainer,
      conversations,
      messages,
      filteredConversations,
      authStore,
      getInitials,
      formatTime,
      selectConversation,
      sendMessage,
      handleTyping,
      goBack
    }
  }
}
</script>

<style scoped>
/* Styles pour l'animation de frappe */
@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.animate-bounce {
  animation: bounce 1.4s infinite ease-in-out both;
}

/* Styles pour la zone de texte auto-redimensionnable */
textarea {
  field-sizing: content;
}
</style>
