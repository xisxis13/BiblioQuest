<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <Header />
    <Sidebar />
    <div class="ml-44 pt-20 px-6 py-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-4xl font-bold text-foreground mb-2">Ma Bibliothèque</h1>
        <p class="text-muted-foreground">Gérez vos livres et suivez votre progression de lecture</p>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div class="bg-white border border-border rounded-2xl p-6">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <div>
              <p class="text-sm text-muted-foreground">Livres lus</p>
              <p class="text-2xl font-bold text-foreground">{{ stats.totalBooksRead }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white border border-border rounded-2xl p-6">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
            </div>
            <div>
              <p class="text-sm text-muted-foreground">En cours</p>
              <p class="text-2xl font-bold text-foreground">{{ stats.booksInProgress }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white border border-border rounded-2xl p-6">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-yellow-100 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z" />
              </svg>
            </div>
            <div>
              <p class="text-sm text-muted-foreground">À lire</p>
              <p class="text-2xl font-bold text-foreground">{{ stats.booksToRead }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white border border-border rounded-2xl p-6">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-purple-100 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <div>
              <p class="text-sm text-muted-foreground">Pages lues</p>
              <p class="text-2xl font-bold text-foreground">{{ stats.totalPagesRead }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Tabs -->
      <div class="mb-6">
        <div class="flex gap-4 border-b border-border">
          <button
            v-for="tab in tabs"
            :key="tab.value"
            @click="activeTab = tab.value"
            :class="[
              'px-6 py-3 font-semibold transition-colors relative',
              activeTab === tab.value
                ? 'text-primary'
                : 'text-muted-foreground hover:text-foreground'
            ]"
          >
            {{ tab.label }}
            <div
              v-if="activeTab === tab.value"
              class="absolute bottom-0 left-0 right-0 h-0.5 bg-primary rounded-full"
            ></div>
          </button>
        </div>
      </div>

      <!-- Books Grid -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <div
          v-for="book in filteredBooks"
          :key="book.id"
          class="bg-white border border-border rounded-2xl overflow-hidden hover:shadow-lg transition-shadow"
        >
          <!-- Book Cover -->
          <div class="aspect-[2/3] bg-gradient-to-br from-primary/20 to-accent/20 relative">
            <img
              v-if="book.coverUrl"
              :src="book.coverUrl"
              :alt="book.title"
              class="w-full h-full object-cover"
            />
            <div v-else class="w-full h-full flex items-center justify-center text-muted-foreground">
              <svg class="w-16 h-16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
            </div>
            
            <!-- Status Badge -->
            <div class="absolute top-3 right-3">
              <span 
                :class="[
                  'px-3 py-1 rounded-full text-xs font-medium',
                  getStatusBadgeClass(book.status)
                ]"
              >
                {{ getStatusLabel(book.status) }}
              </span>
            </div>
          </div>

          <!-- Book Info -->
          <div class="p-4">
            <h3 class="font-semibold text-foreground mb-1 line-clamp-2">
              {{ book.title }}
            </h3>
            <p class="text-sm text-muted-foreground mb-3">
              {{ book.author }}
            </p>

            <!-- Progress Bar for books in progress -->
            <ProgressBar
              v-if="book.status === 'reading' && book.progress"
              :current-value="book.progress.currentPage"
              :max-value="book.progress.totalPages"
              :show-details="false"
              label=""
            />

            <!-- Action Buttons -->
            <div class="flex gap-2 mt-4">
              <button
                @click="updateBookStatus(book.id, 'read')"
                :class="[
                  'flex-1 px-3 py-2 rounded-lg text-sm font-medium transition-colors',
                  book.status === 'read'
                    ? 'bg-green-500 text-white'
                    : 'bg-secondary text-secondary-foreground hover:bg-secondary/80'
                ]"
              >
                Lu
              </button>
              <button
                @click="updateBookStatus(book.id, 'reading')"
                :class="[
                  'flex-1 px-3 py-2 rounded-lg text-sm font-medium transition-colors',
                  book.status === 'reading'
                    ? 'bg-primary text-primary-foreground'
                    : 'bg-secondary text-secondary-foreground hover:bg-secondary/80'
                ]"
              >
                En cours
              </button>
              <button
                @click="updateBookStatus(book.id, 'to-read')"
                :class="[
                  'flex-1 px-3 py-2 rounded-lg text-sm font-medium transition-colors',
                  book.status === 'to-read'
                    ? 'bg-yellow-500 text-white'
                    : 'bg-secondary text-secondary-foreground hover:bg-secondary/80'
                ]"
              >
                À lire
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="filteredBooks.length === 0" class="text-center py-20">
        <svg class="w-16 h-16 mx-auto text-muted-foreground mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
        </svg>
        <p class="text-muted-foreground mb-4">Aucun livre dans cette catégorie</p>
        <router-link
          to="/library"
          class="inline-flex items-center gap-2 px-6 py-3 bg-primary hover:bg-primary/90 text-primary-foreground font-semibold rounded-xl transition-colors"
        >
          Explorer la Bibliothèque
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import ProgressBar from '@/components/ProgressBar.vue'
import Sidebar from '@/components/Sidebar.vue'
import Header from '@/components/Header.vue'

