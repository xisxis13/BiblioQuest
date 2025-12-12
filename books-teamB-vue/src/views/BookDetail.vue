<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen">
    <AppHeader />

    <main class="w-full flex-1 min-h-0 flex gap-3">
      <MenuBar />

      <div class="bg-box border border-border rounded-3xl flex-1 h-full overflow-y-auto flex flex-col gap-6 p-6">
        <!-- Loading state -->
        <div v-if="loading" class="w-full h-full flex items-center justify-center flex-col gap-3">
          <svg class="animate-spin h-10 w-10 text-secondary-text" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p class="text-secondary-text">Chargement du livre...</p>
        </div>

        <!-- Error state -->
        <div v-else-if="error" class="w-full h-full flex items-center justify-center">
          <span class="text-red-500">{{ error }}</span>
        </div>

        <!-- Book content -->
        <template v-else>
          <!-- Top action buttons -->
          <div class="w-full flex items-center justify-between">
            <button
              @click="$router.go(-1)"
              class="w-fit flex items-center gap-2 text-secondary-text hover:text-primary-text transition-colors cursor-pointer"
            >
              <ChevronLeft :size="22" />
              Retour
            </button>

            <div class="flex items-center gap-3">
              <button
                @click="$router.push({ name: 'book-edit', params: { id: book.id } })"
                class="bg-box hover:bg-background border border-border rounded-full px-4 py-2 cursor-pointer transition-colors flex items-center justify-center gap-2"
              >
                <PenLine :size="20" class="stroke-primary-text" />
                <span class="text-primary-text font-normal">Modifier</span>
              </button>

              <button
                class="bg-accent hover:bg-accent/90 rounded-full px-4 py-2 text-box font-normal cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                <Plus :size="22" />
                <span>Ajouter à ma bibliothèque</span>
              </button>
            </div>
          </div>

          <!-- Book header -->
          <div class="grid grid-cols-1 md:grid-cols-4 px-2 gap-6">
            <!-- Book Cover -->
            <div class="relative aspect-2/3 bg-accent/30 rounded-2xl overflow-hidden md:col-span-1">
              <img
                v-if="book.cover"
                :src="book.cover"
                :alt="book.title + ' cover'"
                class="w-full h-full object-cover"
              >
              <div v-else class="w-full h-full p-3 flex flex-col items-center justify-center gap-2">
                <BookOpen class="stroke-secondary-text" :size="50" />
                <span class="text-secondary-text text-base font-normal text-center">Livre sans couverture</span>
              </div>
            </div>

            <!-- Book information -->
            <div class="md:col-span-3 pb-3 flex flex-col items-start justify-end gap-4">
              <!-- add subject -->
              <span class="w-full text-base font-regular text-primary-text">
                Par {{ getAuthorsNames(book) }} •
                <span class="text-base font-regular text-secondary-text">{{ new Date(book.created_at).getFullYear() }}</span>
              </span>
              <span class="w-full text-5xl font-bold text-primary-text">{{ book.title }}</span>
              <span v-if="book.isbn" class="text-base font-regular text-primary-text">ISBN : {{ book.isbn }}</span>

              <!-- Statistics -->
              <div v-if="reviewStats" class="flex items-center gap-6">
                <div class="flex items-center gap-3">
                  <div class="flex gap-1">
                    <template v-for="starIndex in 5" :key="starIndex">
                      <div class="relative">
                        <Star
                          :size="20"
                          :class="{
                            'fill-accent stroke-accent': starIndex <= Math.floor(reviewStats.averageRating),
                            'fill-transparent stroke-[#A68060]': starIndex > Math.ceil(reviewStats.averageRating) || (starIndex === Math.ceil(reviewStats.averageRating) && reviewStats.averageRating % 1 !== 0),
                            'fill-transparent stroke-secondary-text': starIndex > Math.ceil(reviewStats.averageRating) && reviewStats.averageRating % 1 === 0,
                          }"
                        />
                        <div
                          v-if="starIndex === Math.ceil(reviewStats.averageRating) && reviewStats.averageRating % 1 !== 0"
                          class="absolute top-0 left-0 h-full overflow-hidden"
                          :style="{ width: ((reviewStats.averageRating % 1) * 100) + '%' }"
                        >
                          <Star :size="20" class="fill-accent stroke-accent" />
                        </div>
                      </div>
                    </template>
                  </div>
                  <span class="text-xl font-medium text-primary-text">{{ reviewStats.averageRating.toFixed(1) }}</span>
                  <span class="text-secondary-text">({{ reviewStats.totalReviews }} avis)</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Description and reviews section -->
          <div class="w-full px-2 flex flex-col gap-6">
            <!-- Switch button -->
            <div class="bg-background border border-border rounded-full w-full p-1 flex items-center justify-center">
              <button
                @click="showDesc"
                type="button"
                class="rounded-full w-full px-4 py-2 cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 whitespace-nowrap"
                :class="{
                  'bg-accent text-box': activeTab === 'description',
                  'hover:bg-border text-primary-text': activeTab !== 'description'
                }"
              >
                <span>Description</span>
              </button>

              <button
                @click="showReviews"
                type="button"
                class="rounded-full w-full px-4 py-2 cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 whitespace-nowrap"
                :class="{
                  'bg-accent text-box': activeTab === 'reviews',
                  'hover:bg-border text-primary-text': activeTab !== 'reviews'
                }"
              >
                <span>Avis des lecteurs</span>
              </button>
            </div>

            <!-- Description -->
            <div v-if="activeTab === 'description'" class="w-full h-fit flex flex-col gap-3">
              <span class="text-primary-text font-semibold text-xl">Description</span>
              <span v-if="book.desc_txt" class="text-primary-text font-normal text-base">{{ book.desc_txt }}</span>
              <span v-else class="text-secondary-text font-normal text-base italic">Aucune description disponible pour ce livre.</span>
            </div>

            <!-- Reviews -->
            <div v-if="activeTab === 'reviews'" class="w-full h-fit">
              <ReviewList
                :book-id="parseInt(book.id)"
                :reviews="reviews"
                :loading="reviewsLoading"
                @add-review="handleAddReview"
                @update-review="handleUpdateReview"
                @delete-review="handleDeleteReview"
                @edit-review="handleEditReview"
                @refresh-reviews="loadReviews"
                @show-review-editor="handleShowReviewEditor"
              />
            </div>
          </div>
        </template>
      </div>

      <!-- Review editor sidebar -->
      <ReviewForm
        v-if="showReviewEditor"
        :book-id="parseInt(book.id)"
        :existing-review="currentEditingReview"
        @submit="handleReviewSubmit"
        @cancel="closeReviewEditor"
      />
    </main>
  </div>
