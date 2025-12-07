<template>
  <div class="border border-border rounded-2xl flex flex-col gap-2 bg-background overflow-hidden cursor-pointer hover:shadow-md transition-all duration-200">
    <!-- Cover -->
    <div class="relative aspect-2/3 bg-accent/30 rounded-2xl overflow-hidden">
      <img 
        v-if="coverImage"
        :src="coverImage" 
        :alt="book.title + ' cover'"
        class="w-full h-full object-cover"
      >
      <div v-else class="w-full h-full p-3 flex flex-col items-center justify-center gap-2">
        <BookOpen class="stroke-secondary-text" :size="50" />
        <span class="text-secondary-text text-base font-normal text-center">Livre sans couverture</span>
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
  
      <!-- Action button -->
      <div v-if="showActions" class="flex gap-1 my-4 p-1 bg-box border border-border rounded-2xl">
        <button
          @click="$emit('update-status', book.id, 'read')"
          :class="{ 'bg-green-500 rounded-xl text-white hover:bg-green-400': book.status === 'read' }"
          class="flex-1 px-3 py-2 hover:rounded-xl hover:bg-green-200 text-sm font-medium transition-all cursor-pointer"
        >
          Lu
        </button>

        <div class="w-px bg-border"></div>

        <button
          @click="$emit('update-status', book.id, 'reading')"
          :class="{ 'bg-accent rounded-xl text-white hover:bg-accent/80': book.status === 'reading' }"
          class="flex-1 px-3 py-2 hover:rounded-xl hover:bg-accent/20 text-sm font-medium transition-all cursor-pointer"
        >
          En cours
        </button>

        <div class="w-px bg-border"></div>

        <button
          @click="$emit('update-status', book.id, 'to-read')"
          :class="{ 'bg-yellow-500 rounded-xl text-white hover:bg-yellow-400': book.status === 'to-read' }"
          class="flex-1 px-3 py-2 hover:rounded-xl hover:bg-accent/20 text-sm font-medium transition-all cursor-pointer"
        >
          À lire
        </button>
      </div>

      <!-- Progress Bar for books in progress -->
      <ProgressBar
        v-if="book.status === 'reading' && book.progress"
        :current-value="book.progress.currentPage"
        :max-value="book.progress.totalPages"
        :show-details="true"
        label=""
      />
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