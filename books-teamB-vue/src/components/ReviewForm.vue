<template>
  <div
    class="bg-box border border-border rounded-3xl w-72 h-full p-5 flex flex-col items-start justify-start gap-4 overflow-y-auto"
  >
    <div class="w-full pb-3 border-b border-border flex items-center justify-between">
      <span class="text-primary-text font-medium text-lg">Rédiger un avis</span>
      <button
        @click="handleCancel"
        class="text-secondary-text hover:text-primary-text transition-colors cursor-pointer"
        type="button"
      >
        <X :size="20" class="stroke-secondary-text hover:stroke-primary-text" />
      </button>
    </div>

    <form @submit.prevent="handleSubmit" class="w-full flex flex-col items-start justify-start gap-4">
      <!-- Star rating system -->
      <div class="w-full flex flex-col gap-2">
        <label class="text-sm font-medium text-primary-text">
          Note <span class="text-red-500">*</span>
        </label>
        <div class="flex items-center gap-2">
          <div class="flex items-center gap-1">
            <button
              v-for="star in 5"
              :key="star"
              type="button"
              @click="rating = star"
              @mouseover="hoverRating = star"
              @mouseleave="hoverRating = 0"
              class="focus:outline-none transition-colors duration-200"
            >
              <Star
                :size="24"
                :class="{
                  'fill-accent stroke-accent': star <= (hoverRating || rating),
                  'fill-transparent stroke-secondary-text': star > (hoverRating || rating)
                }"
              />
            </button>
          </div>
          <span class="text-sm text-secondary-text">
            {{ rating ? `${rating}/5` : '' }}
          </span>
        </div>
      </div>

      <!-- Comment textarea -->
      <div class="w-full flex flex-col gap-2">
        <label for="review-comment" class="text-sm font-medium text-primary-text">
          Commentaire (optionnel)
        </label>
        <textarea
          id="review-comment"
          v-model="comment"
          rows="6"
          maxlength="1000"
          placeholder="Partagez votre opinion sur ce livre..."
          class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm text-primary-text placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent resize-none"
        ></textarea>
        <div class="text-xs text-secondary-text">
          {{ comment.length }}/1000 caractères
        </div>
      </div>

      <!-- Action buttons -->
      <div class="w-full flex flex-col gap-2">
        <button 
          type="submit"
          :disabled="!rating || isSubmitting"
          class="bg-accent hover:bg-accent/90 rounded-2xl w-full px-4 py-3 text-box font-normal cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
        >
          <span>{{ isSubmitting ? 'Enregistrement...' : 'Publier' }}</span>
        </button>
        <button
          type="button"
          @click="handleCancel"
          class="bg-background hover:bg-border rounded-2xl w-full px-4 py-3 text-primary-text font-normal cursor-pointer transition-colors flex items-center justify-center gap-2"
        >
          <span>Annuler</span>
        </button>
      </div>
    </form>
  </div>
</template>

<script>
import { Star, X } from 'lucide-vue-next'

export default {
  name: 'ReviewForm',
  components: {
    Star,
    X
  },
  props: {
    bookId: {
      type: Number,
      required: true
    },
    existingReview: {
      type: Object,
      default: null
    }
  },
  emits: ['submit', 'cancel'],
  data() {
    return {
      rating: 0,
      comment: '',
      hoverRating: 0,
      isSubmitting: false
    }
  },
  watch: {
    existingReview: {
      handler(review) {
        if (review) {
          this.rating = review.rating
          this.comment = review.comment || ''
        }
      },
      immediate: true
    }
  },
  methods: {
    async handleSubmit() {
      if (!this.rating) return
      
      this.isSubmitting = true
      
      try {
        const reviewData = {
          rating: this.rating,
          comment: this.comment.trim() || null
        }
        
        this.$emit('submit', reviewData)
      } catch (error) {
        console.error('Erreur lors de la soumission:', error)
      } finally {
        this.isSubmitting = false
      }
    },

    handleCancel() {
      // Reset form if not editing
      if (!this.existingReview) {
        this.rating = 0
        this.comment = ''
      } else {
        this.rating = this.existingReview.rating
        this.comment = this.existingReview.comment || ''
      }
      
      this.$emit('cancel')
    }
  }
}
</script>
