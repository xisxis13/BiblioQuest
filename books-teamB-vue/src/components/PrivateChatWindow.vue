<template>
  <div class="bg-white rounded-2xl border border-gray-200 overflow-hidden h-96 flex flex-col">
    <!-- Header du chat privé -->
    <div class="bg-gradient-to-r from-[#8B4513] to-[#A0522D] text-white p-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-3">
          <div class="relative">
            <div class="w-10 h-10 bg-white/20 rounded-full flex items-center justify-center text-white font-semibold">
              {{ getUserInitials(conversation.otherUser.username) }}
            </div>
            <div v-if="conversation.otherUser.isOnline" class="absolute -bottom-1 -right-1 w-3 h-3 bg-green-400 border-2 border-white rounded-full"></div>
          </div>
          <div>
            <h3 class="font-semibold">{{ conversation.otherUser.username }}</h3>
            <p class="text-sm text-orange-100">
              {{ conversation.otherUser.isOnline ? 'En ligne' : 'Hors ligne' }}
            </p>
          </div>
        </div>
        
        <div class="flex items-center space-x-2">
          <button 
            @click="$emit('close')"
            class="bg-white/20 hover:bg-white/30 p-2 rounded-lg transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Messages -->
    <div ref="messagesContainer" class="flex-1 overflow-y-auto p-4 space-y-4">
      <div v-if="messages.length === 0" class="text-center text-gray-500 py-8">
        <p>Aucun message dans cette conversation</p>
        <p class="text-sm text-gray-400 mt-1">Envoyez le premier message !</p>
      </div>

      <div 
        v-for="message in messages" 
        :key="message.id"
        class="flex"
        :class="{ 'justify-end': isOwnMessage(message) }"
      >
        <div 
          class="max-w-xs lg:max-w-md px-4 py-2 rounded-2xl"
          :class="isOwnMessage(message) 
            ? 'bg-orange-500 text-white rounded-br-md' 
            : 'bg-gray-100 text-gray-900 rounded-bl-md'"
        >
          <p class="text-sm">{{ message.content }}</p>
          <p 
            class="text-xs mt-1"
            :class="isOwnMessage(message) ? 'text-orange-100' : 'text-gray-500'"
          >
            {{ formatMessageTime(message.timestamp) }}
          </p>
        </div>
      </div>
    </div>

    <!-- Zone de saisie -->
    <div class="border-t border-gray-200 p-4">
      <form @submit.prevent="sendMessage" class="flex space-x-3">
        <input
          v-model="newMessage"
          type="text"
          placeholder="Tapez votre message..."
          class="flex-1 px-4 py-2 border border-gray-300 rounded-full focus:ring-2 focus:ring-orange-500 focus:border-orange-500"
          :disabled="sending"
        />
        <button
          type="submit"
          :disabled="!newMessage.trim() || sending"
          class="bg-orange-500 hover:bg-orange-600 disabled:bg-gray-300 text-white p-2 rounded-full transition-colors"
        >
          <svg v-if="sending" class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="m4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
          </svg>
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, computed, nextTick, onMounted, watch } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { useWebSocketStore } from '@/stores/websocketStore'

export default {
  name: 'PrivateChatWindow',
  props: {
    conversation: {
      type: Object,
      required: true
    }
  },
  emits: ['message-sent', 'close'],
  setup(props, { emit }) {
    const authStore = useAuthStore()
    const websocketStore = useWebSocketStore()
    const messages = ref([])
    const newMessage = ref('')
    const sending = ref(false)
    const messagesContainer = ref(null)

    // Charger les messages de la conversation depuis la DB
    const loadMessages = async () => {
      try {
        const currentUser = authStore.getUser
        if (!currentUser) return
        
        // Charger les messages privés depuis l'API
        const response = await fetch(
          `http://localhost:8080/api/chat/messages/private?user1=${currentUser.username}&user2=${props.conversation.otherUser.username}`
        )
        
        if (response.ok) {
          const privateMessages = await response.json()
          
          // Convertir les messages pour l'affichage
          messages.value = privateMessages.map(msg => ({
            id: msg.id || Date.now() + Math.random(),
            content: msg.content,
            senderId: msg.senderName === currentUser.username ? currentUser.id : props.conversation.otherUser.id,
            sender: msg.senderName,
            timestamp: new Date(msg.timestamp),
            isOwn: msg.senderName === currentUser.username
          }))
          
          console.log(`${privateMessages.length} messages privés chargés`)
        }
        
        // Faire défiler vers le bas
        await nextTick()
        scrollToBottom()
      } catch (error) {
        console.error('Erreur lors du chargement des messages:', error)
      }
    }

    // Envoyer un message
    const sendMessage = async () => {
      if (!newMessage.value.trim() || sending.value) return

      sending.value = true
      
      try {
        const messageContent = newMessage.value.trim()
        const currentUser = authStore.getUser
        
        if (!currentUser) {
          console.error('Utilisateur non connecté')
          return
        }

        // Envoyer le message privé via WebSocket
        websocketStore.sendPrivateMessage(
          props.conversation.otherUser.username,
          messageContent
        )
        
        // Ajouter le message localement pour affichage immédiat
        const message = {
          id: Date.now(),
          content: messageContent,
          senderId: currentUser.id,
          sender: currentUser.username,
          timestamp: new Date(),
          isOwn: true
        }
        
        messages.value.push(message)
        
        // Émettre l'événement pour mettre à jour la liste des conversations
        emit('message-sent', message)
        
        // Vider le champ de saisie
        newMessage.value = ''
        
        // Faire défiler vers le bas
        await nextTick()
        scrollToBottom()
        
        console.log('Message privé envoyé via WebSocket:', message)
        
      } catch (error) {
        console.error('Erreur lors de l\'envoi du message:', error)
      } finally {
        sending.value = false
      }
    }

    // Vérifier si le message appartient à l'utilisateur actuel
    const isOwnMessage = (message) => {
      return message.senderId === authStore.getUser?.id
    }

    // Faire défiler vers le bas
    const scrollToBottom = () => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    // Utilitaires
    const getUserInitials = (username) => {
      if (!username) return '?'
      return username.substring(0, 2).toUpperCase()
    }

    const formatMessageTime = (date) => {
      if (!date) return ''
      return date.toLocaleTimeString('fr-FR', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    }

    // Watcher pour recharger les messages quand la conversation change
    watch(() => props.conversation, () => {
      if (props.conversation) {
        loadMessages()
      }
    }, { immediate: true })

    return {
      messages,
      newMessage,
      sending,
      messagesContainer,
      sendMessage,
      isOwnMessage,
      getUserInitials,
      formatMessageTime
    }
  }
}
</script>
