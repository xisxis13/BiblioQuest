<template>
  <div class="review-form bg-white p-6 rounded-lg shadow-lg">
    <h3 class="text-lg font-semibold mb-4">
      {{ isEditing ? 'Modifier votre avis' : 'Donnez votre avis' }}
    </h3>
    
    <form @submit.prevent="submitReview">
      <!-- Système d'étoiles cliquable -->
      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Note *
        </label>
        <div class="flex items-center space-x-1">
          <button
            v-for="star in 5"
            :key="star"
            type="button"
            @click="setRating(star)"
            @mouseover="hoverRating = star"
            @mouseleave="hoverRating = 0"
            class="text-2xl focus:outline-none transition-colors duration-200"
            :class="star <= (hoverRating || rating) ? 'text-yellow-400' : 'text-gray-300'"
          >
            ★
          </button>
          <span class="ml-2 text-sm text-gray-600">
            {{ rating ? `${rating}/5` : 'Sélectionnez une note' }}
          </span>
        </div>
      </div>

      <!-- Zone de texte pour le commentaire -->
      <div class="mb-4">
        <label for="comment" class="block text-sm font-medium text-gray-700 mb-2">
          Commentaire (optionnel)
        </label>
        <textarea
          id="comment"
          v-model="comment"
          rows="4"
          maxlength="1000"
          placeholder="Partagez votre opinion sur ce livre..."
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
        ></textarea>
        <div class="text-xs text-gray-500 mt-1">
          {{ comment.length }}/1000 caractères
        </div>
      </div>

      <!-- Boutons d'action -->
      <div class="flex justify-end space-x-3">
        <button
          type="button"
          @click="cancelReview"
          class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors duration-200"
        >
          Annuler
        </button>
        <button
          type="submit"
          :disabled="!rating || isSubmitting"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
        >
          {{ isSubmitting ? 'Enregistrement...' : 'Enregistrer' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'

const props = defineProps({
  bookId: {
    type: Number,
    required: true
  },
  existingReview: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['review-submitted', 'cancel'])

// État du formulaire
const rating = ref(0)
const comment = ref('')
const hoverRating = ref(0)
const isSubmitting = ref(false)

// Mode édition si un avis existe déjà
const isEditing = computed(() => !!props.existingReview)

// Initialiser le formulaire avec l'avis existant
watch(() => props.existingReview, (review) => {
  if (review) {
    rating.value = review.rating
    comment.value = review.comment || ''
  }
}, { immediate: true })

// Méthodes
const setRating = (star) => {
  rating.value = star
}

const submitReview = async () => {
  if (!rating.value) return
  
  isSubmitting.value = true
  
  try {
    const reviewData = {
      rating: rating.value,
      comment: comment.value.trim() || null
    }
    
    emit('review-submitted', reviewData)
  } finally {
    isSubmitting.value = false
  }
}

const cancelReview = () => {
  // Reset du formulaire
  if (!props.existingReview) {
    rating.value = 0
    comment.value = ''
  } else {
    rating.value = props.existingReview.rating
    comment.value = props.existingReview.comment || ''
  }
  
  emit('cancel')
}
</script>

<style scoped>
.review-form {
  border: 1px solid #e5e7eb;
}
</style>