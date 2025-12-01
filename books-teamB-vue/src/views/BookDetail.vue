<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <HeaderComponent />
    <Sidebar />
    
    <div class="ml-44 pt-20 px-6 py-8">
      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-[#8B4513]"></div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="flex justify-center items-center py-12">
        <div class="text-center">
          <p class="text-red-600 mb-4">{{ error }}</p>
          <button @click="$router.go(-1)" class="px-4 py-2 bg-[#8B4513] text-white rounded-lg hover:bg-[#6B3410]">
            Retour
          </button>
        </div>
      </div>

      <!-- Book Detail Content -->
      <div v-else-if="book" class="max-w-6xl mx-auto">
        <!-- Back Button -->
        <button 
          @click="$router.go(-1)" 
          class="flex items-center gap-2 mb-6 text-[#8B4513] hover:text-[#6B3410] transition-colors"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
          Retour au catalogue
        </button>

        <!-- Book Header Section -->
        <div class="bg-white rounded-xl shadow-lg p-8 mb-8">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
            <!-- Book Cover -->
            <div class="md:col-span-1">
              <div class="aspect-[3/4] rounded-lg overflow-hidden shadow-md">
                <img 
                  v-if="book.cover" 
                  :src="book.cover" 
                  :alt="book.title"
                  class="w-full h-full object-cover"
                />
                <div 
                  v-else
                  class="w-full h-full bg-gradient-to-br from-orange-400 to-red-600 flex items-center justify-center text-white text-center p-4"
                >
                  <div>
                    <h3 class="font-bold text-lg mb-2">{{ book.title }}</h3>
                    <p class="text-sm opacity-90">{{ getAuthorsNames(book) }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Book Information -->
            <div class="md:col-span-2">
              <div class="space-y-4">
                <div>
                  <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ book.title }}</h1>
                  <p class="text-lg text-gray-600 mb-4">par {{ getAuthorsNames(book) }}</p>
                </div>

                <div class="flex flex-wrap gap-4 text-sm text-gray-600">
                  <span v-if="book.subject" class="inline-flex items-center px-3 py-1 bg-orange-100 text-orange-700 rounded-full">
                    {{ book.subject.text }}
                  </span>
                  <span v-if="book.totalPages" class="inline-flex items-center gap-1">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                    {{ book.totalPages }} pages
                  </span>
                  <span v-if="book.isbn" class="inline-flex items-center gap-1">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a.997.997 0 01-1.414 0l-7-7A1.997 1.997 0 013 12V7a4 4 0 014-4z" />
                    </svg>
                    ISBN: {{ book.isbn }}
                  </span>
                  <span v-if="book.created_at" class="inline-flex items-center gap-1">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    {{ new Date(book.created_at).getFullYear() }}
                  </span>
                </div>

                <!-- Statistics -->
                <div v-if="reviewStats" class="flex items-center gap-6 py-4 border-t border-gray-200">
                  <div class="flex items-center gap-2">
                    <div class="flex text-yellow-400">
                      <svg v-for="star in 5" :key="star" class="w-5 h-5" :class="star <= Math.round(reviewStats.averageRating) ? 'fill-current' : 'fill-gray-300'" viewBox="0 0 20 20">
                        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                      </svg>
                    </div>
                    <span class="text-lg font-medium text-gray-900">{{ reviewStats.averageRating.toFixed(1) }}</span>
                    <span class="text-gray-500">({{ reviewStats.totalReviews }} avis)</span>
                  </div>
                </div>

                <!-- Description -->
                <div v-if="book.desc_txt" class="prose max-w-none">
                  <h3 class="text-lg font-semibold text-gray-900 mb-2">Description</h3>
                  <p class="text-gray-700 leading-relaxed">{{ book.desc_txt }}</p>
                </div>

                <!-- Action Button -->
                <div class="pt-4">
                  <button class="px-6 py-3 bg-[#8B4513] text-white rounded-lg hover:bg-[#6B3410] transition-colors font-medium">
                    Ajouter à ma bibliothèque
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Reviews Section -->
        <div class="bg-white rounded-xl shadow-lg p-8">
          <div class="mb-6">
            <h2 class="text-2xl font-bold text-gray-900 mb-2">Avis des lecteurs</h2>
            <p v-if="reviewStats" class="text-gray-600">
              {{ reviewStats.totalReviews }} {{ reviewStats.totalReviews > 1 ? 'avis' : 'avis' }} sur ce livre
            </p>
          </div>

          <!-- Reviews List Placeholder -->
          <div class="space-y-6">
            <!-- Sample Review (à remplacer par le composant ReviewsList) -->
            <div class="border-b border-gray-200 pb-6 last:border-b-0">
              <div class="flex items-start gap-4">
                <div class="w-10 h-10 bg-[#8B4513] rounded-full flex items-center justify-center text-white font-medium text-sm">
                  U
                </div>
                <div class="flex-1">
                  <div class="flex items-center gap-2 mb-2">
                    <span class="font-medium text-gray-900">Utilisateur</span>
                    <div class="flex text-yellow-400 text-sm">
                      <svg v-for="star in 5" :key="star" class="w-4 h-4 fill-current" viewBox="0 0 20 20">
                        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                      </svg>
                    </div>
                    <span class="text-sm text-gray-500">Il y a 2 jours</span>
                  </div>
                  <p class="text-gray-700">
                    Les avis des utilisateurs apparaîtront ici une fois le composant ReviewsList implémenté.
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Add Review Form Placeholder -->
          <div class="mt-8 pt-6 border-t border-gray-200">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Donnez votre avis</h3>
            <p class="text-gray-600">Le formulaire d'avis sera implémenté ici avec le composant ReviewForm.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import HeaderComponent from '@/components/HeaderComponent.vue'
import Sidebar from '@/components/Sidebar.vue'
import booksService from '@/services/booksService'

export default {
  name: 'BookDetail',
  components: {
    HeaderComponent,
    Sidebar
  },
  data() {
    return {
      book: null,
      loading: true,
      error: null,
      reviewStats: null
    }
  },
  async mounted() {
    await this.loadBookDetails()
  },
  watch: {
    '$route.params.id': {
      handler() {
        this.loadBookDetails()
      },
      immediate: false
    }
  },
  methods: {
    async loadBookDetails() {
      try {
        this.loading = true
        this.error = null
        
        const bookId = this.$route.params.id
        this.book = await booksService.getBookById(bookId)
        
        // TODO: Charger les statistiques des avis
        this.reviewStats = {
          averageRating: 4.2,
          totalReviews: 8
        }
        
      } catch (error) {
        console.error('Erreur lors du chargement du livre:', error)
        this.error = 'Impossible de charger les détails du livre'
      } finally {
        this.loading = false
      }
    },
    
    getAuthorsNames(book) {
      if (!book.authors || book.authors.length === 0) {
        return 'Auteur inconnu'
      }
      return book.authors.map(author => `${author.firstName} ${author.lastName}`).join(', ')
    }
  }
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.prose {
  line-height: 1.7;
}
</style>