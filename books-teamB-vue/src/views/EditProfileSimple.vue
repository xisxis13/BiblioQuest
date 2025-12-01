<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <HeaderComponent />
    <Sidebar />
    <div class="ml-44 pt-20 px-6 py-8">
      <div class="max-w-4xl mx-auto">
        <!-- En-tête -->
        <div class="mb-8">
          <h1 class="text-3xl font-bold text-gray-900">Modifier mon profil</h1>
          <p class="text-gray-600 mt-2">Mettez à jour vos informations personnelles et préférences</p>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <!-- Section principale -->
          <div class="lg:col-span-2 space-y-8">
            
            <!-- Informations personnelles -->
            <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
              <div class="px-6 py-4 border-b border-gray-200">
                <h2 class="text-xl font-semibold text-gray-900">Informations personnelles</h2>
              </div>
              <div class="p-6 space-y-6">
                <!-- Nom d'utilisateur -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Nom d'utilisateur</label>
                  <input 
                    v-model="profileForm.username"
                    type="text" 
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-colors"
                    placeholder="Votre nom d'utilisateur"
                  />
                </div>

                <!-- Email -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Adresse email</label>
                  <input 
                    v-model="profileForm.email"
                    type="email" 
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-colors"
                    placeholder="votre@email.com"
                  />
                </div>

                <!-- Boutons d'action -->
                <div class="flex gap-3 pt-4">
                  <button 
                    @click="updateProfile"
                    :disabled="loading"
                    class="px-6 py-3 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors font-medium disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <span v-if="loading">Mise à jour...</span>
                    <span v-else>Sauvegarder</span>
                  </button>
                  <button 
                    @click="resetForm"
                    class="px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors font-medium"
                  >
                    Annuler
                  </button>
                </div>
              </div>
            </div>

            <!-- Changer le mot de passe -->
            <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
              <div class="px-6 py-4 border-b border-gray-200">
                <h2 class="text-xl font-semibold text-gray-900">Changer le mot de passe</h2>
              </div>
              <div class="p-6 space-y-6">
                <!-- Mot de passe actuel -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Mot de passe actuel</label>
                  <input 
                    v-model="passwordForm.currentPassword"
                    type="password" 
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-colors"
                    placeholder="Votre mot de passe actuel"
                  />
                </div>

                <!-- Nouveau mot de passe -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Nouveau mot de passe</label>
                  <input 
                    v-model="passwordForm.newPassword"
                    type="password" 
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-colors"
                    placeholder="Nouveau mot de passe (min. 6 caractères)"
                  />
                </div>

                <!-- Confirmer le mot de passe -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Confirmer le nouveau mot de passe</label>
                  <input 
                    v-model="passwordForm.confirmPassword"
                    type="password" 
                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-colors"
                    :class="{ 'border-red-300': passwordForm.confirmPassword && !passwordsMatch }"
                    placeholder="Confirmer le nouveau mot de passe"
                  />
                  <p v-if="passwordForm.confirmPassword && !passwordsMatch" class="text-red-600 text-sm mt-1">
                    Les mots de passe ne correspondent pas
                  </p>
                </div>

                <!-- Bouton changer mot de passe -->
                <div class="pt-4">
                  <button 
                    @click="changePassword"
                    :disabled="!passwordsMatch || !passwordForm.currentPassword || !passwordForm.newPassword || loading"
                    class="px-6 py-3 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors font-medium disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <span v-if="loading">Changement...</span>
                    <span v-else>Changer le mot de passe</span>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Sidebar droite -->
          <div class="space-y-8">
            
            <!-- Photo de profil -->
            <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
              <div class="px-6 py-4 border-b border-gray-200">
                <h2 class="text-xl font-semibold text-gray-900">Photo de profil</h2>
              </div>
              <div class="p-6 text-center">
                <!-- Avatar -->
                <div class="relative inline-block mb-6">
                  <div class="w-32 h-32 rounded-full overflow-hidden bg-gray-200 border-4 border-white shadow-lg mx-auto">
                    <img 
                      v-if="profileData.avatarData" 
                      :src="profileData.avatarData" 
                      :alt="profileData.username"
                      class="w-full h-full object-cover"
                    />
                    <div v-else class="w-full h-full flex items-center justify-center bg-orange-500">
                      <span class="text-white text-4xl font-bold">{{ getUserInitials() }}</span>
                    </div>
                  </div>
                  <!-- Badge de statut -->
                  <div class="absolute bottom-2 right-2 w-6 h-6 bg-green-500 border-2 border-white rounded-full"></div>
                </div>

                <!-- Boutons avatar -->
                <div class="space-y-3">
                  <input 
                    ref="avatarInput"
                    type="file" 
                    accept="image/*" 
                    @change="handleAvatarUpload"
                    class="hidden"
                  />
                  <button 
                    @click="$refs.avatarInput.click()"
                    :disabled="uploadingAvatar"
                    class="w-full px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors font-medium disabled:opacity-50"
                  >
                    {{ profileData.avatarData ? 'Changer la photo' : 'Ajouter une photo' }}
                  </button>
                  <button 
                    v-if="profileData.avatarData"
                    @click="removeAvatar"
                    :disabled="uploadingAvatar"
                    class="w-full px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50"
                  >
                    Supprimer
                  </button>
                </div>

                <p class="text-xs text-gray-500 mt-4">
                  JPG, PNG ou GIF. Taille max : 5MB
                </p>
              </div>
            </div>

            <!-- Statistiques du profil -->
            <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
              <div class="px-6 py-4 border-b border-gray-200">
                <h2 class="text-xl font-semibold text-gray-900">Statistiques</h2>
              </div>
              <div class="p-6 space-y-4">
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">Membre depuis</span>
                  <span class="font-medium">{{ formatDate(profileData.createdAt) }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">Livres lus</span>
                  <span class="font-medium">{{ profileData.booksRead || 0 }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">Rôle</span>
                  <span class="px-2 py-1 bg-orange-100 text-orange-800 text-sm rounded-full font-medium">
                    {{ getRoleDisplay() }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Message de feedback -->
        <div v-if="message" :class="messageClass" class="mt-8 p-4 rounded-lg">
          {{ message }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import Sidebar from '@/components/Sidebar.vue'
import HeaderComponent from '@/components/HeaderComponent.vue'

export default {
  name: 'EditProfileSimple',
  components: {
    Sidebar,
    HeaderComponent
  },
  setup() {
    const authStore = useAuthStore()
    
    // État des données
    const profileData = ref({
      id: null,
      username: '',
      email: '',
      avatarData: null,
      createdAt: null,
      booksRead: 0
    })
    
    const profileForm = ref({
      username: '',
      email: ''
    })
    
    const passwordForm = ref({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    
    const loading = ref(false)
    const uploadingAvatar = ref(false)
    const message = ref('')
    const messageType = ref('success')
    
    // Computed
    const messageClass = computed(() => {
      return messageType.value === 'success' 
        ? 'bg-green-50 text-green-800 border border-green-200'
        : 'bg-red-50 text-red-800 border border-red-200'
    })
    
    const passwordsMatch = computed(() => {
      return passwordForm.value.newPassword === passwordForm.value.confirmPassword
    })
    
    // Méthodes
    const getUserInitials = () => {
      const user = authStore.getUser
      if (user?.username) {
        return user.username.substring(0, 2).toUpperCase()
      }
      return 'U'
    }
    
    const getRoleDisplay = () => {
      const role = authStore.getUser?.role
      switch (role) {
        case 'USER':
          return 'Utilisateur'
        case 'ADMIN':
          return 'Administrateur'
        case 'AUTEUR':
          return 'Auteur'
        case 'BIBLIOTHECAIRE':
          return 'Bibliothécaire'
        default:
          return 'Utilisateur'
      }
    }
    
    const formatDate = (date) => {
      if (!date) return 'Non disponible'
      return new Date(date).toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: 'long'
      })
    }
    
    const showMessage = (text, type = 'success') => {
      message.value = text
      messageType.value = type
      setTimeout(() => {
        message.value = ''
      }, 5000)
    }
    
    const loadProfile = () => {
      // Charger les données depuis le store d'authentification
      const user = authStore.getUser
      if (user) {
        profileData.value = {
          id: user.id,
          username: user.username,
          email: user.email,
          avatarData: user.avatarUrl,
          createdAt: new Date(), // Simulé pour l'instant
          booksRead: 0 // Simulé pour l'instant
        }
        
        profileForm.value = {
          username: user.username,
          email: user.email
        }
      }
    }
    
    const updateProfile = async () => {
      loading.value = true
      try {
        // Simuler la mise à jour (à remplacer par un vrai appel API)
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // Mettre à jour les données locales
        profileData.value.username = profileForm.value.username
        profileData.value.email = profileForm.value.email
        
        showMessage('Profil mis à jour avec succès !', 'success')
      } catch (error) {
        showMessage('Erreur lors de la mise à jour du profil', 'error')
      } finally {
        loading.value = false
      }
    }
    
    const changePassword = async () => {
      if (!passwordsMatch.value) {
        showMessage('Les mots de passe ne correspondent pas', 'error')
        return
      }
      
      loading.value = true
      try {
        // Simuler le changement de mot de passe
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // Réinitialiser le formulaire
        passwordForm.value = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        }
        
        showMessage('Mot de passe changé avec succès !', 'success')
      } catch (error) {
        showMessage('Erreur lors du changement de mot de passe', 'error')
      } finally {
        loading.value = false
      }
    }
    
    const handleAvatarUpload = async (event) => {
      const file = event.target.files[0]
      if (!file) return
      
      console.log('=== AVATAR UPLOAD START ===')
      console.log('File selected:', file.name, 'Size:', file.size, 'Type:', file.type)
      
      // Validation du fichier
      if (file.size > 5 * 1024 * 1024) { // 5MB max
        console.log('File too large:', file.size)
        showMessage('L\'image ne doit pas dépasser 5MB', 'error')
        return
      }
      
      if (!file.type.startsWith('image/')) {
        console.log('Invalid file type:', file.type)
        showMessage('Veuillez sélectionner une image valide', 'error')
        return
      }
      
      uploadingAvatar.value = true
      console.log('Starting multipart upload process...')
      
      try {
        // Créer FormData directement avec le fichier original
        const formData = new FormData()
        formData.append('avatar', file)
        
        const userId = authStore.getUser?.id
        console.log('User ID:', userId)
        
        if (!userId) {
          throw new Error('User ID not found')
        }
        
        const url = `http://localhost:8080/api/profile/${userId}/avatar`
        console.log('API URL:', url)
        
        console.log('Sending multipart request without auth (temporarily disabled)')
        
        const response = await fetch(url, {
          method: 'POST',
          body: formData
        })
        
        console.log('API Response status:', response.status)
        console.log('API Response ok:', response.ok)
        
        if (response.ok) {
          const responseData = await response.json()
          console.log('API Response data:', responseData)
          
          // Créer une URL d'objet pour prévisualiser l'image
          const imageUrl = URL.createObjectURL(file)
          profileData.value.avatarData = imageUrl
          
          // Mettre à jour aussi dans le store avec l'URL de l'avatar du backend
          if (responseData.avatarUrl) {
            authStore.user.avatarUrl = responseData.avatarUrl
          }
          
          showMessage('Photo de profil mise à jour !', 'success')
        } else {
          const errorText = await response.text()
          console.log('API Error response:', errorText)
          throw new Error(`Erreur ${response.status}: ${errorText}`)
        }
      } catch (error) {
        console.error('Erreur upload avatar:', error)
        showMessage(`Erreur lors de la sauvegarde de l'avatar: ${error.message}`, 'error')
      } finally {
        uploadingAvatar.value = false
      }
    }
    
    const removeAvatar = () => {
      profileData.value.avatarData = null
      showMessage('Photo de profil supprimée', 'success')
    }
    
    const resetForm = () => {
      loadProfile()
      showMessage('Modifications annulées', 'success')
    }
    
    const resetPasswordForm = () => {
      passwordForm.value = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    }
    
    // Initialisation
    onMounted(() => {
      loadProfile()
    })
    
    return {
      authStore,
      profileData,
      profileForm,
      passwordForm,
      loading,
      uploadingAvatar,
      message,
      messageClass,
      passwordsMatch,
      getUserInitials,
      getRoleDisplay,
      formatDate,
      updateProfile,
      changePassword,
      handleAvatarUpload,
      removeAvatar,
      resetForm,
      resetPasswordForm
    }
  }
}
</script>
