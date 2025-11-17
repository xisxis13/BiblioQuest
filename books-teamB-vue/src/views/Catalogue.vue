<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <Header />
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

      <!-- Books Grid -->
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
        <div
          v-for="book in filteredBooks"
          :key="book.id"
          class="bg-white rounded-lg p-3 shadow-sm hover:shadow-md transition-shadow cursor-pointer"
        >
          <!-- Book Cover -->
          <div class="aspect-[3/4] bg-gradient-to-br from-blue-400 to-purple-600 rounded-lg mb-3 flex items-center justify-center text-white text-center p-2">
            <div>
              <h3 class="font-bold text-xs mb-1 leading-tight">{{ book.title }}</h3>
              <p class="text-xs opacity-90">{{ book.author }}</p>
            </div>
          </div>
          
          <!-- Book Info -->
          <div class="space-y-1">
            <h3 class="font-semibold text-xs text-gray-900 line-clamp-2 leading-tight">{{ book.title }}</h3>
            <p class="text-xs text-gray-600">{{ book.author }}</p>
            <span class="inline-block px-2 py-0.5 bg-orange-100 text-orange-700 text-xs rounded-full">{{ book.year }}</span>
          </div>
        </div>
      </div>

      <!-- Filters Sidebar (Fixed Right) -->
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
              <input type="radio" v-model="selectedGenre" value="Fantaisie" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">Fantaisie</span>
            </label>
            <label class="flex items-center">
              <input type="radio" v-model="selectedGenre" value="Science-Fiction" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">Science-Fiction</span>
            </label>
            <label class="flex items-center">
              <input type="radio" v-model="selectedGenre" value="Romance" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">Romance</span>
            </label>
            <label class="flex items-center">
              <input type="radio" v-model="selectedGenre" value="Thriller" class="text-orange-500 focus:ring-orange-500">
              <span class="ml-2 text-sm text-gray-700">Thriller</span>
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
              <span class="ml-2 text-sm text-gray-700">avant 2000</span>
            </label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Header from '@/components/Header.vue'
import Sidebar from '@/components/Sidebar.vue'

export default {
  name: 'CataloguePage',
  components: {
    Header,
    Sidebar
  },
  data() {
    return {
      searchQuery: '',
      selectedGenre: 'tous',
      selectedYear: 'toutes',
      showFilters: false,
      books: [
        { id: 1, title: 'In Other Lands', author: 'Sarah Rees Brennan', year: '2017', genre: 'Fantaisie' },
        { id: 2, title: "The Gentleman's Guide to Vice and Virtue", author: 'Mackenzi Lee', year: '2018', genre: 'Romance' },
        { id: 3, title: 'Anger is a Gift', author: 'Mark Oshiro', year: '2018', genre: 'Contemporary' },
        { id: 4, title: "The Lady's Guide to Petticoats and Piracy", author: 'Mackenzi Lee', year: '2018', genre: 'Romance' },
        { id: 5, title: 'The Ruin of a Rake', author: 'Mackenzi Lee', year: '2019', genre: 'Romance' },
        { id: 6, title: 'The Darkest Minds', author: 'Alexandra Bracken', year: '2012', genre: 'Science-Fiction' },
        { id: 7, title: 'Nevernight', author: 'Jay Kristoff', year: '2016', genre: 'Fantaisie' },
        { id: 8, title: 'Illuminae', author: 'Amie Kaufman', year: '2015', genre: 'Science-Fiction' },
        { id: 9, title: 'Gemina', author: 'Amie Kaufman', year: '2016', genre: 'Science-Fiction' },
        { id: 10, title: 'Obsidio', author: 'Amie Kaufman', year: '2018', genre: 'Science-Fiction' }
      ]
    }
  },
  computed: {
    filteredBooks() {
      let filtered = this.books

      // Filter by search query
      if (this.searchQuery) {
        filtered = filtered.filter(book =>
          book.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          book.author.toLowerCase().includes(this.searchQuery.toLowerCase())
        )
      }

      // Filter by genre
      if (this.selectedGenre !== 'tous') {
        filtered = filtered.filter(book => book.genre === this.selectedGenre)
      }

      // Filter by year
      if (this.selectedYear !== 'toutes') {
        filtered = filtered.filter(book => {
          const year = parseInt(book.year)
          switch (this.selectedYear) {
            case '2025-2020':
              return year >= 2020 && year <= 2025
            case '2019-2010':
              return year >= 2010 && year <= 2019
            case '2009-2000':
              return year >= 2000 && year <= 2009
            case 'avant 2000':
              return year < 2000
            default:
              return true
          }
        })
      }

      return filtered
    }
  }
}
</script>
