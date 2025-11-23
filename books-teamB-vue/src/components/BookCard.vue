<template>
  <div class="flex flex-col gap-2">
    <!-- Cover -->
    <div class="relative aspect-2/3 bg-accent/20 rounded-3xl overflow-hidden">
      <img 
        v-if="book.coverUrl"
        :src="book.coverUrl" 
        :alt="title + ' cover'"
        class="w-full h-full object-cover"
      >
      <div v-else class="w-full h-full flex items-center justify-center text-secondary-text">
        <BookOpen class="stroke-secondary-text" :size="50" />
      </div>

      <!-- Status badge -->
      <div class="absolute top-3 right-3">
        <span
          :class="[
            'px-3 py-1 rounded-full text-xs font-medium',
            statusClass,
          ]"
        >
          {{ statusLabel }}
        </span>
      </div>
    </div>

    <!-- Book infos -->
    <div class="flex flex-col px-1">
      <span class="text-primary-text font-medium text-lg">{{ book.title }}</span>
      <span class="text-secondary-text text-sm">{{ book.author }}</span>
    </div>

    <!-- Progress Bar for books in progress -->
    <ProgressBar
      v-if="book.status === 'reading' && book.progress"
      :current-value="book.progress.currentPage"
      :max-value="book.progress.totalPages"
      :show-details="true"
      label=""
    />

    <!-- Action button -->
    <div class="flex gap-2 mt-4">
      <button
        @click="$emit('update-status', book.id, 'read')"
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
        @click="$emit('update-status', book.id, 'reading')"
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
        @click="$emit('update-status', book.id, 'to-read')"
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
</template>

<script>
import { BookOpen } from 'lucide-vue-next';
import ProgressBar from './ProgressBar.vue';

export default {
  name: 'BookCard',
  components: {
    BookOpen,
    ProgressBar,
  },
  emits: ['update-status'],
  props: {
    book: {
      type: Object,
      required: true
    },
    statusClass: {
      type: String,
      required: true,
    },
    statusLabel: {
      type: String,
      required: true,
    },
  },
};
</script>