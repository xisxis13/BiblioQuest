<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <Header />
    <Sidebar />
    <div class="ml-44 pt-20 px-6 py-8">
      <div class="text-center py-12 mb-8">
        <h1 class="text-4xl font-bold text-[#8B4513] mb-4">
          Bienvenue <span class="text-orange-500">{{ authStore.getUser?.username || 'user' }}</span> !
        </h1>
        <p class="text-gray-600">Vous êtes maintenant connecté</p>
      </div>

      <!-- Quick Actions Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 max-w-6xl mx-auto">
        <router-link
          to="/my-library"
          class="bg-white border border-gray-200 rounded-2xl p-6 hover:shadow-lg transition-all group"
        >
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center group-hover:bg-orange-200 transition-colors">
              <svg class="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900 mb-1">Ma Bibliothèque</h3>
              <p class="text-sm text-gray-600">Gérez vos livres et progression</p>
            </div>
          </div>
        </router-link>

        <router-link
          to="/reading-tracker"
          class="bg-white border border-gray-200 rounded-2xl p-6 hover:shadow-lg transition-all group"
        >
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center group-hover:bg-orange-200 transition-colors">
              <svg class="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900 mb-1">Suivi de Lecture</h3>
              <p class="text-sm text-gray-600">Enregistrez vos sessions</p>
            </div>
          </div>
        </router-link>

        <router-link
          to="/catalogue"
          class="bg-white border border-gray-200 rounded-2xl p-6 hover:shadow-lg transition-all group"
        >
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center group-hover:bg-orange-200 transition-colors">
              <svg class="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900 mb-1">Catalogue</h3>
              <p class="text-sm text-gray-600">Explorez tous les livres</p>
            </div>
          </div>
        </router-link>

        <router-link
          to="/library"
          class="bg-white border border-gray-200 rounded-2xl p-6 hover:shadow-lg transition-all group"
        >
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center group-hover:bg-orange-200 transition-colors">
              <svg class="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900 mb-1">Bibliothèque</h3>
              <p class="text-sm text-gray-600">Tous les livres disponibles</p>
            </div>
          </div>
        </router-link>

        <router-link
          to="/marketplace"
          class="bg-white border border-gray-200 rounded-2xl p-6 hover:shadow-lg transition-all group"
        >
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center group-hover:bg-orange-200 transition-colors">
              <svg class="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900 mb-1">Marketplace</h3>
              <p class="text-sm text-gray-600">Achetez et vendez</p>
            </div>
          </div>
        </router-link>

        <router-link
          to="/profile"
          class="bg-white border border-gray-200 rounded-2xl p-6 hover:shadow-lg transition-all group"
        >
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center group-hover:bg-orange-200 transition-colors">
              <svg class="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900 mb-1">Mon Profil</h3>
              <p class="text-sm text-gray-600">Paramètres du compte</p>
            </div>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'
import Sidebar from '@/components/Sidebar.vue'
import Header from '@/components/Header.vue'

export default {
  name: 'HomePage',
  components: {
    Sidebar,
    Header
  },
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()

    return {
      authStore,
      router
    }
  }
}
</script>
