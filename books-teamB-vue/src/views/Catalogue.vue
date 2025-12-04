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

      <!-- Books Grid -->
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
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

        <!-- Books Grid -->
        <div
          v-else
          v-for="book in filteredBooks"
          :key="book.id"
          class="bg-white rounded-lg p-3 shadow-sm hover:shadow-md transition-shadow cursor-pointer"
        >
          <!-- Book Cover -->
          <div class="aspect-[3/4] rounded-lg mb-3 overflow-hidden">
            <img 
              v-if="book.cover" 
              :src="book.cover" 
              :alt="book.title"
              class="w-full h-full object-cover"
              @error="$event.target.style.display='none'"
            />
            <div 
              v-else
              class="w-full h-full bg-gradient-to-br from-orange-400 to-red-600 flex items-center justify-center text-white text-center p-2"
            >
              <div>
                <h3 class="font-bold text-xs mb-1 leading-tight">{{ book.title }}</h3>
                <p class="text-xs opacity-90">{{ getAuthorsNames(book) }}</p>
              </div>
            </div>
          </div>
          
          <!-- Book Info -->
          <div class="space-y-1">
            <h3 class="font-semibold text-xs text-gray-900 line-clamp-2 leading-tight">{{ book.title }}</h3>
            <p class="text-xs text-gray-600">{{ getAuthorsNames(book) }}</p>
            <div class="flex items-center gap-2">
              <span v-if="book.subject" class="inline-block px-2 py-0.5 bg-orange-100 text-orange-700 text-xs rounded-full">
                {{ book.subject.text }}
              </span>
              <span v-if="book.created_at" class="text-xs text-gray-500">
                {{ new Date(book.created_at).getFullYear() }}
              </span>
            </div>
            <p v-if="book.desc_txt" class="text-xs text-gray-500 line-clamp-2 mt-1">
              {{ book.desc_txt }}
            </p>
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
              <span class="ml-2 text-sm text-gray-700">avant 2000</span>
            </label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import HeaderComponent from '@/components/HeaderComponent.vue'
import { useBooksStore } from '@/stores/booksStore'

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
      books: [
        { 
          id: 1, 
          title: 'Bloodmarked', 
          authors: [{ firstName: 'Tracy', lastName: 'Deonn' }],
          year: '2010', 
          genre: 'Fantasy',
          subject: { text: 'Fantasy' },
          desc_txt: 'The Legendborn Cycle, Book 2. Bree wants to heal, but the war has just begun.',
          cover: 'https://covers.openlibrary.org/a/id/12617874-L.jpg',
          created_at: '2010-03-02T03:44:17'
        },
        { 
          id: 2, 
          title: 'In Other Lands', 
          authors: [{ firstName: 'Sarah', lastName: 'Rees Brennan' }],
          year: '2017', 
          genre: 'Young Adult',
          subject: { text: 'Young Adult' },
          desc_txt: 'The Borderlands aren\'t like anywhere else. Don\'t try to smuggle a phone... Elliot is thirteen years old. He\'s smart and just a tiny bit obnoxious.',
          cover: 'https://covers.openlibrary.org/a/id/12354250-L.jpg',
          created_at: '2017-10-20T01:27:09'
        },
        { 
          id: 3, 
          title: 'Strange the Dreamer', 
          authors: [{ firstName: 'Laini', lastName: 'Taylor' }],
          year: '2018', 
          genre: 'Fantasy',
          subject: { text: 'Fantasy' },
          desc_txt: 'The dream chooses the dreamer, not the other way around. Lazlo Strange, war orphan and junior librarian, has always feared that his dream chose poorly.',
          cover: 'https://covers.openlibrary.org/a/id/8367802-L.jpg',
          created_at: '2018-05-15T01:36:29'
        },
        { 
          id: 4, 
          title: 'Muse of Nightmares', 
          authors: [{ firstName: 'Laini', lastName: 'Taylor' }],
          year: '2018', 
          genre: 'Fantasy',
          subject: { text: 'Fantasy' },
          desc_txt: 'In the wake of tragedy, neither Lazlo nor Sarai are who they were before. One a god, the other a ghost, they struggle to grasp the new boundaries of their selves.',
          cover: 'https://covers.openlibrary.org/a/id/12366752-L.jpg',
          created_at: '2018-10-18T01:40:34'
        },
        { 
          id: 5, 
          title: "The Gentleman's Guide to Vice and Virtue", 
          authors: [{ firstName: 'Mackenzi', lastName: 'Lee' }],
          year: '2018', 
          genre: 'Romance',
          subject: { text: 'Romance' },
          desc_txt: 'A young bisexual British lord embarks on an unforgettable Grand Tour of Europe with his best friend/secret crush.',
          cover: 'https://covers.openlibrary.org/b/id/8225261-L.jpg',
          created_at: '2018-01-15T10:20:30'
        },
        { 
          id: 6, 
          title: 'The Darkest Minds', 
          authors: [{ firstName: 'Alexandra', lastName: 'Bracken' }],
          year: '2012', 
          genre: 'Young Adult',
          subject: { text: 'Young Adult' },
          desc_txt: 'When Ruby woke up on her tenth birthday, something about her had changed. Something alarming enough to make her parents lock her in the garage.',
          cover: 'https://covers.openlibrary.org/b/id/7572080-L.jpg',
          created_at: '2012-12-18T14:30:45'
        }
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
          (book.authors && book.authors.some(author => 
            `${author.firstName} ${author.lastName}`.toLowerCase().includes(this.searchQuery.toLowerCase())
          )) ||
          (book.desc_txt && book.desc_txt.toLowerCase().includes(this.searchQuery.toLowerCase()))
        )
      }

      // Filter by genre (using subject)
      if (this.selectedGenre !== 'tous') {
        filtered = filtered.filter(book => 
          book.subject && book.subject.text === this.selectedGenre
        )
      }

      // Filter by year (using created_at)
      if (this.selectedYear !== 'toutes') {
        filtered = filtered.filter(book => {
          if (!book.created_at) return false
          const year = new Date(book.created_at).getFullYear()
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
  },
  methods: {
    getAuthorsNames(book) {
      if (!book.authors || book.authors.length === 0) {
        return 'Auteur inconnu'
      }
      return book.authors.map(author => `${author.firstName} ${author.lastName}`).join(', ')
    }
  }
}
</script>
