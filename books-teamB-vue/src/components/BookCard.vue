<template>
  <div class="border border-border rounded-2xl flex flex-col gap-2 bg-background overflow-hidden">
    <!-- Cover -->
    <div class="relative aspect-2/3 bg-accent/30 rounded-2xl overflow-hidden">
      <img 
        v-if="coverImage"
        :src="coverImage" 
        :alt="book.title + ' cover'"
        class="w-full h-full object-cover"
      >
      <div v-else class="w-full h-full flex items-center justify-center text-secondary-text">
        <BookOpen class="stroke-secondary-text" :size="50" />
      </div>

      <!-- Status badge -->
      <div 
        v-if="statusLabel" 
        class="absolute top-3 right-3"
      >
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
    <div class="flex flex-col gap-1 px-3 pb-3">
      <span class="text-primary-text font-medium text-base">{{ book.title }}</span>
      <div class="flex items-center gap-2">
        <span v-if="book.year" class="text-primary-text text-xs bg-border px-2 py-0.5 rounded-full">{{ book.year }}</span>
        <span class="text-secondary-text text-sm line-clamp-1" :title="getAuthorsNames(book.authors)">{{ getAuthorsNames(book.authors) }}</span>
      </div>
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
    <div v-if="showActions" class="flex gap-2 mt-4">
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
import { BookOpen, List } from 'lucide-vue-next';
import ProgressBar from './ProgressBar.vue';
import { type } from 'happy-dom/lib/PropertySymbol';

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
      default: '',
    },
    statusLabel: {
      type: String,
      default: '',
    },
    showActions: {
      type: Boolean,
      required: true,
      default: true,
    },
  },
  computed: {
    coverImage() {
      return this.book.coverUrl || this.book.cover;
    }
  },
  methods: {
    getAuthorsNames(authors) {
      if (!authors) return 'Auteur inconnu';
      if (typeof authors === 'string') return authors;
      if (Array.isArray(authors)) {
        return authors.map(author => `${author.firstName} ${author.lastName}`).join(', ');
      }
      return 'Auteur inconnu';
    },
  },
};
</script>