<template>
  <div class="reviews-list">
    <!-- En-tête avec statistiques -->
    <div class="mb-6">
      <h3 class="text-xl font-semibold text-gray-900 mb-2">
        Avis des lecteurs
        <span v-if="reviews.length > 0" class="text-base text-gray-600 font-normal">
          ({{ reviews.length }} avis)
        </span>
      </h3>
      
      <!-- Note moyenne -->
      <div v-if="reviews.length > 0" class="flex items-center space-x-4 p-4 bg-gray-50 rounded-lg">
        <div class="flex items-center space-x-2">
          <span class="text-2xl font-bold text-gray-900">{{ averageRating.toFixed(1) }}</span>
          <div class="flex items-center">
            <span
              v-for="star in 5"
              :key="star"
              class="text-lg"
              :class="star <= Math.round(averageRating) ? 'text-yellow-400' : 'text-gray-300'"
            >
              ★
            </span>
          </div>
          <span class="text-sm text-gray-600">sur 5</span>
        </div>
        
        <!-- Distribution des notes -->
        <div class="text-sm text-gray-600">
          <div v-for="rating in ratingDistribution" :key="rating.stars" class="flex items-center space-x-2">
            <span>{{ rating.stars }}★</span>
            <div class="w-16 h-2 bg-gray-200 rounded-full overflow-hidden">
              <div 
                class="h-full bg-yellow-400 transition-all duration-300"
                :style="{ width: `${rating.percentage}%` }"
              ></div>
            </div>
            <span class="text-xs">({{ rating.count }})</span>
          </div>
        </div>
      </div>
      
      <!-- Message si aucun avis -->
      <div v-else class="text-center py-8 text-gray-500">
        <p class="mb-2">Aucun avis pour le moment</p>
        <p class="text-sm">Soyez le premier à donner votre opinion !</p>
      </div>
    </div>
    
    <!-- Bouton pour ajouter un avis -->
    <div v-if="!userReview && reviews.length >= 0" class="mb-6">
      <button
        @click="showReviewForm"
        class="w-full py-3 px-4 border-2 border-dashed border-gray-300 rounded-lg text-gray-600 hover:border-blue-400 hover:text-blue-600 transition-colors duration-200"
      >
        ✏️ Donnez votre avis sur ce livre
      </button>
    </div>
    
    <!-- Formulaire d'avis (si affiché) -->
    <div v-if="showForm" class="mb-6">
      <ReviewForm
        :book-id="bookId"
        :existing-review="userReview"
        @review-submitted="handleReviewSubmitted"
        @cancel="showForm = false"
      />
    </div>
    
    <!-- Liste des avis -->
    <div v-if="reviews.length > 0" class="space-y-4">
      <!-- Avis personnel en premier -->
      <div v-if="userReview">
        <h4 class="text-sm font-medium text-gray-700 mb-2">Votre avis</h4>
        <ReviewCard
          :review="userReview"
          @edit-review="editUserReview"
          @delete-review="deleteUserReview"
        />
        
        <!-- Séparateur si il y a d'autres avis -->
        <div v-if="otherReviews.length > 0" class="border-t border-gray-200 mt-6 pt-6">
          <h4 class="text-sm font-medium text-gray-700 mb-4">Autres avis</h4>
        </div>
      </div>
      
      <!-- Autres avis -->
      <div v-for="review in otherReviews" :key="review.id">
        <ReviewCard :review="review" />
      </div>
    </div>
    
    <!-- Loading state -->
    <div v-if="loading" class="text-center py-8">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      <p class="mt-2 text-gray-600">Chargement des avis...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import ReviewCard from './ReviewCard.vue'
import ReviewForm from './ReviewForm.vue'

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

const emit = defineEmits(['add-review', 'update-review', 'delete-review', 'refresh-reviews'])

// État du composant
const showForm = ref(false)

// Computed properties
const userReview = computed(() => {
  return props.reviews.find(review => review.isOwnReview)
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
  showForm.value = true
}

const handleReviewSubmitted = async (reviewData) => {
  try {
    if (userReview.value) {
      await emit('update-review', reviewData)
    } else {
      await emit('add-review', reviewData)
    }
    showForm.value = false
    emit('refresh-reviews')
  } catch (error) {
    console.error('Erreur lors de l\'enregistrement de l\'avis:', error)
  }
}

const editUserReview = () => {
  showForm.value = true
}

const deleteUserReview = async () => {
  try {
    await emit('delete-review')
    emit('refresh-reviews')
  } catch (error) {
    console.error('Erreur lors de la suppression de l\'avis:', error)
  }
}
</script>

<style scoped>
.reviews-list {
  max-width: 100%;
}
</style>