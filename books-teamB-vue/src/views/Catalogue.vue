<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <HeaderComponent />
    <Sidebar />
    <div class="ml-44 pt-20 px-6 py-8">
      <!-- Page Header -->
      <div class="mb-8">
        <h1 class="text-4xl font-bold text-[#8B4513] mb-2">Catalogue</h1>
        <p class="text-gray-600">Explorer notre catalogue de plus de 70 livres</p>
      </div>

      <!-- Search Bar -->
      <div class="mb-6 flex gap-4">
        <div class="flex-1 relative">
          <svg class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="ex. : Harry Potter"
            class="w-full pl-10 pr-4 py-3 bg-white border border-gray-300 rounded-xl text-sm placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent"
          />
        </div>
        
        <!-- Filters Button -->
        <button 
          @click="showFilters = !showFilters"
          class="flex items-center gap-2 px-4 py-3 bg-white border border-gray-300 rounded-xl text-sm text-gray-700 hover:bg-gray-50"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.707A1 1 0 013 7V4z" />
          </svg>
          Filtres
        </button>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <div class="text-center">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500 mx-auto mb-4"></div>
          <p class="text-gray-600">Chargement des livres...</p>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="flex justify-center items-center py-12">
        <div class="text-center">
          <svg class="h-12 w-12 text-red-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <p class="text-red-600 mb-2">Erreur lors du chargement</p>
          <p class="text-sm text-gray-400">{{ error }}</p>
          <button @click="loadBooks()" class="mt-4 px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600">
            Réessayer
          </button>
        </div>
      </div>

      <!-- Books Grid -->
      <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
        <!-- Empty State -->
        <div v-if="filteredBooks.length === 0" class="col-span-full flex justify-center items-center py-12">
          <div class="text-center">
            <svg class="h-12 w-12 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
            <p class="text-gray-500 mb-2">Aucun livre trouvé</p>
            <p class="text-sm text-gray-400">Essayez de modifier vos filtres de recherche</p>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="filteredBooks.length === 0" class="col-span-full flex justify-center items-center py-12">
          <div class="text-center">
            <svg class="h-12 w-12 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
            <p class="text-gray-500 mb-2">Aucun livre trouvé</p>
            <p class="text-sm text-gray-400">Essayez de modifier vos filtres de recherche</p>
          </div>
        </div>

        <!-- Book Cards -->
        <div v-for="book in filteredBooks" :key="book.id" class="group cursor-pointer" @click="goToBookDetail(book.id)">
          <div class="bg-white rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-all duration-200 group-hover:scale-[1.02]">
            <!-- Book Cover -->
            <div class="aspect-[2/3] bg-gray-100 overflow-hidden">
              <img 
                :src="book.cover || '/placeholder-book.jpg'" 
                :alt="`Couverture de ${book.title}`"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
                @error="$event.target.src = '/placeholder-book.jpg'"
              />
            </div>
            
            <!-- Book Info -->
            <div class="p-4">
              <h3 class="font-semibold text-sm text-gray-900 mb-1 line-clamp-2 group-hover:text-orange-600 transition-colors">
                {{ book.title }}
              </h3>
              <p class="text-xs text-gray-500 mb-2">{{ getAuthorsNames(book) }}</p>
              <div class="flex items-center justify-between">
                <span v-if="book.subject" class="text-xs bg-orange-100 text-orange-600 px-2 py-1 rounded-full">
                  {{ book.subject.text }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Filters Sidebar -->
      <div 
        v-if="showFilters"
        class="fixed top-16 right-0 bottom-0 w-80 bg-white border-l border-gray-200 z-30 p-6 overflow-y-auto"
      >
        <div class="flex items-center justify-between mb-6">
          <div class="flex items-center gap-2">
            <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.707A1 1 0 013 7V4z" />
            </svg>
            <h2 class="text-lg font-semibold text-gray-900">Filtres</h2>
          </div>
          <button @click="showFilters = false" class="text-gray-400 hover:text-gray-600">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Genre Filter -->
        <div class="mb-6">
          <h3 class="text-sm font-semibold text-gray-900 mb-3">Genre</h3>
          <div class="space-y-2">
            <label class="flex items-center">
              <input type="radio" v-model="selectedGenre" value="tous" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">Tous</span>
            </label>
            <label class="flex items-center">
              <input type="radio" v-model="selectedGenre" value="Fantasy" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">Fantasy</span>
            </label>
            <label class="flex items-center">
              <input type="radio" v-model="selectedGenre" value="Young Adult" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">Young Adult</span>
            </label>
            <label class="flex items-center">
              <input type="radio" v-model="selectedGenre" value="Aventure" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">Aventure</span>
            </label>
            <label class="flex items-center">
              <input type="radio" v-model="selectedGenre" value="Romance" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">Romance</span>
            </label>
          </div>
        </div>

        <!-- Year Filter -->
        <div class="mb-6">
          <h3 class="text-sm font-semibold text-gray-900 mb-3">Année de publication</h3>
          <div class="space-y-2">
            <label class="flex items-center">
              <input type="radio" v-model="selectedYear" value="toutes" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">Toutes</span>
            </label>
            <label class="flex items-center">
              <input type="radio" v-model="selectedYear" value="2025-2020" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">2025-2020</span>
            </label>
            <label class="flex items-center">
              <input type="radio" v-model="selectedYear" value="2019-2010" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">2019-2010</span>
            </label>
            <label class="flex items-center">
              <input type="radio" v-model="selectedYear" value="2009-2000" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">2009-2000</span>
            </label>
            <label class="flex items-center">
              <input type="radio" v-model="selectedYear" value="avant 2000" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-bn text-gray-700">avant 2000</span>
            </label>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters Overlay -->
    <div 
      v-if="showFilters"
      @click="showFilters = false"
      class="fixed inset-0 bg-black bg-opacity-50 z-20"
    ></div>
  </div>
</template>

<script>
import Sidebar from '@/components/Sidebar.vue'
import HeaderComponent from '@/components/HeaderComponent.vue'
import booksService from '@/services/booksService'

export default {
  name: 'CataloguePage',
  components: {
    HeaderComponent,
    Sidebar
  },
  data() {
    return {
      searchQuery: '',
      selectedGenre: 'tous',
      selectedYear: 'toutes',
      showFilters: false,
      books: [],
      loading: true,
      error: null
    }
  },
  async mounted() {
    await this.loadBooks()
  },
  computed: {
    filteredBooks() {
      let filtered = this.books

      // Filter by search query
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        filtered = filtered.filter(book => {
          return book.title?.toLowerCase().includes(query) ||
                 book.authorsNames?.some(author => 
                   author.toLowerCase().includes(query)
                 ) ||
                 book.subject?.text?.toLowerCase().includes(query)
        })
      }

      // Filter by genre
      if (this.selectedGenre !== 'tous') {
        filtered = filtered.filter(book => 
          book.subject && book.subject.text === this.selectedGenre
        )
      }

      // Note: Pas de filtre par année car BookSummaryDto ne contient pas publishedDate
      // Si on veut filtrer par année, il faudrait l'ajouter au DTO

      return filtered
    },
    
    availableGenres() {
      const genres = new Set()
      this.books.forEach(book => {
        if (book.subject && book.subject.text) {
          genres.add(book.subject.text)
        }
      })
      return Array.from(genres).sort()
    }
  },
  methods: {
    async loadBooks() {
      try {
        this.loading = true
        this.error = null
        
        const response = await booksService.getCatalogBooks()
        this.books = response.content || response || []
        
      } catch (error) {
        console.error('Erreur lors du chargement des livres:', error)
        this.error = 'Impossible de charger les livres. Vérifiez que le serveur est démarré.'
      } finally {
        this.loading = false
      }
    },
    
    getAuthorsNames(book) {
      if (!book.authorsNames || book.authorsNames.length === 0) {
        return 'Auteur inconnu'
      }
      return book.authorsNames.join(', ')
    },
    
    goToBookDetail(bookId) {
      this.$router.push({ name: 'book-detail', params: { id: bookId } })
    }
  }
}
</script>
