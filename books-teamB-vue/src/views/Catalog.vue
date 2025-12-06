<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen">
    <AppHeader />
    
    <main class="w-full flex-1 min-h-0 flex gap-3">
      <MenuBar />

      <div class="bg-box border border-border rounded-3xl flex-1 h-full overflow-y-auto flex flex-col gap-4 p-6">
        <!-- Header -->
        <div class="w-full flex items-center justify-between">
          <div class="w-full h-fit flex flex-col gap-1">
            <span class="text-primary-text font-semibold text-2xl">Catalogue</span>
            <span class="text-secondary-text font-normal text-base">Explorer notre catalogue de plus de {{ Math.floor(books.length / 10) * 10 }} livres</span>
          </div>

          <!-- Publish book button  -->
          <button 
            @click="goToPublishBook"
            type="button"
            class="bg-accent hover:bg-accent/90 rounded-2xl px-4 py-3 text-box font-normal cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 whitespace-nowrap"
          >
            <Plus :size="22" />
            <span>Publier un livre</span>
          </button>
        </div>

        <div class="w-full flex gap-3">
          <!-- Search bar -->
          <div class="relative w-full bg-background border border-border rounded-full">
            <Search class="absolute top-1/2 -translate-y-1/2 left-3 z-10 text-secondary-text" :size="24" />
            <input 
              v-model="searchQuery"
              type="text" 
              placeholder="ex. : Harry Potter" 
              class="px-4 py-2 pl-11 w-full h-full rounded-full font-normal focus:outline-none focus:ring-1 focus:ring-accent"
            >
          </div>

          <!-- Filters button -->
          <button
            @click="showFilters = !showFilters"
            class="bg-background hover:bg-box transition-colors border border-border rounded-full w-fit h-fit p-2.5 aspect-square cursor-pointer"
          >
            <Filter class="text-secondary-text" :size="22" />
          </button>
        </div>

        <!-- Loading state -->
        <div v-if="loading" class="w-full h-full flex items-center justify-center flex-col gap-3">
          <svg class="animate-spin h-10 w-10 text-secondary-text" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p class="text-secondary-text">Chargement des livres...</p>
        </div>

        <!-- Error State -->
        <div 
          v-else-if="error"
          class="w-full h-full flex items-center justify-center flex-col gap-3"
        >
          <Info class="stroke-secondary-text" :size="50" />
          <p class="text-secondary-text">Erreur lors du chargement</p>
          <p class="text-sm text-gray-400">{{ error }}</p>
          
          <button 
            @click="loadBooks()" 
            type="button"
            class="bg-accent hover:bg-accent/90 rounded-2xl px-4 py-3 text-box font-normal cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 whitespace-nowrap"
          >
            <RotateCw :size="20" />
            <span>Réassayer</span>
          </button>
        </div>

        <!-- Book List -->
        <div class="grid grid-cols-1 min-[520px]:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-4">
          <BookCard 
            v-for="book in filteredBooks"
            @click="goToBookDetail(book.id)"
            :key="book.id"
            :book="book"
            :show-actions="false"
          />
        </div>

        <!-- Empty state -->
        <div v-if="!loading && filteredBooks.length === 0" class="w-full h-full flex items-center justify-center flex-col gap-3">
          <BookOpen class="stroke-secondary-text" :size="50" />
          <p class="text-secondary-text">Aucun livre ne correspond à votre recherche</p>
        </div>
      </div>

      <!-- Filters sidebar -->
      <div
        v-if="showFilters"
        class="bg-box border border-border rounded-3xl w-64 h-full p-5 flex flex-col items-start justify-start gap-4 overflow-hidden"
      >
        <span class="w-full pb-3 border-b border-border text-primary-text font-medium text-lg">Filtres</span>

        <!-- Genre filter -->
        <div class="w-full py-2 flex flex-col gap-3">
          <span class="text-primary-text text-base font-medium">Genre</span>
          <div class="flex flex-wrap gap-2">
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedGenre" value="tous" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">Tous</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedGenre" value="Fantasy" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">Fantasy</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedGenre" value="Young Adult" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">Young Adult</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedGenre" value="Aventure" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">Aventure</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedGenre" value="Romance" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">Romance</span>
            </label>
          </div>
        </div>

        <!-- Year filter -->
        <div class="w-full py-2 flex flex-col gap-3">
          <span class="text-primary-text text-base font-medium">Année de publication</span>
          <div class="flex flex-wrap gap-2">
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedYear" value="toutes" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">Toutes</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedYear" value="2025-2020" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">2025-2020</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedYear" value="2019-2010" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">2019-2010</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedYear" value="2009-2000" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">2009-2000</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedYear" value="avant 2000" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">avant 2000</span>
            </label>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import AppHeader from '@/components/AppHeader.vue'
import MenuBar from '@/components/MenuBar.vue'
import BookCard from '@/components/BookCard.vue'
import booksService from '@/services/booksService'
import { Search, Filter, Plus, Info, RotateCw, BookOpen } from 'lucide-vue-next'

export default {
  name: 'CataloguePage',
  components: {
    AppHeader,
    MenuBar,
    Search,
    Filter,
    Plus,
    Info,
    RotateCw, 
    BookOpen,
    BookCard,
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
    },

    goToPublishBook() {
      this.$router.push({ name: 'book-add' })
    }
  }
}
</script>
