<template>
  <div class="chat-window bg-white rounded-2xl shadow-lg border border-gray-200 overflow-hidden">
    <!-- Header du chat -->
    <div class="chat-header bg-gradient-to-r from-[#8B4513] to-[#A0522D] text-white p-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-3">
          <div class="w-3 h-3 bg-green-400 rounded-full animate-pulse"></div>
          <div>
            <h3 class="font-semibold text-lg">💬 Chat Global</h3>
            <p class="text-sm text-orange-100">
              {{ connectedUsersCount }} utilisateur{{ connectedUsersCount > 1 ? 's' : '' }} en ligne
            </p>
          </div>
        </div>
        
        <div class="flex items-center space-x-2">
          <button 
            v-if="!isConnected" 
            @click="connectToChat"
            class="bg-white/20 hover:bg-white/30 px-3 py-1 rounded-lg text-sm font-medium transition-colors"
            :disabled="connecting"
          >
            <span v-if="connecting" class="flex items-center">
              <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Connexion...
            </span>
            <span v-else>Se connecter</span>
          </button>
          
          <button 
            v-if="isConnected" 
            @click="disconnectFromChat"
            class="bg-red-500/20 hover:bg-red-500/30 px-3 py-1 rounded-lg text-sm font-medium transition-colors"
          >
            Se déconnecter
          </button>
          
          <button 
            v-if="showMinimize"
            @click="$emit('minimize')"
            class="bg-white/20 hover:bg-white/30 p-1 rounded-lg transition-colors"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Zone des messages -->
    <div class="chat-messages flex-1 h-96 overflow-y-auto p-4 bg-gray-50" ref="messagesContainer">
      <div v-if="messages.length > 0">
        <ChatMessage 
          v-for="message in messages" 
          :key="message.id" 
          :message="message"
        />
      </div>
      <div v-else-if="!isConnected" class="flex items-center justify-center h-full">
        <div class="text-center text-gray-500">
          <svg class="w-16 h-16 mx-auto mb-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
          </svg>
          <p class="text-lg font-medium mb-2">Connectez-vous au chat</p>
          <p class="text-sm">Rejoignez la conversation avec la communauté !</p>
        </div>
      </div>
      <div v-else class="flex items-center justify-center h-full">
        <div class="text-center text-gray-500">
          <svg class="w-16 h-16 mx-auto mb-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a2 2 0 01-2-2v-6a2 2 0 012-2h8z" />
          </svg>
          <p class="text-lg font-medium mb-2">Aucun message</p>
          <p class="text-sm">Soyez le premier à envoyer un message !</p>
        </div>
      </div>
    </div>

    <!-- Zone de saisie -->
    <div v-if="isConnected" class="chat-input border-t border-gray-200 p-4 bg-white">
      <form @submit.prevent="sendMessage" class="flex space-x-3">
        <div class="flex-1 relative">
          <input
            v-model="newMessage"
            type="text"
            placeholder="Tapez votre message..."
            class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
            :disabled="!isConnected"
            maxlength="500"
          />
          <div class="absolute right-3 top-1/2 transform -translate-y-1/2 text-xs text-gray-400">
            {{ newMessage.length }}/500
          </div>
        </div>
        <button
          type="submit"
          :disabled="!canSendMessage"
          class="bg-blue-500 hover:bg-blue-600 disabled:bg-gray-300 disabled:cursor-not-allowed text-white px-6 py-3 rounded-xl font-medium transition-colors flex items-center space-x-2"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
          </svg>
          <span>Envoyer</span>
        </button>
      </form>
    </div>

    <!-- Utilisateurs en ligne (sidebar optionnelle) -->
    <div v-if="showUsers && isConnected" class="chat-users border-t border-gray-200 p-4 bg-gray-50">
      <h4 class="font-semibold text-gray-700 mb-3 flex items-center">
        <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
        En ligne ({{ connectedUsersCount }})
      </h4>
      <div class="space-y-2 max-h-32 overflow-y-auto">
        <div v-for="user in connectedUsers" :key="user.username" class="flex items-center space-x-2">
          <div class="w-6 h-6 bg-gradient-to-br from-green-400 to-blue-500 rounded-full flex items-center justify-center text-white text-xs font-semibold">
            {{ getUserInitials(user.username) }}
          </div>
          <span class="text-sm text-gray-700">{{ user.username }}</span>
          <div class="w-2 h-2 bg-green-400 rounded-full"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, nextTick, onMounted, onUnmounted, watchEffect } from 'vue'
import { useWebSocketStore } from '@/stores/websocketStore'
import ChatMessage from './ChatMessage.vue'

export default {
  name: 'ChatWindow',
  components: {
    ChatMessage
  },
  props: {
    showUsers: {
      type: Boolean,
      default: true
    },
    showMinimize: {
      type: Boolean,
      default: false
    },
    autoConnect: {
      type: Boolean,
      default: false
    }
  },
  emits: ['minimize'],
  setup(props) {
    const websocketStore = useWebSocketStore()
    const newMessage = ref('')
    const connecting = ref(false)
    const messagesContainer = ref(null)

    // Computed properties
    const isConnected = computed(() => websocketStore.isConnected)
    const messages = computed(() => websocketStore.getMessages)
    const connectedUsers = computed(() => websocketStore.getConnectedUsers)
    const connectedUsersCount = computed(() => connectedUsers.value.length)
    
    const canSendMessage = computed(() => {
      return isConnected.value && newMessage.value.trim().length > 0
    })

    // Methods
    const connectToChat = async () => {
      try {
        connecting.value = true
        
        // Charger d'abord les messages historiques
        await websocketStore.loadHistoricalMessages()
        
        // Puis se connecter au WebSocket
        await websocketStore.connect()
      } catch (error) {
        console.error('Erreur de connexion:', error)
      } finally {
        connecting.value = false
      }
    }

    const disconnectFromChat = async () => {
      await websocketStore.disconnect()
    }

    const sendMessage = async () => {
      if (!canSendMessage.value) return
      
      try {
        await websocketStore.sendMessage(newMessage.value.trim())
        newMessage.value = ''
        scrollToBottom()
      } catch (error) {
        console.error('Erreur envoi message:', error)
      }
    }

    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    }

    const getUserInitials = (username) => {
      if (!username) return '?'
      return username.substring(0, 2).toUpperCase()
    }

    // Auto-scroll when new messages arrive
    const stopWatching = watchEffect(() => {
      // Watch for changes in messages length
      messages.value.length
      scrollToBottom()
    })

    // Auto-connect if requested
    onMounted(async () => {
      try {
        if (messages.value.length === 0) {
          await websocketStore.loadHistoricalMessages()
        }
        if (props.autoConnect && !isConnected.value) {
          await connectToChat()
        }
      } catch (e) {
        console.error('Erreur lors du chargement initial du chat:', e)
      }
    })

    onUnmounted(() => {
      stopWatching()
    })

    return {
      // Refs
      newMessage,
      connecting,
      messagesContainer,
      
      // Computed
      isConnected,
      messages,
      connectedUsers,
      connectedUsersCount,
      canSendMessage,
      
      // Methods
      connectToChat,
      disconnectFromChat,
      sendMessage,
      getUserInitials
    }
  }
}
</script>

<style scoped>
.chat-window {
  min-height: 500px;
  max-height: 600px;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  scrollbar-width: thin;
  scrollbar-color: #cbd5e0 #f7fafc;
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f7fafc;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

/* Animation pour les nouveaux messages */
.chat-messages > div {
  animation: slideInUp 0.3s ease-out;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
