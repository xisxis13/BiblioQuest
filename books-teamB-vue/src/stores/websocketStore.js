import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import websocketService from '@/services/websocketService'
import { useAuthStore } from './authStore'

export const useWebSocketStore = defineStore('websocket', () => {
  // État
  const connected = ref(false)
  const messages = ref([])
  const privateMessages = ref([])
  const connectedUsers = ref([])
  const currentRoom = ref('public')
  const connectionError = ref(null)
  const reconnecting = ref(false)

  // Getters
  const isConnected = computed(() => connected.value)
  const getMessages = computed(() => messages.value)
  const getPrivateMessages = computed(() => privateMessages.value)
  const getConnectedUsers = computed(() => connectedUsers.value)
  const hasError = computed(() => connectionError.value !== null)

  // Actions
  const connect = async () => {
    const authStore = useAuthStore()
    
    if (!authStore.isAuthenticated || !authStore.getUser?.username) {
      throw new Error('Utilisateur non authentifié')
    }

    try {
      connectionError.value = null
      reconnecting.value = true
      
      // Se connecter au WebSocket
      await websocketService.connect(authStore.getUser.username)
      
      // Enregistrer les callbacks pour les messages
      websocketService.onMessage((data, type) => {
        handleMessage(data, type)
      })
      
      connected.value = true
      reconnecting.value = false
      
      console.log('WebSocket connecté avec succès')
      
    } catch (error) {
      console.error('Erreur de connexion WebSocket:', error)
      connectionError.value = error.message
      reconnecting.value = false
      connected.value = false
      throw error
    }
  }

  const disconnect = () => {
    websocketService.disconnect()
    connected.value = false
    messages.value = []
    privateMessages.value = []
    connectedUsers.value = []
    connectionError.value = null
    reconnecting.value = false
  }

  const sendMessage = (content) => {
    if (!connected.value) {
      throw new Error('WebSocket non connecté')
    }
    
    if (!content || content.trim() === '') {
      throw new Error('Le message ne peut pas être vide')
    }

    websocketService.sendMessage(content.trim())
  }

  const sendPrivateMessage = async (receiver, content) => {
    if (!receiver || !content || content.trim() === '') {
      throw new Error('Destinataire et message requis')
    }

    // Essayer de se connecter si pas connecté
    if (!connected.value) {
      console.log('WebSocket non connecté, tentative de connexion...')
      await connect()
    }

    websocketService.sendPrivateMessage(receiver, content.trim())
  }

  const handleMessage = (data, type) => {
    console.log('Message reçu:', { data, type })
    
    switch (type) {
      case 'public':
        handlePublicMessage(data)
        break
      case 'private':
        handlePrivateMessage(data)
        break
      case 'users':
        handleUserListUpdate(data)
        break
      default:
        console.warn('Type de message inconnu:', type)
    }
  }

  const handlePublicMessage = (message) => {
    // Ajouter le message à la liste
    messages.value.push({
      id: Date.now() + Math.random(),
      sender: message.senderName || message.sender,
      content: message.content,
      type: message.type,
      timestamp: message.timestamp || new Date().toISOString(),
      isOwn: isOwnMessage(message.senderName || message.sender)
    })

    // Limiter le nombre de messages en mémoire
    if (messages.value.length > 100) {
      messages.value = messages.value.slice(-100)
    }
  }

  const handlePrivateMessage = (message) => {
    privateMessages.value.push({
      id: Date.now() + Math.random(),
      sender: message.senderName || message.sender,
      receiver: message.receiver,
      content: message.content,
      timestamp: message.timestamp || new Date().toISOString(),
      isOwn: isOwnMessage(message.senderName || message.sender)
    })

    // Limiter le nombre de messages privés en mémoire
    if (privateMessages.value.length > 50) {
      privateMessages.value = privateMessages.value.slice(-50)
    }
  }

  const handleUserListUpdate = (userList) => {
    connectedUsers.value = Array.isArray(userList) ? userList : []
  }

  const isOwnMessage = (sender) => {
    const authStore = useAuthStore()
    return sender === authStore.getUser?.username
  }

  const clearMessages = () => {
    messages.value = []
  }

  const clearPrivateMessages = () => {
    privateMessages.value = []
  }

  // Charger les messages historiques depuis l'API
  const loadHistoricalMessages = async (limit = 50) => {
    try {
      const response = await fetch(`http://localhost:8080/api/chat/messages/public?limit=${limit}`)
      
      if (!response.ok) {
        if (response.status === 401) {
          console.error('❌ Erreur 401: Authentification requise pour charger les messages')
          connectionError.value = 'Authentification requise. Veuillez vous reconnecter.'
          return
        }
        throw new Error(`Erreur HTTP ${response.status}: ${response.statusText}`)
      }
      
      const historicalMessages = await response.json()
      
      // Convertir et ajouter les messages historiques
      const convertedMessages = historicalMessages.map(msg => ({
        id: msg.id || `hist_${Date.now()}_${Math.random()}`,
        sender: msg.senderName,
        content: msg.content,
        type: msg.type,
        timestamp: msg.timestamp,
        isOwn: isOwnMessage(msg.senderName)
      }))
      
      // Remplacer les messages actuels par l'historique
      messages.value = convertedMessages
      console.log(`✅ ${historicalMessages.length} messages historiques chargés`)
      
    } catch (error) {
      console.error('❌ Erreur lors du chargement des messages historiques:', error.message)
      connectionError.value = `Impossible de charger l'historique: ${error.message}`
    }
  }

  const clearError = () => {
    connectionError.value = null
  }

  // Reconnexion automatique
  const reconnect = async () => {
    if (reconnecting.value) return
    
    try {
      await connect()
    } catch (error) {
      console.error('Échec de la reconnexion:', error)
    }
  }

  return {
    // État
    connected,
    messages,
    privateMessages,
    connectedUsers,
    currentRoom,
    connectionError,
    reconnecting,
    
    // Getters
    isConnected,
    getMessages,
    getPrivateMessages,
    getConnectedUsers,
    hasError,
    
    // Actions
    connect,
    disconnect,
    sendMessage,
    sendPrivateMessage,
    clearMessages,
    clearPrivateMessages,
    clearError,
    reconnect,
    loadHistoricalMessages
  }
})
