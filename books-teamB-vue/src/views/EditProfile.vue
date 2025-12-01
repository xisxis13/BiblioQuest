<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <HeaderComponent />
    <Sidebar />
    <div class="ml-44 pt-20 px-6 py-8">
      <div class="max-w-2xl mx-auto">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
          <!-- En-tête -->
          <div class="px-6 py-4 border-b border-gray-200">
            <h1 class="text-2xl font-bold text-gray-900">Modifier mon profil</h1>
            <p class="text-gray-600 mt-1">Mettez à jour vos informations personnelles</p>
          </div>

          <div class="p-6">
            <!-- Section Avatar -->
            <div class="mb-8">
              <h2 class="text-lg font-semibold text-gray-900 mb-4">Photo de profil</h2>
              <div class="flex items-center gap-6">
                <div class="relative">
                  <div class="w-24 h-24 rounded-full overflow-hidden bg-gray-200 border-4 border-white shadow-lg">
                    <img 
                      v-if="profileData.avatarData" 
                      :src="profileData.avatarData" 
                      :alt="profileData.username"
                      class="w-full h-full object-cover"
                    />
                    <div v-else class="w-full h-full flex items-center justify-center bg-orange-500">
                      <span class="text-white text-2xl font-bold">{{ getUserInitials() }}</span>
                    </div>
                  </div>
                  <div v-if="uploadingAvatar" class="absolute inset-0 bg-black bg-opacity-50 rounded-full flex items-center justify-center">
                    <div class="w-6 h-6 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  </div>
                </div>
                
                <div class="flex flex-col gap-2">
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
                    class="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors font-medium disabled:opacity-50"
                  >
                    {{ profileData.avatarData ? 'Changer la photo' : 'Ajouter une photo' }}
                  </button>
                  <button 
                    v-if="profileData.avatarData"
                    @click="removeAvatar"
                    :disabled="uploadingAvatar"
                    class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50"
                  >
                    Supprimer
                  </button>
                  <p class="text-sm text-gray-500">JPG, PNG ou GIF. Max 5MB.</p>
                </div>
              </div>
            </div>

            <!-- Section Informations personnelles -->
            <div class="mb-8">
              <h2 class="text-lg font-semibold text-gray-900 mb-4">Informations personnelles</h2>
              <form @submit.prevent="updateProfile" class="space-y-4">
                <div>
                  <label for="username" class="block text-sm font-medium text-gray-700 mb-1">
                    Nom d'utilisateur
                  </label>
                  <input
                    id="username"
                    v-model="profileForm.username"
                    type="text"
                    required
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-orange-500"
                    placeholder="Votre nom d'utilisateur"
                  />
                </div>

                <div>
                  <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                    Adresse email
                  </label>
                  <input
                    id="email"
                    v-model="profileForm.email"
                    type="email"
                    required
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-orange-500"
                    placeholder="votre@email.com"
                  />
                </div>

                <div class="flex gap-3 pt-4">
                  <button
                    type="submit"
                    :disabled="updatingProfile"
                    class="px-6 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors font-medium disabled:opacity-50"
                  >
                    {{ updatingProfile ? 'Mise à jour...' : 'Mettre à jour' }}
                  </button>
                  <button
                    type="button"
                    @click="resetForm"
                    class="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    Annuler
                  </button>
                </div>
              </form>
            </div>

            <!-- Section Mot de passe -->
            <div class="mb-8">
              <h2 class="text-lg font-semibold text-gray-900 mb-4">Changer le mot de passe</h2>
              <form @submit.prevent="changePassword" class="space-y-4">
                <div>
                  <label for="currentPassword" class="block text-sm font-medium text-gray-700 mb-1">
                    Mot de passe actuel
                  </label>
                  <input
                    id="currentPassword"
                    v-model="passwordForm.currentPassword"
                    type="password"
                    required
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-orange-500"
                    placeholder="Votre mot de passe actuel"
                  />
                </div>

                <div>
                  <label for="newPassword" class="block text-sm font-medium text-gray-700 mb-1">
                    Nouveau mot de passe
                  </label>
                  <input
                    id="newPassword"
                    v-model="passwordForm.newPassword"
                    type="password"
                    required
                    minlength="6"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-orange-500"
                    placeholder="Au moins 6 caractères"
                  />
                </div>

                <div>
                  <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">
                    Confirmer le nouveau mot de passe
                  </label>
                  <input
                    id="confirmPassword"
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    required
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-orange-500"
                    placeholder="Confirmez votre nouveau mot de passe"
                  />
                </div>

                <div class="flex gap-3 pt-4">
                  <button
                    type="submit"
                    :disabled="changingPassword || !passwordsMatch"
                    class="px-6 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors font-medium disabled:opacity-50"
                  >
                    {{ changingPassword ? 'Changement...' : 'Changer le mot de passe' }}
                  </button>
                  <button
                    type="button"
                    @click="resetPasswordForm"
                    class="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    Annuler
                  </button>
                </div>

                <p v-if="!passwordsMatch && passwordForm.confirmPassword" class="text-sm text-red-600">
                  Les mots de passe ne correspondent pas
                </p>
              </form>
            </div>
          </div>
        </div>

        <!-- Messages d'erreur/succès -->
        <div v-if="message" class="mt-4 p-4 rounded-lg" :class="messageClass">
          {{ message }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import Sidebar from '@/components/Sidebar.vue'
import HeaderComponent from '@/components/HeaderComponent.vue'
import { profileService } from '@/services/profileService'

export default {
  name: 'EditProfilePage',
  components: {
    Sidebar,
    HeaderComponent
  },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const profileData = ref({
      id: null,
      username: '',
      email: '',
      avatarData: null
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
    
    const uploadingAvatar = ref(false)
    const updatingProfile = ref(false)
    const changingPassword = ref(false)
    const message = ref('')
    const messageType = ref('success')

    const passwordsMatch = computed(() => {
      return passwordForm.value.newPassword === passwordForm.value.confirmPassword
    })

    const messageClass = computed(() => {
      return messageType.value === 'success' 
        ? 'bg-green-50 text-green-800 border border-green-200'
        : 'bg-red-50 text-red-800 border border-red-200'
    })

    const getUserInitials = () => {
      if (profileData.value.username) {
        return profileData.value.username.substring(0, 2).toUpperCase()
      }
      return 'U'
    }


    const loadProfile = async () => {
      try {
        const userId = authStore.getUser?.id
        if (!userId) {
          showMessage('Utilisateur non connecté', 'error')
          return
        }
        
        const profile = await profileService.getCurrentProfile(userId)
        profileData.value = profile
        profileForm.value = {
          username: profile.username,
          email: profile.email
        }
      } catch (error) {
        showMessage('Erreur lors du chargement du profil', 'error')
      }
    }

    const updateProfile = async () => {
      updatingProfile.value = true
      try {
        const updatedProfile = await profileService.updateProfile(profileForm.value)
        profileData.value = updatedProfile
        
        // Mettre à jour le store d'authentification
        authStore.user = updatedProfile
        localStorage.setItem('auth_user', JSON.stringify(updatedProfile))
        
        showMessage('Profil mis à jour avec succès', 'success')
      } catch (error) {
        showMessage(error.response?.data?.message || 'Erreur lors de la mise à jour', 'error')
      } finally {
        updatingProfile.value = false
      }
    }

    const handleAvatarUpload = async (event) => {
      const file = event.target.files[0]
      if (!file) return

      uploadingAvatar.value = true
      try {
        const updatedProfile = await profileService.uploadAvatar(file)
        profileData.value = updatedProfile
        
        // Mettre à jour le store d'authentification
        authStore.user = updatedProfile
        localStorage.setItem('auth_user', JSON.stringify(updatedProfile))
        
        showMessage('Avatar mis à jour avec succès', 'success')
      } catch (error) {
        showMessage(error.response?.data?.message || 'Erreur lors de l\'upload', 'error')
      } finally {
        uploadingAvatar.value = false
        event.target.value = ''
      }
    }

    const removeAvatar = async () => {
      uploadingAvatar.value = true
      try {
        const updatedProfile = await profileService.removeAvatar()
        profileData.value = updatedProfile
        
        // Mettre à jour le store d'authentification
        authStore.user = updatedProfile
        localStorage.setItem('auth_user', JSON.stringify(updatedProfile))
        
        showMessage('Avatar supprimé avec succès', 'success')
      } catch (error) {
        showMessage('Erreur lors de la suppression', 'error')
      } finally {
        uploadingAvatar.value = false
      }
    }

    const changePassword = async () => {
      if (!passwordsMatch.value) return

      changingPassword.value = true
      try {
        await profileService.changePassword({
          currentPassword: passwordForm.value.currentPassword,
          newPassword: passwordForm.value.newPassword
        })
        
        resetPasswordForm()
        showMessage('Mot de passe changé avec succès', 'success')
      } catch (error) {
        showMessage(error.response?.data?.message || 'Erreur lors du changement', 'error')
      } finally {
        changingPassword.value = false
      }
    }

    const resetForm = () => {
      profileForm.value = {
        username: profileData.value.username,
        email: profileData.value.email
      }
    }

    const resetPasswordForm = () => {
      passwordForm.value = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    }

    const showMessage = (msg, type) => {
      message.value = msg
      messageType.value = type
      setTimeout(() => {
        message.value = ''
      }, 5000)
    }

    onMounted(() => {
      loadProfile()
    })

    return {
      profileData,
      profileForm,
      passwordForm,
      uploadingAvatar,
      updatingProfile,
      changingPassword,
      message,
      messageClass,
      passwordsMatch,
      getUserInitials,
      updateProfile,
      handleAvatarUpload,
      removeAvatar,
      changePassword,
      resetForm,
      resetPasswordForm
    }
  }
}
</script>
