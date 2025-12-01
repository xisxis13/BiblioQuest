import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

// Polyfill pour les environnements qui n'ont pas global
if (typeof global === 'undefined') {
  window.global = window
}

class WebSocketService {
  constructor() {
    this.client = null
    this.connected = false
    this.username = null
    this.subscriptions = new Map()
    this.messageCallbacks = new Map()
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
  }

  connect(username) {
    if (this.connected) {
      console.log('WebSocket déjà connecté')
      return Promise.resolve()
    }

    this.username = username
    
    return new Promise((resolve, reject) => {
      try {
        // Créer une connexion SockJS
        const socket = new SockJS('http://localhost:8080/ws')
        
        // Créer le client STOMP
        this.client = new Client({
          webSocketFactory: () => socket,
          debug: (str) => {
            console.log('STOMP Debug:', str)
          },
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000,
        })

        // Gestionnaire de connexion
        this.client.onConnect = (frame) => {
          console.log('WebSocket connecté:', frame)
          this.connected = true
          this.reconnectAttempts = 0
          
          // S'abonner aux canaux publics
          this.subscribeToPublicMessages()
          this.subscribeToUserList()
          this.subscribeToPrivateMessages()
          
          // Annoncer l'arrivée de l'utilisateur
          this.addUser()
          
          resolve()
        }

        // Gestionnaire d'erreur
        this.client.onStompError = (frame) => {
          console.error('Erreur STOMP:', frame.headers['message'])
          console.error('Détails:', frame.body)
          this.connected = false
          reject(new Error(frame.headers['message']))
        }

        // Gestionnaire de déconnexion
        this.client.onDisconnect = () => {
          console.log('WebSocket déconnecté')
          this.connected = false
          this.attemptReconnect()
        }

        // Activer la connexion
        this.client.activate()
        
      } catch (error) {
        console.error('Erreur lors de la connexion WebSocket:', error)
        reject(error)
      }
    })
  }

  disconnect() {
    if (this.client && this.connected) {
      console.log('Déconnexion WebSocket...')
      this.client.deactivate()
      this.connected = false
      this.subscriptions.clear()
      this.messageCallbacks.clear()
    }
  }

  // S'abonner aux messages publics
  subscribeToPublicMessages() {
    const subscription = this.client.subscribe('/topic/public', (message) => {
      const chatMessage = JSON.parse(message.body)
      console.log('Message public reçu:', chatMessage)
      
      // Notifier les callbacks enregistrés
      this.messageCallbacks.forEach(callback => {
        if (typeof callback === 'function') {
          callback(chatMessage, 'public')
        }
      })
    })
    
    this.subscriptions.set('public', subscription)
  }

  // S'abonner à la liste des utilisateurs
  subscribeToUserList() {
    const subscription = this.client.subscribe('/topic/users', (message) => {
      const userList = JSON.parse(message.body)
      console.log('Liste utilisateurs mise à jour:', userList)
      
      // Notifier les callbacks enregistrés
      this.messageCallbacks.forEach(callback => {
        if (typeof callback === 'function') {
          callback(userList, 'users')
        }
      })
    })
    
    this.subscriptions.set('users', subscription)
  }

  // S'abonner aux messages privés
  subscribeToPrivateMessages() {
    const subscription = this.client.subscribe('/user/queue/private', (message) => {
      const chatMessage = JSON.parse(message.body)
      console.log('Message privé reçu:', chatMessage)
      
      // Notifier les callbacks enregistrés
      this.messageCallbacks.forEach(callback => {
        if (typeof callback === 'function') {
          callback(chatMessage, 'private')
        }
      })
    })
    
    this.subscriptions.set('private', subscription)
  }

  // Envoyer un message public
  sendMessage(content) {
    if (!this.connected || !this.client) {
      console.error('WebSocket non connecté')
      return
    }

    const chatMessage = {
      senderName: this.username,
      content: content,
      type: 'CHAT'
    }

    this.client.publish({
      destination: '/app/chat.sendMessage',
      body: JSON.stringify(chatMessage)
    })
  }

  // Envoyer un message privé
  sendPrivateMessage(receiver, content) {
    if (!this.connected || !this.client) {
      console.error('WebSocket non connecté')
      return
    }

    const chatMessage = {
      senderName: this.username,
      receiver: receiver,
      content: content,
      type: 'PRIVATE'
    }

    this.client.publish({
      destination: '/app/chat.sendPrivateMessage',
      body: JSON.stringify(chatMessage)
    })
  }

  // Annoncer l'arrivée de l'utilisateur
  addUser() {
    if (!this.connected || !this.client) {
      console.error('WebSocket non connecté')
      return
    }

    const chatMessage = {
      senderName: this.username,
      type: 'JOIN'
    }

    this.client.publish({
      destination: '/app/chat.addUser',
      body: JSON.stringify(chatMessage)
    })
  }

  // Enregistrer un callback pour les messages
  onMessage(callback) {
    const callbackId = Date.now() + Math.random()
    this.messageCallbacks.set(callbackId, callback)
    
    // Retourner une fonction pour désinscrire le callback
    return () => {
      this.messageCallbacks.delete(callbackId)
    }
  }

  // Tentative de reconnexion automatique
  attemptReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      console.log(`Tentative de reconnexion ${this.reconnectAttempts}/${this.maxReconnectAttempts}`)
      
      setTimeout(() => {
        if (this.username) {
          this.connect(this.username)
        }
      }, 5000 * this.reconnectAttempts) // Délai croissant
    } else {
      console.error('Nombre maximum de tentatives de reconnexion atteint')
    }
  }

  // Vérifier l'état de la connexion
  isConnected() {
    return this.connected
  }

  // Obtenir le nom d'utilisateur actuel
  getCurrentUsername() {
    return this.username
  }
}

// Exporter une instance singleton
export default new WebSocketService()
