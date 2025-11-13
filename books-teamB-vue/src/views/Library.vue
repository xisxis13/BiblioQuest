<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900">
    <!-- Navigation -->
    <nav class="bg-gradient-to-r from-indigo-600 via-purple-600 to-pink-600 shadow-xl sticky top-0 z-50 backdrop-blur-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <router-link to="/" class="flex items-center gap-3 hover:scale-105 transition-transform">
            <div class="w-10 h-10 bg-white/20 backdrop-blur-lg rounded-xl flex items-center justify-center shadow-lg border border-white/30">
              <svg class="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 715.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z"/>
              </svg>
            </div>
            <div>
              <h1 class="text-lg font-black text-white tracking-tight">BCO Library</h1>
              <p class="text-xs text-white/80">Votre bibliothèque digitale</p>
            </div>
          </router-link>
          
          <div class="hidden md:flex items-center gap-2">
            <router-link to="/" class="px-4 py-2 rounded-xl text-sm font-semibold text-white/90 hover:bg-white/20 backdrop-blur-sm transition-all">
              Accueil
            </router-link>
            <router-link to="/library" class="px-4 py-2 rounded-xl text-sm font-semibold bg-white/20 backdrop-blur-sm text-white shadow-lg border border-white/30">
              Ma Bibliothèque
            </router-link>
          </div>

          <div class="flex items-center gap-3">
            <div class="hidden sm:flex items-center gap-2 px-3 py-2 bg-white/20 backdrop-blur-lg rounded-xl border border-white/30 shadow-lg">
              <div class="w-8 h-8 bg-gradient-to-br from-yellow-400 to-orange-500 rounded-full flex items-center justify-center shadow-md">
                <span class="text-white text-sm font-bold">{{ authStore.getUser?.username?.charAt(0).toUpperCase() }}</span>
              </div>
              <span class="text-sm font-semibold text-white">{{ authStore.getUser?.username }}</span>
            </div>
            <button @click="handleLogout" class="px-4 py-2 text-sm font-semibold text-white bg-white/10 backdrop-blur-sm border border-white/30 rounded-xl hover:bg-white/20 transition-all shadow-lg">
              Déconnexion
            </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- Header -->
    <header class="bg-gradient-to-r from-indigo-900/50 to-purple-900/50 backdrop-blur-xl border-b border-white/10">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div class="flex justify-between items-center">
          <div>
            <h1 class="text-3xl font-black text-white mb-1">Ma Bibliothèque</h1>
            <p class="text-base text-purple-200">Explorez et gérez votre collection</p>
          </div>
          <button @click="showAddModal = true" class="group relative inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-pink-500 to-purple-600 text-white text-sm font-bold rounded-2xl hover:shadow-2xl hover:shadow-pink-500/50 transition-all hover:scale-105">
            <svg class="w-5 h-5 group-hover:rotate-90 transition-transform duration-300" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd"/>
            </svg>
            Ajouter un livre
          </button>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <!-- Filtres -->
      <div class="bg-white/10 backdrop-blur-xl rounded-3xl shadow-2xl p-6 mb-8 border border-white/20">
        <div class="flex flex-col sm:flex-row gap-4">
          <div class="relative flex-1 max-w-md">
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="Rechercher un livre..." 
              class="w-full pl-12 pr-4 py-3 bg-white/10 backdrop-blur-sm border border-white/20 rounded-2xl text-sm text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-pink-500 focus:border-transparent transition-all"
            />
            <svg class="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-white/50" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd"/>
            </svg>
          </div>
          <div class="flex flex-wrap gap-2">
            <button 
              v-for="status in statusFilters" 
              :key="status.value" 
              @click="selectedStatus = status.value"
              :class="[
                'px-5 py-3 text-sm font-bold rounded-2xl transition-all hover:scale-105',
                selectedStatus === status.value
                  ? 'bg-gradient-to-r from-pink-500 to-purple-600 text-white shadow-lg shadow-pink-500/50'
                  : 'bg-white/10 backdrop-blur-sm text-white/80 hover:bg-white/20 border border-white/20'
              ]"
            >
              {{ status.label }}
            </button>
          </div>
        </div>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-6 mb-8">
        <div class="group bg-gradient-to-br from-blue-500 to-blue-700 rounded-3xl shadow-2xl p-6 text-white hover:shadow-blue-500/50 hover:scale-105 transition-all border border-blue-400/30">
          <div class="flex items-center justify-between">
            <div class="w-14 h-14 bg-white/20 backdrop-blur-lg rounded-2xl flex items-center justify-center group-hover:rotate-12 transition-transform">
              <svg class="w-7 h-7" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 715.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z"/>
              </svg>
            </div>
            <div class="text-right">
              <div class="text-4xl font-black mb-1">{{ stats.total }}</div>
              <div class="text-sm text-blue-100 font-semibold">Total</div>
            </div>
          </div>
        </div>
        <div class="group bg-gradient-to-br from-green-500 to-emerald-700 rounded-3xl shadow-2xl p-6 text-white hover:shadow-green-500/50 hover:scale-105 transition-all border border-green-400/30">
          <div class="flex items-center justify-between">
            <div class="w-14 h-14 bg-white/20 backdrop-blur-lg rounded-2xl flex items-center justify-center group-hover:rotate-12 transition-transform">
              <svg class="w-7 h-7" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
              </svg>
            </div>
            <div class="text-right">
              <div class="text-4xl font-black mb-1">{{ stats.completed }}</div>
              <div class="text-sm text-green-100 font-semibold">Terminés</div>
            </div>
          </div>
        </div>
        <div class="group bg-gradient-to-br from-orange-500 to-red-600 rounded-3xl shadow-2xl p-6 text-white hover:shadow-orange-500/50 hover:scale-105 transition-all border border-orange-400/30">
          <div class="flex items-center justify-between">
            <div class="w-14 h-14 bg-white/20 backdrop-blur-lg rounded-2xl flex items-center justify-center group-hover:rotate-12 transition-transform">
              <svg class="w-7 h-7" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clip-rule="evenodd"/>
              </svg>
            </div>
            <div class="text-right">
              <div class="text-4xl font-black mb-1">{{ stats.inProgress }}</div>
              <div class="text-sm text-orange-100 font-semibold">En cours</div>
            </div>
          </div>
        </div>
        <div class="group bg-gradient-to-br from-purple-500 to-pink-600 rounded-3xl shadow-2xl p-6 text-white hover:shadow-purple-500/50 hover:scale-105 transition-all border border-purple-400/30">
          <div class="flex items-center justify-between">
            <div class="w-14 h-14 bg-white/20 backdrop-blur-lg rounded-2xl flex items-center justify-center group-hover:rotate-12 transition-transform">
              <svg class="w-7 h-7" fill="currentColor" viewBox="0 0 20 20">
                <path d="M5 4a2 2 0 012-2h6a2 2 0 012 2v14l-5-2.5L5 18V4z"/>
              </svg>
            </div>
            <div class="text-right">
              <div class="text-4xl font-black mb-1">{{ stats.toRead }}</div>
              <div class="text-sm text-purple-100 font-semibold">À lire</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="isLoading" class="flex justify-center py-12">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-pink-500"></div>
      </div>

      <!-- Empty state -->
      <div v-else-if="filteredBooks.length === 0" class="text-center py-16">
        <div class="w-24 h-24 bg-gradient-to-br from-pink-500/20 to-purple-600/20 backdrop-blur-lg rounded-3xl flex items-center justify-center mx-auto mb-6 border border-white/20">
          <svg class="w-12 h-12 text-white/60" fill="currentColor" viewBox="0 0 20 20">
            <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 715.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z"/>
          </svg>
        </div>
        <h3 class="text-2xl font-black text-white mb-3">Aucun livre trouvé</h3>
        <p class="text-base text-purple-200 mb-8">Commencez votre aventure littéraire maintenant</p>
        <button @click="showAddModal = true" class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-pink-500 to-purple-600 text-white text-sm font-bold rounded-2xl hover:shadow-2xl hover:shadow-pink-500/50 transition-all hover:scale-105">
          Ajouter un livre
        </button>
      </div>

      <!-- Grid de livres -->
      <div v-else>
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-2xl font-black text-white">
            {{ selectedStatus === 'all' ? 'Tous les livres' : statusFilters.find(s => s.value === selectedStatus)?.label }}
            <span class="text-purple-300 font-normal ml-2">({{ filteredBooks.length }})</span>
          </h2>
        </div>
        <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-6">
          <BookCard 
            v-for="book in filteredBooks" 
            :key="book.id" 
            :book="book" 
            @edit="editBook" 
            @delete="deleteBook" 
            @update-status="updateBookStatus" 
            @start-reading="startReading" 
          />
        </div>
      </div>
    </main>

    <!-- Modal -->
    <BookModal v-if="showAddModal || showEditModal" :book="selectedBook" :is-editing="showEditModal" @close="closeModals" @save="saveBook" />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useBooksStore } from '@/stores/booksStore'