export default {
  name: 'MyLibraryPage',
  components: {
    ProgressBar,
    Sidebar,
    Header
  },
  data() {
    return {
      activeTab: 'all',
      tabs: [
        { label: 'Tous', value: 'all' },
        { label: 'En cours', value: 'reading' },
        { label: 'À lire', value: 'to-read' },
        { label: 'Lus', value: 'read' }
      ],
      stats: {
        totalBooksRead: 0,
        booksInProgress: 0,
        booksToRead: 0,
        totalPagesRead: 0
      },
      userBooks: []
    }
  },
  computed: {
    filteredBooks() {
      if (this.activeTab === 'all') {
        return this.userBooks
      }
      return this.userBooks.filter(book => book.status === this.activeTab)
    }
  },
  methods: {
    getStatusLabel(status) {
      const labels = {
        'reading': 'En cours',
        'to-read': 'À lire',
        'read': 'Lu'
      }
      return labels[status] || status
    },
    getStatusBadgeClass(status) {
      const classes = {
        'reading': 'bg-primary text-primary-foreground',
        'to-read': 'bg-yellow-100 text-yellow-700',
        'read': 'bg-green-100 text-green-700'
      }
      return classes[status] || 'bg-secondary text-secondary-foreground'
    },
    async updateBookStatus(bookId, status) {
      const book = this.userBooks.find(b => b.id === bookId)
      if (book) {
        book.status = status
        // Mise à jour des stats
        this.stats.booksInProgress = this.userBooks.filter(b => b.status === 'reading').length
        this.stats.booksToRead = this.userBooks.filter(b => b.status === 'to-read').length
        this.stats.totalBooksRead = this.userBooks.filter(b => b.status === 'read').length
        // API call to update status
        // await axios.put(`/api/users/books/${bookId}`, { status })
      }
    },
    async loadUserBooks() {
      // Mock data - replace with API call
      this.userBooks = [
        {
          id: 1,
          title: 'In Other Lands',
          author: 'Sarah Rees Brennan',
          status: 'reading',
          coverUrl: '',
          progress: {
            currentPage: 150,
            totalPages: 400
          }
        }
      ]
      
      // Update stats
      this.stats.booksInProgress = this.userBooks.filter(b => b.status === 'reading').length
      this.stats.booksToRead = this.userBooks.filter(b => b.status === 'to-read').length
      this.stats.totalBooksRead = this.userBooks.filter(b => b.status === 'read').length
    }
  },
  mounted() {
    this.loadUserBooks()
  }
}
</script>
