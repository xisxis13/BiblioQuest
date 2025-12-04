<template>
  <div class="review-card bg-white p-4 rounded-lg border" :class="{ 'ring-2 ring-blue-200 bg-blue-50': review.isOwnReview }">
    <div class="flex items-start space-x-3">
      <!-- Avatar utilisateur -->
      <div class="flex-shrink-0">
        <div class="w-10 h-10 bg-gray-300 rounded-full flex items-center justify-center">
          <span class="text-sm font-medium text-gray-700">
            {{ review.username.charAt(0).toUpperCase() }}
          </span>
        </div>
      </div>
      
      <div class="flex-grow">
        <!-- En-tête avec nom et note -->
        <div class="flex items-center justify-between mb-2">
          <div class="flex items-center space-x-2">
            <h4 class="font-medium text-gray-900">
              {{ review.username }}
              <span v-if="review.isOwnReview" class="text-xs bg-blue-100 text-blue-800 px-2 py-0.5 rounded-full ml-2">
                Votre avis
              </span>
            </h4>
          </div>
          
          <!-- Actions pour son propre avis -->
          <div v-if="review.isOwnReview" class="flex space-x-2">
            <button
              @click="editReview"
              class="text-xs text-blue-600 hover:text-blue-800 font-medium"
            >
              Modifier
            </button>
            <button
              @click="deleteReview"
              class="text-xs text-red-600 hover:text-red-800 font-medium"
            >
              Supprimer
            </button>
          </div>
        </div>
        
        <!-- Étoiles de notation -->
        <div class="flex items-center space-x-1 mb-2">
          <span
            v-for="star in 5"
            :key="star"
            class="text-sm"
            :class="star <= review.rating ? 'text-yellow-400' : 'text-gray-300'"
          >
            ★
          </span>
          <span class="text-sm text-gray-600 ml-2">{{ review.rating }}/5</span>
        </div>
        
        <!-- Commentaire -->
        <div v-if="review.comment" class="mb-2">
          <p class="text-gray-700 text-sm leading-relaxed">{{ review.comment }}</p>
        </div>
        
        <!-- Date de publication -->
        <div class="text-xs text-gray-500">
          {{ formatDate(review.createdAt) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// Composant ReviewCard - Pas d'imports Vue nécessaires

const props = defineProps({
  review: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['edit-review', 'delete-review'])

// Méthodes
const editReview = () => {
  emit('edit-review', props.review)
}

const deleteReview = () => {
  if (confirm('Êtes-vous sûr de vouloir supprimer votre avis ?')) {
    emit('delete-review', props.review)
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  }).format(date)
}
</script>

<style scoped>
.review-card {
  transition: all 0.2s ease-in-out;
}

.review-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>