import BookCard from '@/components/BookCard.vue'
import BookModal from '@/components/BookModal.vue'

export default {
  name: 'Library',
  components: { BookCard, BookModal },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const booksStore = useBooksStore()

    const searchQuery = ref('')
    const selectedStatus = ref('all')
    const showAddModal = ref(false)
    const showEditModal = ref(false)
    const selectedBook = ref(null)
    const isLoading = ref(true)

    const statusFilters = [
      { value: 'all', label: 'Tous' },
      { value: 'TO_READ', label: 'À lire' },
      { value: 'IN_PROGRESS', label: 'En cours' },
      { value: 'COMPLETED', label: 'Terminés' }
    ]

    const filteredBooks = computed(() => {
      let books = booksStore.books
      if (selectedStatus.value !== 'all') {
        books = books.filter(book => book.status === selectedStatus.value)
      }
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        books = books.filter(book => book.title.toLowerCase().includes(query) || book.author.toLowerCase().includes(query))
      }
      return books
    })

    const stats = computed(() => ({
      total: booksStore.books.length,
      completed: booksStore.books.filter(b => b.status === 'COMPLETED').length,
      inProgress: booksStore.books.filter(b => b.status === 'IN_PROGRESS').length,
      toRead: booksStore.books.filter(b => b.status === 'TO_READ').length
    }))

    const loadBooks = async () => {
      try {
        isLoading.value = true
        await booksStore.fetchUserBooks()
      } catch (error) {
        console.error('Erreur chargement livres:', error)
      } finally {
        isLoading.value = false
      }
    }

    const editBook = (book) => {
      selectedBook.value = book
      showEditModal.value = true
    }

    const deleteBook = async (bookId) => {
      if (confirm('Supprimer ce livre ?')) {
        await booksStore.deleteBook(bookId)
      }
    }

    const updateBookStatus = async (bookId, newStatus) => {
      await booksStore.updateBookStatus(bookId, newStatus)
    }

    const startReading = (book) => {
      router.push(`/reading/${book.id}`)
    }

    const saveBook = async (bookData) => {
      if (showEditModal.value) {
        await booksStore.updateBook(selectedBook.value.id, bookData)
      } else {
        await booksStore.addBook(bookData)
      }
      closeModals()
    }

    const closeModals = () => {
      showAddModal.value = false
      showEditModal.value = false
      selectedBook.value = null
    }

    const handleLogout = () => {
      authStore.logout()
      router.push('/login')
    }

    onMounted(() => {
      loadBooks()
    })

    return {
      authStore,
      searchQuery,
      selectedStatus,
      statusFilters,
      showAddModal,
      showEditModal,
      selectedBook,
      isLoading,
      filteredBooks,
      stats,
      editBook,
      deleteBook,
      updateBookStatus,
      startReading,
      saveBook,
      closeModals,
      handleLogout
    }
  }
}
</script>