<template>
  <div class="w-full flex flex-col items-start justify-start gap-6">
    <!-- Header -->
    <span class="text-primary-text font-semibold text-xl">
      Avis des lecteurs
      <span v-if="reviews.length > 0" class="text-secondary-text font-normal text-base">
        ({{ reviews.length }} avis)
      </span>
    </span>

    <!-- Statistics -->
    <div v-if="reviews.length > 0" class="w-full flex items-center gap-8">
      <!-- Average note -->
      <div class="flex items-center gap-4">
        <span class="text-5xl font-bold text-primary-text">{{ averageRating.toFixed(1) }}</span>
        <div class="flex flex-col gap-1">
          <div class="flex gap-1">
            <template v-for="starIndex in 5" :key="starIndex">
              <div class="relative">
                <Star
                  :size="24"
                  :class="{
                    'fill-accent stroke-accent': starIndex <= Math.floor(averageRating),
                    'fill-transparent stroke-secondary-text': starIndex > Math.floor(averageRating)
                  }"
                />
                <div
                  v-if="starIndex === Math.ceil(averageRating) && averageRating % 1 !== 0"
                  class="absolute top-0 left-0 h-full overflow-hidden"
                  :style="{ width: ((averageRating % 1) * 100) + '%' }"
                >
                  <Star :size="24" class="fill-accent stroke-accent" />
                </div>
              </div>
            </template>
          </div>
          <span class="text-sm text-secondary-text">sur 5</span>
        </div>
      </div>

      <!-- Rating distribution -->
      <div class="flex-1 flex flex-col gap-1">
        <div v-for="rating in ratingDistribution" :key="rating.stars" class="flex items-center gap-3">
          <div class="flex items-center justify-start gap-1">
            <span class="text-sm font-medium text-primary-text text-center w-3">{{ rating.stars }}</span>
            <Star :size="16" class="fill-primary-text stroke-primary-text" />
          </div>
          <div class="flex-1 h-2 bg-border rounded-full overflow-hidden">
            <div
              class="h-full bg-accent transition-all duration-300 rounded-full"
              :style="{ width: `${rating.percentage}%` }"
            ></div>
          </div>
          <span class="text-sm text-secondary-text">({{ rating.count }})</span>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="w-full py-4 flex flex-col items-center justify-center gap-2">
      <p class="text-base text-secondary-text">Aucun avis pour le moment</p>
      <p class="text-sm text-secondary-text">Soyez le premier à donner votre opinion !</p>
    </div>

    <!-- Add review button -->
    <button
      @click="showReviewForm"
      class="w-full py-3 px-4 flex items-center justify-center gap-2 border-2 border-dashed border-secondary-text rounded-2xl hover:border-accent transition-colors duration-200 cursor-pointer"
    >
      <PenLine :size="16" class="stroke-primary-text" />
      <span class="text-primary-text font-normal">Donnez votre avis sur ce livre</span>
    </button>

    <!-- Review List -->
    <div v-if="reviews.length > 0" class="flex flex-col gap-5">
      <!-- User's own review (if exists) -->
      <div v-if="userReview" class="flex flex-col items-start justify-start gap-3">
        <span class="w-full text-primary-text font-medium text-sm">Votre avis</span>
        <ReviewItem
          :review="userReview"
          @edit-review="editUserReview"
          @delete-review="deleteUserReview"
        />
      </div>

      <!-- Other reviews -->
      <div v-if="otherReviews.length > 0" class="flex flex-col items-start justify-start gap-3">
        <span v-if="userReview" class="w-full text-primary-text font-medium text-sm border-t border-border pt-3">Autres avis</span>
        <ReviewItem
          v-for="review in otherReviews"
          :key="review.id"
          :review="review"
        />
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="w-full h-full flex items-center justify-center flex-col gap-3">
      <svg class="animate-spin h-10 w-10 text-secondary-text" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
      <p class="text-secondary-text">Chargement des avis...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Star, PenLine } from 'lucide-vue-next'
import ReviewItem from './ReviewCard.vue'
import { useAuthStore } from '@/stores/authStore'

const props = defineProps({
  bookId: {
    type: Number,
    required: true
  },
  reviews: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const authStore = useAuthStore()
const emit = defineEmits(['add-review', 'update-review', 'delete-review', 'refresh-reviews', 'show-review-editor'])

// Computed properties
const userReview = computed(() => {
  console.log("coucou du userReview")
  //TODO adapter le isOwnReview
  const userId = authStore.getUser().id
  return props.reviews.find(review => review.userBookDTO.userId === userId)
})

const otherReviews = computed(() => {
  return props.reviews.filter(review => !review.isOwnReview)
})

const averageRating = computed(() => {
  if (props.reviews.length === 0) return 0
  const sum = props.reviews.reduce((acc, review) => acc + review.rating, 0)
  return sum / props.reviews.length
})

const ratingDistribution = computed(() => {
  const distribution = [5, 4, 3, 2, 1].map(stars => ({
    stars,
    count: props.reviews.filter(review => review.rating === stars).length,
    percentage: 0
  }))

  const total = props.reviews.length
  if (total > 0) {
    distribution.forEach(item => {
      item.percentage = (item.count / total) * 100
    })
  }

  return distribution
})

// Méthodes
const showReviewForm = () => {
  emit('show-review-editor')
}

const editUserReview = (review) => {
  // Emit event to parent to show editor with existing review data
  emit('edit-review', review)
}

const deleteUserReview = (review) => {
  // Just emit the event, parent will handle deletion and refresh
  emit('delete-review', review)
}
</script>
