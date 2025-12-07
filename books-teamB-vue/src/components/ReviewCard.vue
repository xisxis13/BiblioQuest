<template>
  <div class="bg-box border border-border rounded-3xl w-full h-fit p-4 flex items-start justify-start gap-4">
    <div class="bg-accent w-10 h-10 aspect-square rounded-full flex items-center justify-center">
      <span class="text-white text-lg font-medium">{{ review.username.charAt(0).toUpperCase() }}</span>
    </div>

    <div class="w-full h-fit flex flex-col items-start justify-start gap-3">
      <div class="w-full flex items-center justify-between">
        <div class="w-full h-fit flex flex-col">
          <span class="text-primary-text font-semibold text-base">{{ review.username || 'Username' }}</span>
          
          <div class="flex items-center gap-6">
            <div class="flex items-center gap-3">
              <div class="flex items-center gap-3">
                <div class="flex items-center gap-1">
                  <template v-for="starIndex in 5" :key="starIndex">
                    <div class="relative">
                      <Star
                        :size="16"
                        :class="{
                          'fill-accent stroke-accent': starIndex <= Math.floor(review.rating),
                          'fill-transparent stroke-accent': starIndex > Math.floor(review.rating)
                        }"
                      />
                      <div
                        v-if="starIndex === Math.ceil(review.rating) && review.rating % 1 !== 0"
                        class="absolute top-0 left-0 h-full overflow-hidden"
                        :style="{ width: ((review.rating % 1) * 100) + '%' }"
                      >
                        <Star :size="16" class="fill-accent stroke-accent" />
                      </div>
                    </div>
                  </template>
                </div>
  
                <span class="text-primary-text text-sm">{{ review.rating }}/5</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Actions for own review only -->
        <div v-if="review.isOwnReview" class="flex gap-2">
          <button
            @click="editReview"
            class="px-2 py-1 rounded-full text-xs text-primary-text hover:bg-border/50 font-medium transition-colors cursor-pointer"
          >
            Modifier
          </button>
          
          <button
            @click="deleteReview"
            class="px-2 py-1 rounded-full text-xs text-red-600 hover:bg-red-100 font-medium transition-colors cursor-pointer"
          >
            Supprimer
          </button>
        </div>
      </div>

      <span v-if="review.comment" class="text-primary-text">{{ review.comment }}</span>

      <span class="text-secondary-text font-normal text-sm">{{ formatDate(review.createdAt) }}</span>
    </div>
  </div>
</template>

<script setup>
import { Star } from 'lucide-vue-next'

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