</template>

<script>
import AppHeader from '@/components/AppHeader.vue';
import MenuBar from '@/components/MenuBar.vue';
import ReviewList from '@/components/ReviewList.vue';
import ReviewForm from '@/components/ReviewForm.vue';
import { ChevronLeft, Plus, BookOpen, Star, PenLine } from 'lucide-vue-next';
import bookService from '@/services/bookService'
import reviewService from '@/services/reviewService';

export default {
  name: 'BookDetail',
  components: {
    AppHeader,
    MenuBar,
    ReviewList,
    ReviewForm,
    ChevronLeft,
    Plus,
    BookOpen,
    Star,
    PenLine,
  },
  data() {
    return {
      book: null,
      loading: true,
      error: null,
      reviewStats: null,
      reviews: [],
      reviewsLoading: false,
      activeTab: 'description',
      showReviewEditor: false,
      currentEditingReview: null
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
        this.book = await bookService.getById(bookId)
        console.log("book fetched by id for details", this.book)
        // Charger les avis
        await this.loadReviews()

      } catch (error) {
        console.error('Erreur lors du chargement du livre:', error)
        this.error = 'Impossible de charger les détails du livre'
      } finally {
        this.loading = false
      }
    },

    async loadReviews() {
      try {
        this.reviewsLoading = true
        const bookId = parseInt(this.$route.params.id)
        this.reviews = await reviewService.getBookReviews(bookId)
        console.log("book reviews", this.reviews)
        // this.reviewStats = reviewService.getReviewsStats(this.reviews)
      } catch (error) {
        console.error('Erreur lors du chargement des avis:', error)
        this.reviews = []
        this.reviewStats = { averageRating: 0, totalReviews: 0 }
      } finally {
        this.reviewsLoading = false
      }
    },

    async handleAddReview(reviewData) {
      try {
        const bookId = parseInt(this.$route.params.id)
        await reviewService.addReview(bookId, reviewData)
        await this.loadReviews() // Recharger les avis
      } catch (error) {
        console.error('Erreur lors de l\'ajout de l\'avis:', error)
        alert('Erreur lors de l\'ajout de votre avis')
      }
    },

    async handleUpdateReview(reviewData) {
      try {
        const bookId = parseInt(this.$route.params.id)
        await reviewService.updateReview(bookId, reviewData)
        await this.loadReviews() // Recharger les avis
      } catch (error) {
        console.error('Erreur lors de la mise à jour de l\'avis:', error)
        alert('Erreur lors de la mise à jour de votre avis')
      }
    },

    async handleDeleteReview(review) {
      try {
        const bookId = parseInt(this.$route.params.id)
        await reviewService.delete(bookId)
        await this.loadReviews() // Recharger les avis
        // Force Vue to update the view
        this.$forceUpdate()
      } catch (error) {
        console.error('Erreur lors de la suppression de l\'avis:', error)
        alert('Erreur lors de la suppression de votre avis')
      }
    },

    getAuthorsNames(book) {
      if (!book.authors || book.authors.length === 0) {
        return 'Auteur inconnu'
      }
      return book.authors.map(author => `${author.firstName} ${author.lastName}`).join(', ')
    },

    showDesc() {
      this.activeTab = 'description'
    },

    showReviews() {
      this.activeTab = 'reviews'
    },

    handleShowReviewEditor() {
      this.currentEditingReview = null
      this.showReviewEditor = true
    },

    handleEditReview(review) {
      this.currentEditingReview = review
      this.showReviewEditor = true
    },

    closeReviewEditor() {
      this.showReviewEditor = false
      this.currentEditingReview = null
    },

    async handleReviewSubmit(reviewData) {
      try {
        if (this.currentEditingReview) {
          // Update existing review
          await this.handleUpdateReview(reviewData)
        } else {
          // Add new review
          await this.handleAddReview(reviewData)
        }
        this.closeReviewEditor()
      } catch (error) {
        console.error('Erreur lors de la soumission de l\'avis:', error)
        alert('Erreur lors de la soumission de votre avis')
      }
    }
  }
}
</script>
