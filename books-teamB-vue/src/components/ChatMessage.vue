<template>
  <div class="message-container" :class="messageClasses">
    <!-- Message de l'utilisateur connecté -->
    <div v-if="message.isOwn" class="flex justify-end mb-3">
      <div class="max-w-xs lg:max-w-md">
        <div class="bg-blue-500 text-white rounded-2xl rounded-br-md px-4 py-2 shadow-sm">
          <p class="text-sm">{{ message.content }}</p>
        </div>
        <div class="flex justify-end mt-1">
          <span class="text-xs text-gray-500">{{ formatTime(message.timestamp) }}</span>
        </div>
      </div>
    </div>

    <!-- Message d'un autre utilisateur -->
    <div v-else class="flex justify-start mb-3">
      <div class="flex items-start space-x-2 max-w-xs lg:max-w-md">
        <!-- Avatar -->
        <div class="flex-shrink-0">
          <div class="w-8 h-8 bg-gradient-to-br from-purple-400 to-blue-500 rounded-full flex items-center justify-center text-white text-sm font-semibold">
            {{ getUserInitials(message.sender) }}
          </div>
        </div>
        
        <!-- Contenu du message -->
        <div class="flex-1">
          <div class="bg-gray-100 rounded-2xl rounded-bl-md px-4 py-2 shadow-sm">
            <p class="text-xs font-medium text-gray-600 mb-1">{{ message.sender }}</p>
            <p class="text-sm text-gray-800">{{ message.content }}</p>
          </div>
          <div class="mt-1">
            <span class="text-xs text-gray-500">{{ formatTime(message.timestamp) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Messages système (JOIN, LEAVE) -->
    <div v-if="isSystemMessage" class="flex justify-center mb-3">
      <div class="bg-yellow-50 border border-yellow-200 rounded-full px-3 py-1">
        <p class="text-xs text-yellow-700 flex items-center">
          <svg class="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
          </svg>
          {{ getSystemMessage() }}
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatMessage',
  props: {
    message: {
      type: Object,
      required: true
    }
  },
  computed: {
    messageClasses() {
      return {
        'system-message': this.isSystemMessage,
        'own-message': this.message.isOwn,
        'other-message': !this.message.isOwn && !this.isSystemMessage
      }
    },
    isSystemMessage() {
      return ['JOIN', 'LEAVE'].includes(this.message.type)
    }
  },
  methods: {
    formatTime(timestamp) {
      if (!timestamp) return ''
      
      const date = new Date(timestamp)
      const now = new Date()
      const diffInHours = (now - date) / (1000 * 60 * 60)
      
      if (diffInHours < 24) {
        return date.toLocaleTimeString('fr-FR', { 
          hour: '2-digit', 
          minute: '2-digit' 
        })
      } else {
        return date.toLocaleDateString('fr-FR', { 
          day: '2-digit', 
          month: '2-digit',
          hour: '2-digit', 
          minute: '2-digit' 
        })
      }
    },
    getUserInitials(username) {
      if (!username) return '?'
      return username.substring(0, 2).toUpperCase()
    },
    getSystemMessage() {
      if (this.message.type === 'JOIN') {
        return `${this.message.sender} a rejoint le chat`
      } else if (this.message.type === 'LEAVE') {
        return `${this.message.sender} a quitté le chat`
      }
      return ''
    }
  }
}
</script>

<style scoped>
.message-container {
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Scrollbar personnalisée pour les messages */
.message-container::-webkit-scrollbar {
  width: 4px;
}

.message-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.message-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.message-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
